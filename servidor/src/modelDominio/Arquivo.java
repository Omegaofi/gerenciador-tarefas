package modelDominio;

import java.io.Serializable;
import java.util.Date;

// Arquivo enviado em uma tarefa (tabela arquivo). Viaja pelo socket entre cliente e servidor.
public class Arquivo implements Serializable {

    private static final long serialVersionUID = 125L;
    private int id;
    private String nomeArquivo;
    private String arquivoUrl;
    private Usuario usuario;
    private Tarefa tarefa;
    private Date criadoEm;

    // Construtor completo: usado ao ler um arquivo do banco
    public Arquivo(int id, String nomeArquivo, String arquivoUrl, Usuario usuario, Tarefa tarefa, Date criadoEm) {
        this.id = id;
        this.nomeArquivo = nomeArquivo;
        this.arquivoUrl = arquivoUrl;
        this.usuario = usuario;
        this.tarefa = tarefa;
        this.criadoEm = criadoEm;
    }

    // Construtor sem id: usado para cadastrar um arquivo novo
    public Arquivo(String nomeArquivo, String arquivoUrl, Usuario usuario, Tarefa tarefa) {
        this.nomeArquivo = nomeArquivo;
        this.arquivoUrl = arquivoUrl;
        this.usuario = usuario;
        this.tarefa = tarefa;
    }

    // Getters e setters
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

    // Texto usado para mostrar o objeto no console/depuração
    @Override
    public String toString() {
        return "Arquivo{" + "id=" + id + ", nomeArquivo=" + nomeArquivo + "}";
    }
}