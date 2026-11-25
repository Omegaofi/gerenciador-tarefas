package modelDominio;

import java.io.Serializable;
import java.util.Date;

public class Arquivo implements Serializable {

    private static final long serialVersionUID = 125L;
    private int id;
    private String nomeArquivo;
    private String arquivoUrl;
    private Usuario usuario;
    private Tarefa tarefa;
    private Date criadoEm;

    public Arquivo(int id, String nomeArquivo, String arquivoUrl, Usuario usuario, Tarefa tarefa, Date criadoEm) {
        this.id = id;
        this.nomeArquivo = nomeArquivo;
        this.arquivoUrl = arquivoUrl;
        this.usuario = usuario;
        this.tarefa = tarefa;
        this.criadoEm = criadoEm;
    }

    public Arquivo(String nomeArquivo, String arquivoUrl, Usuario usuario, Tarefa tarefa) {
        this.nomeArquivo = nomeArquivo;
        this.arquivoUrl = arquivoUrl;
        this.usuario = usuario;
        this.tarefa = tarefa;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNomeArquivo() { return nomeArquivo; }
    public void setNomeArquivo(String nomeArquivo) { this.nomeArquivo = nomeArquivo; }
    public String getArquivoUrl() { return arquivoUrl; }
    public void setArquivoUrl(String arquivoUrl) { this.arquivoUrl = arquivoUrl; }
    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
    public Tarefa getTarefa() { return tarefa; }
    public void setTarefa(Tarefa tarefa) { this.tarefa = tarefa; }
    public Date getCriadoEm() { return criadoEm; }
    public void setCriadoEm(Date criadoEm) { this.criadoEm = criadoEm; }

    @Override
    public String toString() {
        return "Arquivo{" + "id=" + id + ", nomeArquivo=" + nomeArquivo + "}";
    }
}