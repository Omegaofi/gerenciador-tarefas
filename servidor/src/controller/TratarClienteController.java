package controller;

import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.net.Socket;
import model.UsuarioDao;
import model.ProjetoDao;
import model.MensagemDao;
import model.TarefaDao;
import model.EntregaTarefaDao;
import model.InscricaoDao;
import modelDominio.Usuario;
import modelDominio.Projeto;
import modelDominio.Mensagem;
import modelDominio.Tarefa;
import modelDominio.EntregaTarefa;

/**
 * Atende um cliente (desktop ou Android) conectado pelo socket.
 *
 * <p>Regras de segurança:</p>
 * <ul>
 * <li>Só UsuarioLogin e UsuarioInserir funcionam sem login. Os outros comandos devolvem
 * null/false enquanto ninguém fez login nesta conexão.</li>
 * <li>O servidor nunca usa um id de usuário mandado pelo cliente: usa sempre o usuário logado.
 * Os comandos *PorCriador e *PorResponsavel ainda recebem um id (para o protocolo continuar
 * o mesmo nos dois clientes), mas esse id é ignorado.</li>
 * <li>Alterar ou excluir só funciona no que é do usuário logado (conferido no próprio SQL).</li>
 * </ul>
 *
 * <p>Mesmo quando recusa, o servidor responde "ok", lê os parâmetros e devolve a resposta,
 * para o cliente e o servidor não se perderem no meio do protocolo.</p>
 */
public class TratarClienteController extends Thread {

    private Socket cliente;
    private ObjectInput in;
    private ObjectOutput out;
    private int idUnico;
    // Quem fez login nesta conexão. Todas as operações usam este usuário,
    // e não o usuario_id que o cliente manda.
    private Usuario usuarioLogado;

    // Prepara os canais de entrada e saída de objetos com o cliente que conectou
    public TratarClienteController(Socket cliente, int idUnico) {
        this.cliente = cliente;
        this.idUnico = idUnico;
        try {
            this.in = new ObjectInputStream(cliente.getInputStream());
            this.out = new ObjectOutputStream(cliente.getOutputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Laço principal: lê um comando do cliente, executa no DAO certo e devolve a resposta, até ele sair
    @Override
    public void run() {
        String comando;
        boolean res;

        System.out.println("Aguardando comando do cliente:" + idUnico);
        try {
            comando = (String) in.readObject();
            while (!comando.equalsIgnoreCase("fim")) {
                System.out.println("Cliente " + idUnico + " enviou comando: " + comando);
                switch (comando) {

                    // ---------- Usuário (sem login) ----------
                    // Confere e-mail + SHA-256 da senha com o SHA-256 do banco; se bater, guarda quem logou
                    case "UsuarioLogin":
                        out.writeObject("ok");
                        Usuario user = (Usuario) in.readObject();
                        System.out.println("Login: " + (user == null ? null : user.getEmail()));
                        usuarioLogado = user == null ? null : new UsuarioDao().login(user);
                        out.writeObject(usuarioLogado);
                        break;
                    // Cadastra um novo usuário (só os perfis do documento)
                    case "UsuarioInserir":
                        out.writeObject("ok");
                        Usuario u = (Usuario) in.readObject();
                        System.out.println(u);
                        res = u != null && ("criador".equals(u.getTipo()) || "participante".equals(u.getTipo()))
                                && new UsuarioDao().inserir(u);
                        out.writeObject(res);
                        break;

                    // ---------- Projetos ----------
                    // Projetos criados pelo usuário logado (o id enviado é ignorado)
                    case "ProjetoListaPorCriador":
                        out.writeObject("ok");
                        in.readObject();
                        out.writeObject(logado() ? new ProjetoDao().getListaPorCriador(idLogado()) : null);
                        break;
                    // Só um Criador cria projeto, e o dono é sempre ele
                    case "ProjetoInserir":
                        out.writeObject("ok");
                        Projeto p = (Projeto) in.readObject();
                        System.out.println(p);
                        res = criadorLogado() && p != null && new ProjetoDao().inserir(p, idLogado());
                        out.writeObject(res);
                        break;
                    // Atualiza um projeto do usuário logado (o dono não muda)
                    case "ProjetoAtualizar":
                        out.writeObject("ok");
                        p = (Projeto) in.readObject();
                        System.out.println(p);
                        res = logado() && p != null && new ProjetoDao().atualizar(p, idLogado());
                        out.writeObject(res);
                        break;
                    // Exclui um projeto do usuário logado
                    case "ProjetoExcluir":
                        out.writeObject("ok");
                        p = (Projeto) in.readObject();
                        System.out.println(p);
                        res = logado() && p != null && new ProjetoDao().excluir(p.getId(), idLogado());
                        out.writeObject(res);
                        break;

                    // ---------- Tarefas ----------
                    // Tarefas atribuídas ao usuário logado (o id enviado é ignorado)
                    case "TarefaListaPorResponsavel":
                        out.writeObject("ok");
                        in.readObject();
                        out.writeObject(logado() ? new TarefaDao().getListaPorResponsavel(idLogado()) : null);
                        break;
                    // Tarefas dos projetos do usuário logado (o id enviado é ignorado)
                    case "TarefaListaPorCriador":
                        out.writeObject("ok");
                        in.readObject();
                        out.writeObject(logado() ? new TarefaDao().getListaPorCriador(idLogado()) : null);
                        break;
                    // Cria uma tarefa: só o dono do projeto, e só para um membro do projeto
                    case "TarefaInserir":
                        out.writeObject("ok");
                        Tarefa t = (Tarefa) in.readObject();
                        System.out.println(t);
                        res = responsavelValido(t) && new TarefaDao().inserir(t);
                        out.writeObject(res);
                        break;
                    // Atualiza uma tarefa que já é de um projeto do usuário logado
                    case "TarefaAtualizar":
                        out.writeObject("ok");
                        t = (Tarefa) in.readObject();
                        System.out.println(t);
                        res = responsavelValido(t) && new TarefaDao().atualizar(t, idLogado());
                        out.writeObject(res);
                        break;
                    // Exclui uma tarefa de um projeto do usuário logado
                    case "TarefaExcluir":
                        out.writeObject("ok");
                        t = (Tarefa) in.readObject();
                        System.out.println(t);
                        res = logado() && t != null && new TarefaDao().excluir(t.getId(), idLogado());
                        out.writeObject(res);
                        break;

                    // ---------- Entregas ----------
                    // Entregas dos projetos do usuário logado (o id enviado é ignorado)
                    case "EntregaTarefaListaPorCriador":
                        out.writeObject("ok");
                        in.readObject();
                        out.writeObject(logado() ? new EntregaTarefaDao().getListaPorCriador(idLogado()) : null);
                        break;
                    // Entregas feitas pelo usuário logado (o id enviado é ignorado)
                    case "EntregaTarefaListaPorResponsavel":
                        out.writeObject("ok");
                        in.readObject();
                        out.writeObject(logado() ? new EntregaTarefaDao().getListaPorResponsavel(idLogado()) : null);
                        break;
                    // Responsável envia a entrega de uma tarefa dele
                    case "EntregaTarefaEnviar":
                        out.writeObject("ok");
                        EntregaTarefa entregaEnviar = (EntregaTarefa) in.readObject();
                        System.out.println(entregaEnviar);
                        res = logado() && entregaEnviar != null
                                && new EntregaTarefaDao().enviar(entregaEnviar, idLogado());
                        out.writeObject(res);
                        break;
                    // Dono do projeto aprova ou rejeita uma entrega
                    case "EntregaTarefaAvaliar":
                        out.writeObject("ok");
                        EntregaTarefa entregaAvaliar = (EntregaTarefa) in.readObject();
                        System.out.println(entregaAvaliar);
                        res = logado() && entregaAvaliar != null
                                && new EntregaTarefaDao().avaliar(entregaAvaliar, idLogado());
                        out.writeObject(res);
                        break;

                    // ---------- Inscrições em projetos ----------
                    // Projetos abertos em que o usuário logado ainda não participa
                    case "InscricaoProjetosDisponiveis":
                        out.writeObject("ok");
                        out.writeObject(logado() ? new InscricaoDao().projetosDisponiveis(idLogado()) : null);
                        break;
                    // Usuário logado pede para entrar em um projeto
                    case "InscricaoInscrever":
                        out.writeObject("ok");
                        int projetoInscricaoId = (Integer) in.readObject();
                        res = logado() && new InscricaoDao().inscrever(projetoInscricaoId, idLogado());
                        out.writeObject(res);
                        break;
                    // Pedidos de entrada pendentes nos projetos do criador logado
                    case "InscricaoPendentesCriador":
                        out.writeObject("ok");
                        out.writeObject(logado() ? new InscricaoDao().pendentesDoCriador(idLogado()) : null);
                        break;
                    // Criador aprova ou recusa um pedido de entrada
                    case "InscricaoResponder":
                        out.writeObject("ok");
                        int projetoRespostaId = (Integer) in.readObject();
                        int candidatoId = (Integer) in.readObject();
                        boolean aceitar = (Boolean) in.readObject();
                        res = logado() && new InscricaoDao()
                                .responder(projetoRespostaId, candidatoId, idLogado(), aceitar);
                        out.writeObject(res);
                        break;
                    // Membros de um projeto do criador logado
                    case "ProjetoParticipantes":
                        out.writeObject("ok");
                        int projetoParticipantesId = (Integer) in.readObject();
                        out.writeObject(logado()
                                ? new InscricaoDao().participantesDoProjeto(projetoParticipantesId, idLogado()) : null);
                        break;
                    // Projetos em que o usuário logado participa
                    case "ProjetoListaParticipando":
                        out.writeObject("ok");
                        out.writeObject(logado() ? new InscricaoDao().projetosParticipando(idLogado()) : null);
                        break;
                    // Diz se o usuário logado já participa de algum projeto
                    case "UsuarioParticipaDeProjeto":
                        out.writeObject("ok");
                        res = logado() && new InscricaoDao().participaDeAlgumProjeto(idLogado());
                        out.writeObject(res);
                        break;

                    // ---------- Senha e chat cifrado ----------
                    // Troca a senha do usuário logado (confere a senha atual antes)
                    case "UsuarioTrocarSenha":
                        out.writeObject("ok");
                        String shaSenhaAtual = (String) in.readObject();
                        Usuario novosDados = (Usuario) in.readObject();
                        res = logado() && new UsuarioDao().trocarSenha(idLogado(), shaSenhaAtual, novosDados);
                        System.out.println("Troca de senha do usuário "
                                + (logado() ? idLogado() : "?") + ": " + (res ? "ok" : "recusada"));
                        out.writeObject(res);
                        break;
                    // Grava as chaves do chat cifrado do usuário logado (só na primeira vez)
                    case "UsuarioSalvarChaves":
                        out.writeObject("ok");
                        Usuario chaves = (Usuario) in.readObject();
                        res = logado() && chaves != null && new UsuarioDao().salvarChaves(idLogado(), chaves);
                        if (res) {
                            usuarioLogado.setChavePublica(chaves.getChavePublica());
                        }
                        out.writeObject(res);
                        break;
                    // Membros do projeto com a chave pública de cada um, para cifrar mensagens
                    case "ProjetoMembrosChaves":
                        out.writeObject("ok");
                        int projetoMembrosId = (Integer) in.readObject();
                        MensagemDao membrosDao = new MensagemDao();
                        boolean membro = logado() && membrosDao.isMembro(projetoMembrosId, idLogado());
                        out.writeObject(membro ? membrosDao.membrosComChaves(projetoMembrosId) : null);
                        break;
                    // Grava uma mensagem cifrada junto com a cópia da chave para cada membro
                    case "MensagemInserirCifrada":
                        out.writeObject("ok");
                        Mensagem cifrada = (Mensagem) in.readObject();
                        System.out.println(cifrada + " com "
                                + (cifrada == null || cifrada.getChaves() == null ? 0 : cifrada.getChaves().size())
                                + " copias de K");
                        res = logado() && cifrada != null && cifrada.getProjeto() != null
                                && new MensagemDao().inserirCifrada(cifrada, idLogado());
                        out.writeObject(res);
                        break;
                    // Mensagens cifradas de um projeto, com a chave do usuário logado
                    case "MensagemListaPorProjeto":
                        out.writeObject("ok");
                        int projetoChatId = (Integer) in.readObject();
                        int depoisDoId = (Integer) in.readObject();
                        MensagemDao chatDao = new MensagemDao();
                        if (logado() && chatDao.isMembro(projetoChatId, idLogado())) {
                            out.writeObject(chatDao.listaPorProjeto(projetoChatId, depoisDoId, idLogado()));
                        } else {
                            out.writeObject(null);   // CT07: quem não é membro não recebe nada
                        }
                        break;

                    default:
                        System.out.println("Comando inválido");
                }
                comando = (String) in.readObject();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // Alguém fez login nesta conexão?
    private boolean logado() {
        return usuarioLogado != null;
    }

    // Id do usuário logado (só chamar depois de conferir logado())
    private int idLogado() {
        return usuarioLogado.getId();
    }

    // O usuário logado tem o perfil Criador (o tipo vem do banco, no login)
    private boolean criadorLogado() {
        return logado() && "criador".equals(usuarioLogado.getTipo());
    }

    /** A tarefa precisa ser de um projeto do usuário logado e ir para um membro desse projeto. */
    private boolean responsavelValido(Tarefa t) {
        if (!logado() || t == null || t.getProjeto() == null || t.getUsuario() == null) {
            return false;
        }
        boolean ok = new InscricaoDao().podeReceberTarefa(t.getProjeto().getId(), t.getUsuario().getId(), idLogado());
        if (!ok) {
            System.out.println("Tarefa recusada: responsável " + t.getUsuario().getId()
                    + " não é membro do projeto " + t.getProjeto().getId());
        }
        return ok;
    }
}
