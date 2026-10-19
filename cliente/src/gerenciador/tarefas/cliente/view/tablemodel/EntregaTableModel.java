package gerenciador.tarefas.cliente.view.tablemodel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import javax.swing.table.AbstractTableModel;
import modelDominio.EntregaTarefa;

public class EntregaTableModel extends AbstractTableModel {

    public static final int COLUNA_STATUS = 4;
    private ArrayList<EntregaTarefa> lista;
    private final SimpleDateFormat sdf;

    public EntregaTableModel(ArrayList<EntregaTarefa> listaEntrega) {
        this.lista = listaEntrega != null ? listaEntrega : new ArrayList<>();
        sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    }

    @Override
    public int getRowCount() {
        return lista.size();
    }

    @Override
    public int getColumnCount() {
        return 5;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        EntregaTarefa entrega = lista.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return entrega.getTarefa() != null ? entrega.getTarefa().getTitulo() : "";
            case 1:
                return entrega.getTarefa() != null && entrega.getTarefa().getProjeto() != null
                        ? entrega.getTarefa().getProjeto().getTitulo() : "";
            case 2:
                return entrega.getTarefa() != null && entrega.getTarefa().getUsuario() != null
                        ? entrega.getTarefa().getUsuario().getNome() : "";
            case 3:
                return entrega.getDataEntrega() != null ? sdf.format(entrega.getDataEntrega()) : "";
            case 4:
                return entrega.getStatus();
            default:
                return "erro";
        }
    }

    public EntregaTarefa getEntrega(int linha) {
        return lista.get(linha);
    }

    @Override
    public String getColumnName(int column) {
        switch (column) {
            case 0:
                return "Tarefa";
            case 1:
                return "Projeto";
            case 2:
                return "Colaborador";
            case 3:
                return "Enviada em";
            case 4:
                return "Status";
            default:
                return "erro";
        }
    }
}
