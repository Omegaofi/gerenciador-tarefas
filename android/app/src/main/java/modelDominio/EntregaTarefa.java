package modelDominio;

import java.io.Serializable;
import java.util.Date;

public class EntregaTarefa implements Serializable {

    private static final long serialVersionUID = 126L;
    private int id;
    private Date dataEntrega;
    private String status;
    private Tarefa tarefa;
    private Arquivo arquivo;

    public EntregaTarefa(int id, Date dataEntrega, String status, Tarefa tarefa, Arquivo arquivo) {
        this.id = id;
        this.dataEntrega = dataEntrega;
        this.status = status;
        this.tarefa = tarefa;
        this.arquivo = arquivo;
    }

    public EntregaTarefa(Date dataEntrega, String status, Tarefa tarefa, Arquivo arquivo) {
        this.dataEntrega = dataEntrega;
        this.status = status;
        this.tarefa = tarefa;
        this.arquivo = arquivo;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public Date getDataEntrega() { return dataEntrega; }
    public void setDataEntrega(Date dataEntrega) { this.dataEntrega = dataEntrega; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Tarefa getTarefa() { return tarefa; }
    public void setTarefa(Tarefa tarefa) { this.tarefa = tarefa; }
    public Arquivo getArquivo() { return arquivo; }
    public void setArquivo(Arquivo arquivo) { this.arquivo = arquivo; }

    @Override
    public String toString() {
        return "EntregaTarefa{" + "id=" + id + ", status=" + status + ", tarefa=" + (tarefa != null ? tarefa.getTitulo() : null) + "}";
    }
}