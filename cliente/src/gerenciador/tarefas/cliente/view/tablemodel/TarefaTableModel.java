package gerenciador.tarefas.cliente.view.tablemodel;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import javax.swing.table.AbstractTableModel;
import modelDominio.Tarefa;

public class TarefaTableModel extends AbstractTableModel {

    public static final int COLUNA_STATUS = 4;
    private ArrayList<Tarefa> lista;
    private final SimpleDateFormat sdf;

    // Recebe a lista de tarefas que a tabela vai mostrar
    public TarefaTableModel(ArrayList<Tarefa> listaTarefa) {
        this.lista = listaTarefa != null ? listaTarefa : new ArrayList<>();
        sdf = new SimpleDateFormat("dd/MM/yyyy");
    }

    /**
     * Status mostrado na tela: uma tarefa pendente ou em andamento com o prazo
     * vencido aparece como "atrasada".
     */
    public static String statusExibido(Tarefa tarefa) {
        String status = tarefa.getStatus();
        boolean aberta = "pendente".equals(status) || "em_andamento".equals(status);
        if (aberta && tarefa.getPrazo() != null) {
            LocalDate prazo = new java.sql.Date(tarefa.getPrazo().getTime()).toLocalDate();
            if (prazo.isBefore(LocalDate.now(ZoneId.systemDefault()))) {
                return "atrasada";
            }
        }
        return status;
    }

    // Quantidade de linhas = quantidade de tarefas
    @Override
    public int getRowCount() {
        return lista.size();
    }

    @Override
    public int getColumnCount() {
        return 5;
    }

    // Valor de cada célula: título, prazo, status, projeto...
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Tarefa tarefa = lista.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return tarefa.getTitulo();
            case 1:
                return tarefa.getProjeto() != null ? tarefa.getProjeto().getTitulo() : "";
            case 2:
                return tarefa.getUsuario() != null ? tarefa.getUsuario().getNome() : "";
            case 3:
                return tarefa.getPrazo() != null ? sdf.format(tarefa.getPrazo()) : "";
            case 4:
                return statusExibido(tarefa);
            default:
                return "erro";
        }
    }

    // Tarefa da linha selecionada
    public Tarefa getTarefa(int linha) {
        return lista.get(linha);
    }

    // Nome de cada coluna
    @Override
    public String getColumnName(int column) {
        switch (column) {
            case 0:
                return "Título";
            case 1:
                return "Projeto";
            case 2:
                return "Responsável";
            case 3:
                return "Prazo";
            case 4:
                return "Status";
            default:
                return "erro";
        }
    }
}
