package modelDominio;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

// Mensagem do chat de um projeto (tabela mensagem). Viaja pelo socket.
public class Mensagem implements Serializable {

    private static final long serialVersionUID = 123L;
    private int id;
    private String conteudo;
    private Date dataEnvio;
    private Usuario usuario;
    private Projeto projeto;
    // Chat cifrado: conteudo passa a ser AES-GCM(K, iv, texto) em Base64
    private String iv;
    private ArrayList<ChaveMensagem> chaves;        // no envio: uma cópia de K por membro
    private ChaveMensagem minhaChave;               // na leitura: só a cópia do usuário logado

    // Construtor completo: usado ao ler uma mensagem do banco
    public Mensagem(int id, String conteudo, Date dataEnvio, Usuario usuario, Projeto projeto) {
        this.id = id;
        this.conteudo = conteudo;
        this.dataEnvio = dataEnvio;
        this.usuario = usuario;
        this.projeto = projeto;
    }

    /** Mensagem cifrada pronta para MensagemInserirCifrada. */
    public Mensagem(String conteudo, String iv, Usuario usuario, Projeto projeto, ArrayList<ChaveMensagem> chaves) {
        this.conteudo = conteudo;
        this.iv = iv;
        this.usuario = usuario;
        this.projeto = projeto;
        this.chaves = chaves;
    }

    // Construtor sem id: usado para enviar uma mensagem nova
    public Mensagem(String conteudo, Usuario usuario, Projeto projeto) {
        this.conteudo = conteudo;
        this.usuario = usuario;
        this.projeto = projeto;
    }

    // Getters e setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getConteudo() { return conteudo; }
    public void setConteudo(String conteudo) { this.conteudo = conteudo; }
    public Date getDataEnvio() { return dataEnvio; }
    public void setDataEnvio(Date dataEnvio) { this.dataEnvio = dataEnvio; }
    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
    public Projeto getProjeto() { return projeto; }
    public void setProjeto(Projeto projeto) { this.projeto = projeto; }
    public String getIv() { return iv; }
    public void setIv(String iv) { this.iv = iv; }
    public ArrayList<ChaveMensagem> getChaves() { return chaves; }
    public void setChaves(ArrayList<ChaveMensagem> chaves) { this.chaves = chaves; }
    public ChaveMensagem getMinhaChave() { return minhaChave; }
    public void setMinhaChave(ChaveMensagem minhaChave) { this.minhaChave = minhaChave; }

    // Texto usado para mostrar o objeto no console/depuração
    @Override
    public String toString() {
        return "Mensagem{" + "id=" + id + ", usuario=" + (usuario != null ? usuario.getNome() : null) + ", projeto=" + (projeto != null ? projeto.getId() : null) + "}";
    }
}