package modelDominio;

import java.io.Serializable;
import java.util.Date;

public class Tarefa implements Serializable {

    private static final long serialVersionUID = 124L;
    private int id;
    private String titulo;
    private Date prazo;
    private String status;
    private Projeto projeto;
    private Usuario usuario;
    private Date criadoEm;

    public Tarefa(int id, String titulo, Date prazo, String status, Projeto projeto, Usuario usuario, Date criadoEm) {
        this.id = id;
        this.titulo = titulo;
        this.prazo = prazo;
        this.status = status;
        this.projeto = projeto;
        this.usuario = usuario;
        this.criadoEm = criadoEm;
    }

    public Tarefa(String titulo, Date prazo, String status, Projeto projeto, Usuario usuario) {
        this.titulo = titulo;
        this.prazo = prazo;
        this.status = status;
        this.projeto = projeto;
        this.usuario = usuario;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public Date getPrazo() { return prazo; }
    public void setPrazo(Date prazo) { this.prazo = prazo; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Projeto getProjeto() { return projeto; }
    public void setProjeto(Projeto projeto) { this.projeto = projeto; }
    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
    public Date getCriadoEm() { return criadoEm; }
    public void setCriadoEm(Date criadoEm) { this.criadoEm = criadoEm; }

    @Override
    public String toString() {
        return "Tarefa{" + "id=" + id + ", titulo=" + titulo + ", status=" + status + "}";
    }
}