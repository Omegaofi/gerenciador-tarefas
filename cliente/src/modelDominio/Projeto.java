package modelDominio;

import java.io.Serializable;
import java.util.Date;

// Projeto criado por um usuário criador (tabela projeto). Viaja pelo socket.
public class Projeto implements Serializable {

    private static final long serialVersionUID = 122L;
    private int id;
    private String titulo;
    private String descricao;
    private String areaAtuacao;
    private String status;
    private Usuario usuario;
    private Date criadoEm;

    // Construtor completo: usado ao ler um projeto do banco
    public Projeto(int id, String titulo, String descricao, String areaAtuacao, String status, Usuario usuario, Date criadoEm) {
        this.id = id;
        this.titulo = titulo;
        this.descricao = descricao;
        this.areaAtuacao = areaAtuacao;
        this.status = status;
        this.usuario = usuario;
        this.criadoEm = criadoEm;
    }

    // Construtor sem id: usado para cadastrar um projeto novo
    public Projeto(String titulo, String descricao, String areaAtuacao, String status, Usuario usuario) {
        this.titulo = titulo;
        this.descricao = descricao;
        this.areaAtuacao = areaAtuacao;
        this.status = status;
        this.usuario = usuario;
    }

    // Construtor resumido (só id e título), usado dentro de tarefas e entregas
    public Projeto(int id, String titulo) {
        this.id = id;
        this.titulo = titulo;
    }

    // Getters e setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getAreaAtuacao() {
        return areaAtuacao;
    }

    public void setAreaAtuacao(String areaAtuacao) {
        this.areaAtuacao = areaAtuacao;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Date getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Date criadoEm) {
        this.criadoEm = criadoEm;
    }

    // Texto usado para mostrar o objeto no console/depuração
    @Override
    public String toString() {
        return "Projeto{" + "id=" + id + ", titulo=" + titulo + ", status=" + status + ", dono=" + (usuario != null ? usuario.getNome() : null) + "}";
    }
}
