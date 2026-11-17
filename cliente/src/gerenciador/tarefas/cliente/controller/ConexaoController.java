package gerenciador.tarefas.cliente.controller;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import gerenciador.tarefas.cliente.cripto.ChatCifrado;
import java.util.ArrayList;
import modelDominio.Arquivo;
import modelDominio.EntregaTarefa;
import modelDominio.Inscricao;
import modelDominio.Mensagem;
import modelDominio.Projeto;
import modelDominio.Tarefa;
import modelDominio.Usuario;

public class ConexaoController {

    private final ObjectInputStream in;
    private final ObjectOutputStream out;
    private Usuario usuarioLogado;
    private final ChatCifrado chatCifrado;

    public ConexaoController(ObjectInputStream in, ObjectOutputStream out) {
        this.in = in;
        this.out = out;
        this.chatCifrado = new ChatCifrado(this);
    }

    /** Chaves do chat do usuário logado (abertas no login). */
    public ChatCifrado getChatCifrado() {
        return chatCifrado;
    }

    public Usuario getUsuarioLogado() {
        return usuarioLogado;
    }

    public void setUsuarioLogado(Usuario usuarioLogado) {
        this.usuarioLogado = usuarioLogado;
        if (usuarioLogado == null) {
            chatCifrado.fechar();   // logout: a chave privada sai da memória
        }
    }

    // Criador = idealizador do projeto; Participante = colaborador
    public boolean isCriador() {
        return usuarioLogado != null && "criador".equals(usuarioLogado.getTipo());
    }

    // Envia um comando que recebe o id de um usuário e devolve uma lista
    private <T> ArrayList<T> listaPorUsuario(String comando, int usuarioId) {
        try {
            out.writeObject(comando);
            in.readObject();
            out.writeObject(usuarioId);
            ArrayList<T> lista = (ArrayList<T>) in.readObject();
            return lista != null ? lista : new ArrayList<>();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public ArrayList<Projeto> projetoListaPorCriador(int criadorId) {
        return listaPorUsuario("ProjetoListaPorCriador", criadorId);
    }

    public ArrayList<Tarefa> tarefaListaPorResponsavel(int usuarioId) {
        return listaPorUsuario("TarefaListaPorResponsavel", usuarioId);
    }

    public ArrayList<Tarefa> tarefaListaPorCriador(int criadorId) {
        return listaPorUsuario("TarefaListaPorCriador", criadorId);
    }

    public ArrayList<EntregaTarefa> entregaTarefaListaPorCriador(int criadorId) {
        return listaPorUsuario("EntregaTarefaListaPorCriador", criadorId);
    }

    public ArrayList<EntregaTarefa> entregaTarefaListaPorResponsavel(int usuarioId) {
        return listaPorUsuario("EntregaTarefaListaPorResponsavel", usuarioId);
    }

    public boolean entregaTarefaEnviar(EntregaTarefa entrega) {
        try {
            out.writeObject("EntregaTarefaEnviar");
            in.readObject();
            out.writeObject(entrega);
            return (boolean) in.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean entregaTarefaAvaliar(EntregaTarefa entrega) {
        try {
            out.writeObject("EntregaTarefaAvaliar");
            in.readObject();
            out.writeObject(entrega);
            return (boolean) in.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public Usuario usuarioLogin(Usuario usuario) {
        try {
            out.writeObject("UsuarioLogin");
            in.readObject();
            out.writeObject(usuario);
            return (Usuario) in.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public ArrayList<Usuario> usuarioLista() {
        try {
            out.writeObject("UsuarioLista");
            in.readObject();
            ArrayList<Usuario> lista = (ArrayList<Usuario>) in.readObject();
            return lista != null ? lista : new ArrayList<>();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public boolean usuarioInserir(Usuario usuario) {
        try {
            out.writeObject("UsuarioInserir");
            in.readObject();
            out.writeObject(usuario);
            return (boolean) in.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean usuarioAtualizar(Usuario usuario) {
        try {
            out.writeObject("UsuarioAtualizar");
            in.readObject();
            out.writeObject(usuario);
            return (boolean) in.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean usuarioExcluir(Usuario usuario) {
        try {
            out.writeObject("UsuarioExcluir");
            in.readObject();
            out.writeObject(usuario);
            return (boolean) in.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public ArrayList<Projeto> projetoLista() {
        try {
            out.writeObject("ProjetoLista");
            in.readObject();
            ArrayList<Projeto> lista = (ArrayList<Projeto>) in.readObject();
            return lista != null ? lista : new ArrayList<>();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public boolean projetoInserir(Projeto projeto) {
        try {
            out.writeObject("ProjetoInserir");
            in.readObject();
            out.writeObject(projeto);
            return (boolean) in.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean projetoAtualizar(Projeto projeto) {
        try {
            out.writeObject("ProjetoAtualizar");
            in.readObject();
            out.writeObject(projeto);
            return (boolean) in.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean projetoExcluir(Projeto projeto) {
        try {
            out.writeObject("ProjetoExcluir");
            in.readObject();
            out.writeObject(projeto);
            return (boolean) in.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public ArrayList<Mensagem> mensagemLista() {
        try {
            out.writeObject("MensagemLista");
            in.readObject();
            ArrayList<Mensagem> lista = (ArrayList<Mensagem>) in.readObject();
            return lista != null ? lista : new ArrayList<>();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public boolean mensagemInserir(Mensagem mensagem) {
        try {
            out.writeObject("MensagemInserir");
            in.readObject();
            out.writeObject(mensagem);
            return (boolean) in.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean mensagemAtualizar(Mensagem mensagem) {
        try {
            out.writeObject("MensagemAtualizar");
            in.readObject();
            out.writeObject(mensagem);
            return (boolean) in.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean mensagemExcluir(Mensagem mensagem) {
        try {
            out.writeObject("MensagemExcluir");
            in.readObject();
            out.writeObject(mensagem);
            return (boolean) in.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public ArrayList<Tarefa> tarefaLista() {
        try {
            out.writeObject("TarefaLista");
            in.readObject();
            ArrayList<Tarefa> lista = (ArrayList<Tarefa>) in.readObject();
            return lista != null ? lista : new ArrayList<>();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public boolean tarefaInserir(Tarefa tarefa) {
        try {
            out.writeObject("TarefaInserir");
            in.readObject();
            out.writeObject(tarefa);
            return (boolean) in.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean tarefaAtualizar(Tarefa tarefa) {
        try {
            out.writeObject("TarefaAtualizar");
            in.readObject();
            out.writeObject(tarefa);
            return (boolean) in.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean tarefaExcluir(Tarefa tarefa) {
        try {
            out.writeObject("TarefaExcluir");
            in.readObject();
            out.writeObject(tarefa);
            return (boolean) in.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public ArrayList<Arquivo> arquivoLista() {
        try {
            out.writeObject("ArquivoLista");
            in.readObject();
            ArrayList<Arquivo> lista = (ArrayList<Arquivo>) in.readObject();
            return lista != null ? lista : new ArrayList<>();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public boolean arquivoInserir(Arquivo arquivo) {
        try {
            out.writeObject("ArquivoInserir");
            in.readObject();
            out.writeObject(arquivo);
            return (boolean) in.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean arquivoAtualizar(Arquivo arquivo) {
        try {
            out.writeObject("ArquivoAtualizar");
            in.readObject();
            out.writeObject(arquivo);
            return (boolean) in.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean arquivoExcluir(Arquivo arquivo) {
        try {
            out.writeObject("ArquivoExcluir");
            in.readObject();
            out.writeObject(arquivo);
            return (boolean) in.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public ArrayList<EntregaTarefa> entregaTarefaLista() {
        try {
            out.writeObject("EntregaTarefaLista");
            in.readObject();
            ArrayList<EntregaTarefa> lista = (ArrayList<EntregaTarefa>) in.readObject();
            return lista != null ? lista : new ArrayList<>();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public boolean entregaTarefaInserir(EntregaTarefa entrega) {
        try {
            out.writeObject("EntregaTarefaInserir");
            in.readObject();
            out.writeObject(entrega);
            return (boolean) in.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean entregaTarefaAtualizar(EntregaTarefa entrega) {
        try {
            out.writeObject("EntregaTarefaAtualizar");
            in.readObject();
            out.writeObject(entrega);
            return (boolean) in.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean entregaTarefaExcluir(EntregaTarefa entrega) {
        try {
            out.writeObject("EntregaTarefaExcluir");
            in.readObject();
            out.writeObject(entrega);
            return (boolean) in.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // ---------- Inscrições em projetos ----------

    /** Projetos em que o usuário logado pode se inscrever. Inscricao.status null = ainda não inscrito. */
    public ArrayList<Inscricao> inscricaoProjetosDisponiveis() {
        try {
            out.writeObject("InscricaoProjetosDisponiveis");
            in.readObject();
            ArrayList<Inscricao> lista = (ArrayList<Inscricao>) in.readObject();
            return lista != null ? lista : new ArrayList<>();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public boolean inscricaoInscrever(int projetoId) {
        try {
            out.writeObject("InscricaoInscrever");
            in.readObject();
            out.writeObject(projetoId);
            return (boolean) in.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /** Inscrições pendentes nos projetos do criador logado. */
    public ArrayList<Inscricao> inscricaoPendentesCriador() {
        try {
            out.writeObject("InscricaoPendentesCriador");
            in.readObject();
            ArrayList<Inscricao> lista = (ArrayList<Inscricao>) in.readObject();
            return lista != null ? lista : new ArrayList<>();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /** O criador logado aceita ou recusa a inscrição do usuário no projeto. */
    public boolean inscricaoResponder(int projetoId, int usuarioId, boolean aceitar) {
        try {
            out.writeObject("InscricaoResponder");
            in.readObject();
            out.writeObject(projetoId);
            out.writeObject(usuarioId);
            out.writeObject(aceitar);
            return (boolean) in.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /** Quem pode receber tarefa no projeto (participantes aceitos). Vazio se o projeto não for do logado. */
    public ArrayList<Usuario> projetoParticipantes(int projetoId) {
        try {
            out.writeObject("ProjetoParticipantes");
            in.readObject();
            out.writeObject(projetoId);
            ArrayList<Usuario> lista = (ArrayList<Usuario>) in.readObject();
            return lista != null ? lista : new ArrayList<>();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /** Projetos em que o usuário logado foi aceito. */
    public ArrayList<Projeto> projetoListaParticipando() {
        try {
            out.writeObject("ProjetoListaParticipando");
            in.readObject();
            ArrayList<Projeto> lista = (ArrayList<Projeto>) in.readObject();
            return lista != null ? lista : new ArrayList<>();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /** True se o usuário logado já participa de algum projeto. */
    public boolean usuarioParticipaDeProjeto() {
        try {
            out.writeObject("UsuarioParticipaDeProjeto");
            in.readObject();
            return (boolean) in.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // ---------- Chat cifrado ----------

    /**
     * Login com o hashLogin (PBKDF2 da senha) no lugar da senha.
     * O servidor guarda o usuário logado nesta conexão.
     */
    public Usuario usuarioLoginHash(String email, String hashLogin) {
        return usuarioLogin(new Usuario(email, hashLogin));
    }

    /** Conta antiga: troca o BCrypt(senha) guardado por BCrypt(hashLogin). Só para o usuário logado. */
    public boolean usuarioMigrarLogin(String novoHashGuardado) {
        try {
            out.writeObject("UsuarioMigrarLogin");
            in.readObject();
            out.writeObject(novoHashGuardado);
            return (boolean) in.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Troca a senha do usuário logado.
     *
     * @param hashAtual PBKDF2(senha atual, e-mail): o servidor confere antes de trocar
     * @param novosDados senha = BCrypt(PBKDF2(senha nova)) e a chave privada trancada com a senha nova
     */
    public boolean usuarioTrocarSenha(String hashAtual, Usuario novosDados) {
        try {
            out.writeObject("UsuarioTrocarSenha");
            in.readObject();
            out.writeObject(hashAtual);
            out.writeObject(novosDados);
            return (boolean) in.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /** Envia a pública e a privada trancada. O servidor só aceita se o usuário ainda não tiver chaves. */
    public boolean usuarioSalvarChaves(Usuario chaves) {
        try {
            out.writeObject("UsuarioSalvarChaves");
            in.readObject();
            out.writeObject(chaves);
            return (boolean) in.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /** Membros do projeto (criador + participantes) com a chave pública. Null se o logado não for membro. */
    public ArrayList<Usuario> projetoMembrosChaves(int projetoId) {
        try {
            out.writeObject("ProjetoMembrosChaves");
            in.readObject();
            out.writeObject(projetoId);
            return (ArrayList<Usuario>) in.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean mensagemInserirCifrada(Mensagem mensagem) {
        try {
            out.writeObject("MensagemInserirCifrada");
            in.readObject();
            out.writeObject(mensagem);
            return (boolean) in.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /** Mensagens do projeto com id &gt; depoisDoId, cada uma só com a cópia de K do usuário logado. */
    public ArrayList<Mensagem> mensagemListaPorProjeto(int projetoId, int depoisDoId) {
        try {
            out.writeObject("MensagemListaPorProjeto");
            in.readObject();
            out.writeObject(projetoId);
            out.writeObject(depoisDoId);
            ArrayList<Mensagem> lista = (ArrayList<Mensagem>) in.readObject();
            return lista != null ? lista : new ArrayList<>();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}
