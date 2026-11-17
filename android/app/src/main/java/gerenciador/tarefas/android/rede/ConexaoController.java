package gerenciador.tarefas.android.rede;

import gerenciador.tarefas.android.cripto.ChatCifrado;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import modelDominio.Arquivo;
import modelDominio.EntregaTarefa;
import modelDominio.Inscricao;
import modelDominio.Mensagem;
import modelDominio.Projeto;
import modelDominio.Tarefa;
import modelDominio.Usuario;

/**
 * Mesmo protocolo do cliente desktop (gerenciador-tarefas-cliente):
 * envia o nome do comando, lê o "ok" do servidor, envia os parâmetros e lê a resposta.
 *
 * <p>Nunca chame estes métodos na thread principal do Android: use {@link Rede#rodar}.
 * O Rede usa uma única thread, então os comandos nunca se misturam no socket.</p>
 */
public class ConexaoController {

    private final Socket socket;
    private final ObjectInputStream in;
    private final ObjectOutputStream out;
    private Usuario usuarioLogado;
    private final ChatCifrado chatCifrado;
    private volatile boolean perdida;

    public ConexaoController(Socket socket) throws IOException {
        this.socket = socket;
        // Mesma ordem do desktop: primeiro o ObjectOutputStream, depois o ObjectInputStream
        this.out = new ObjectOutputStream(socket.getOutputStream());
        this.out.flush();
        this.in = new ObjectInputStream(socket.getInputStream());
        this.chatCifrado = new ChatCifrado(this);
    }

    /** True depois que o socket caiu (servidor fechado, Wi-Fi caiu...). */
    public boolean isPerdida() {
        return perdida;
    }

    /** Avisa o servidor ("fim") e fecha o socket. */
    public void fechar() {
        try {
            if (!perdida) {
                out.writeObject("fim");
                out.flush();
            }
        } catch (IOException ignorado) {
            // já estava fechado
        }
        try {
            socket.close();
        } catch (IOException ignorado) {
            // nada a fazer
        }
        perdida = true;
        chatCifrado.fechar();
    }

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

    // ---------- Núcleo do protocolo ----------

    /**
     * Envia um comando com seus parâmetros e devolve a resposta do servidor.
     * Em caso de erro devolve null (como o desktop, que imprimia e seguia em frente).
     */
    private synchronized Object comando(String nome, Object... parametros) {
        if (perdida) {
            return null;
        }
        try {
            out.reset();   // não reaproveita objetos já enviados (o servidor veria a versão antiga)
            out.writeObject(nome);
            out.flush();
            in.readObject();   // "ok"
            for (Object p : parametros) {
                out.writeObject(p);
            }
            out.flush();
            return in.readObject();
        } catch (IOException e) {
            e.printStackTrace();
            perdida = true;
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private boolean bool(String nome, Object... parametros) {
        Object r = comando(nome, parametros);
        return r instanceof Boolean && (Boolean) r;
    }

    @SuppressWarnings("unchecked")
    private <T> ArrayList<T> lista(String nome, Object... parametros) {
        Object r = comando(nome, parametros);
        return r instanceof ArrayList ? (ArrayList<T>) r : new ArrayList<>();
    }

    // ---------- Usuário ----------

    public Usuario usuarioLogin(Usuario usuario) {
        Object r = comando("UsuarioLogin", usuario);
        return r instanceof Usuario ? (Usuario) r : null;
    }

    /** Login com o hashLogin (PBKDF2 da senha) no lugar da senha. */
    public Usuario usuarioLoginHash(String email, String hashLogin) {
        return usuarioLogin(new Usuario(email, hashLogin));
    }

    public ArrayList<Usuario> usuarioLista() { return lista("UsuarioLista"); }
    public boolean usuarioInserir(Usuario usuario) { return bool("UsuarioInserir", usuario); }
    public boolean usuarioAtualizar(Usuario usuario) { return bool("UsuarioAtualizar", usuario); }
    public boolean usuarioExcluir(Usuario usuario) { return bool("UsuarioExcluir", usuario); }

    /** Conta antiga: troca o BCrypt(senha) guardado por BCrypt(hashLogin). Só para o usuário logado. */
    public boolean usuarioMigrarLogin(String novoHashGuardado) {
        return bool("UsuarioMigrarLogin", novoHashGuardado);
    }

    public boolean usuarioTrocarSenha(String hashAtual, Usuario novosDados) {
        return bool("UsuarioTrocarSenha", hashAtual, novosDados);
    }

    public boolean usuarioSalvarChaves(Usuario chaves) {
        return bool("UsuarioSalvarChaves", chaves);
    }

    public boolean usuarioParticipaDeProjeto() {
        return bool("UsuarioParticipaDeProjeto");
    }

    // ---------- Projetos ----------

    public ArrayList<Projeto> projetoLista() { return lista("ProjetoLista"); }
    public ArrayList<Projeto> projetoListaPorCriador(int criadorId) { return lista("ProjetoListaPorCriador", criadorId); }
    public ArrayList<Projeto> projetoListaParticipando() { return lista("ProjetoListaParticipando"); }
    public boolean projetoInserir(Projeto projeto) { return bool("ProjetoInserir", projeto); }
    public boolean projetoAtualizar(Projeto projeto) { return bool("ProjetoAtualizar", projeto); }
    public boolean projetoExcluir(Projeto projeto) { return bool("ProjetoExcluir", projeto); }
    public ArrayList<Usuario> projetoParticipantes(int projetoId) { return lista("ProjetoParticipantes", projetoId); }

    /** Membros do projeto com a chave pública. Null se o logado não for membro. */
    @SuppressWarnings("unchecked")
    public ArrayList<Usuario> projetoMembrosChaves(int projetoId) {
        Object r = comando("ProjetoMembrosChaves", projetoId);
        return r instanceof ArrayList ? (ArrayList<Usuario>) r : null;
    }

    // ---------- Tarefas ----------

    public ArrayList<Tarefa> tarefaLista() { return lista("TarefaLista"); }
    public ArrayList<Tarefa> tarefaListaPorResponsavel(int usuarioId) { return lista("TarefaListaPorResponsavel", usuarioId); }
    public ArrayList<Tarefa> tarefaListaPorCriador(int criadorId) { return lista("TarefaListaPorCriador", criadorId); }
    public boolean tarefaInserir(Tarefa tarefa) { return bool("TarefaInserir", tarefa); }
    public boolean tarefaAtualizar(Tarefa tarefa) { return bool("TarefaAtualizar", tarefa); }
    public boolean tarefaExcluir(Tarefa tarefa) { return bool("TarefaExcluir", tarefa); }

    // ---------- Entregas ----------

    public ArrayList<EntregaTarefa> entregaTarefaLista() { return lista("EntregaTarefaLista"); }
    public ArrayList<EntregaTarefa> entregaTarefaListaPorCriador(int criadorId) { return lista("EntregaTarefaListaPorCriador", criadorId); }
    public ArrayList<EntregaTarefa> entregaTarefaListaPorResponsavel(int usuarioId) { return lista("EntregaTarefaListaPorResponsavel", usuarioId); }
    public boolean entregaTarefaEnviar(EntregaTarefa entrega) { return bool("EntregaTarefaEnviar", entrega); }
    public boolean entregaTarefaAvaliar(EntregaTarefa entrega) { return bool("EntregaTarefaAvaliar", entrega); }
    public boolean entregaTarefaInserir(EntregaTarefa entrega) { return bool("EntregaTarefaInserir", entrega); }
    public boolean entregaTarefaAtualizar(EntregaTarefa entrega) { return bool("EntregaTarefaAtualizar", entrega); }
    public boolean entregaTarefaExcluir(EntregaTarefa entrega) { return bool("EntregaTarefaExcluir", entrega); }

    // ---------- Arquivos ----------

    public ArrayList<Arquivo> arquivoLista() { return lista("ArquivoLista"); }
    public boolean arquivoInserir(Arquivo arquivo) { return bool("ArquivoInserir", arquivo); }
    public boolean arquivoAtualizar(Arquivo arquivo) { return bool("ArquivoAtualizar", arquivo); }
    public boolean arquivoExcluir(Arquivo arquivo) { return bool("ArquivoExcluir", arquivo); }

    // ---------- Inscrições ----------

    /** Projetos em que o usuário logado pode se inscrever. Inscricao.status null = ainda não inscrito. */
    public ArrayList<Inscricao> inscricaoProjetosDisponiveis() { return lista("InscricaoProjetosDisponiveis"); }
    public boolean inscricaoInscrever(int projetoId) { return bool("InscricaoInscrever", projetoId); }
    public ArrayList<Inscricao> inscricaoPendentesCriador() { return lista("InscricaoPendentesCriador"); }

    public boolean inscricaoResponder(int projetoId, int usuarioId, boolean aceitar) {
        return bool("InscricaoResponder", projetoId, usuarioId, aceitar);
    }

    // ---------- Chat cifrado ----------

    public ArrayList<Mensagem> mensagemLista() { return lista("MensagemLista"); }
    public boolean mensagemInserir(Mensagem mensagem) { return bool("MensagemInserir", mensagem); }
    public boolean mensagemInserirCifrada(Mensagem mensagem) { return bool("MensagemInserirCifrada", mensagem); }

    /** Mensagens do projeto com id &gt; depoisDoId, cada uma só com a cópia de K do usuário logado. */
    public ArrayList<Mensagem> mensagemListaPorProjeto(int projetoId, int depoisDoId) {
        return lista("MensagemListaPorProjeto", projetoId, depoisDoId);
    }
}
