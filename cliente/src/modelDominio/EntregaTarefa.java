package modelDominio;

import java.io.Serializable;
import java.util.Date;

// Entrega de uma tarefa feita pelo participante (tabela entregatarefa). Viaja pelo socket.
public class EntregaTarefa implements Serializable {

    private static final long serialVersionUID = 126L;
    private int id;
    private Date dataEntrega;
    private String status;
    private Tarefa tarefa;
    private Arquivo arquivo;

    // Construtor completo: usado ao ler uma entrega do banco
    public EntregaTarefa(int id, Date dataEntrega, String status, Tarefa tarefa, Arquivo arquivo) {
        this.id = id;
        this.dataEntrega = dataEntrega;
        this.status = status;
        this.tarefa = tarefa;
        this.arquivo = arquivo;
    }

    // Construtor sem id: usado para cadastrar uma entrega nova
    public EntregaTarefa(Date dataEntrega, String status, Tarefa tarefa, Arquivo arquivo) {
        this.dataEntrega = dataEntrega;
        this.status = status;
        this.tarefa = tarefa;
        this.arquivo = arquivo;
    }

    // Getters e setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getDataEntrega() {
        return dataEntrega;
    }

    public void setDataEntrega(Date dataEntrega) {
        this.dataEntrega = dataEntrega;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Tarefa getTarefa() {
        return tarefa;
    }

    public void setTarefa(Tarefa tarefa) {
        this.tarefa = tarefa;
    }

    public Arquivo getArquivo() {
        return arquivo;
    }

    public void setArquivo(Arquivo arquivo) {
        this.arquivo = arquivo;
    }

    // Texto usado para mostrar o objeto no console/depuração
    @Override
    public String toString() {
        return "EntregaTarefa{" + "id=" + id + ", status=" + status + ", tarefa=" + (tarefa != null ? tarefa.getTitulo() : null) + "}";
    }
}
