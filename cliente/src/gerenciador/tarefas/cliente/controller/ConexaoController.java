package gerenciador.tarefas.cliente.controller;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import gerenciador.tarefas.cliente.cripto.ChatCifrado;
import java.util.ArrayList;
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

    // Guarda os canais de comunicação com o servidor e cria o controle do chat cifrado
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

    // Define quem está logado; no logout (null) fecha as chaves do chat
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

    // Envia um comando que recebe o id de um usuário e devolve uma lista.
    // O servidor ignora esse id e usa sempre o usuário logado na conexão.
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

    // Projetos criados pelo usuário informado
    public ArrayList<Projeto> projetoListaPorCriador(int criadorId) {
        return listaPorUsuario("ProjetoListaPorCriador", criadorId);
    }

    // Tarefas atribuídas ao usuário informado
    public ArrayList<Tarefa> tarefaListaPorResponsavel(int usuarioId) {
        return listaPorUsuario("TarefaListaPorResponsavel", usuarioId);
    }

    // Tarefas dos projetos de um criador
    public ArrayList<Tarefa> tarefaListaPorCriador(int criadorId) {
        return listaPorUsuario("TarefaListaPorCriador", criadorId);
    }

    // Entregas dos projetos de um criador
    public ArrayList<EntregaTarefa> entregaTarefaListaPorCriador(int criadorId) {
        return listaPorUsuario("EntregaTarefaListaPorCriador", criadorId);
    }

    // Entregas feitas pelo participante informado
    public ArrayList<EntregaTarefa> entregaTarefaListaPorResponsavel(int usuarioId) {
        return listaPorUsuario("EntregaTarefaListaPorResponsavel", usuarioId);
    }

    // Participante envia a entrega de uma tarefa
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

    // Criador avalia (aprova/reprova) uma entrega
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

    // Manda e-mail + senha criptografada ao servidor; devolve o usuário ou null se errar
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

    // Cadastra um usuário novo
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

    // Cria um projeto
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

    // Atualiza um projeto
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

    // Exclui um projeto
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

    // Cria uma tarefa
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

    // Atualiza uma tarefa
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

    // Exclui uma tarefa
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

    // Usuário logado pede para entrar em um projeto
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
     * Login com o SHA-256 da senha no lugar da senha (calculado na TelaLogin1).
     * O servidor guarda o usuário logado nesta conexão.
     */
    public Usuario usuarioLoginSha(String email, String shaSenha) {
        return usuarioLogin(new Usuario(email, shaSenha));
    }

    /**
     * Troca a senha do usuário logado.
     *
     * @param shaAtual SHA-256(senha atual): o servidor confere antes de trocar
     * @param novosDados senha = SHA-256(senha nova) e a chave privada trancada com a senha nova
     */
    public boolean usuarioTrocarSenha(String shaAtual, Usuario novosDados) {
        try {
            out.writeObject("UsuarioTrocarSenha");
            in.readObject();
            out.writeObject(shaAtual);
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

    // Envia uma mensagem já cifrada, com a cópia da chave para cada membro
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
