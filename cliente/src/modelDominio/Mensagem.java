package modelDominio;

import java.io.Serializable;
import java.util.Date;

public class Mensagem implements Serializable {

    private static final long serialVersionUID = 123L;
    private int id;
    private String conteudo;
    private Date dataEnvio;
    private Usuario usuario;
    private Projeto projeto;

    public Mensagem(int id, String conteudo, Date dataEnvio, Usuario usuario, Projeto projeto) {
        this.id = id;
        this.conteudo = conteudo;
        this.dataEnvio = dataEnvio;
        this.usuario = usuario;
        this.projeto = projeto;
    }

    public Mensagem(String conteudo, Usuario usuario, Projeto projeto) {
        this.conteudo = conteudo;
        this.usuario = usuario;
        this.projeto = projeto;
    }

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

    @Override
    public String toString() {
        return "Mensagem{" + "id=" + id + ", usuario=" + (usuario != null ? usuario.getNome() : null) + ", projeto=" + (projeto != null ? projeto.getId() : null) + "}";
    }
}