package gerenciador.tarefas.cliente.view.tablemodel;

import java.util.ArrayList;
import javax.swing.table.AbstractTableModel;
import modelDominio.Projeto;

public class ProjetoTableModel extends AbstractTableModel {

    public static final int COLUNA_STATUS = 3;
    private ArrayList<Projeto> lista;

    public ProjetoTableModel(ArrayList<Projeto> listaProjeto) {
        this.lista = listaProjeto != null ? listaProjeto : new ArrayList<>();
    }

    @Override
    public int getRowCount() {
        return lista.size();
    }

    @Override
    public int getColumnCount() {
        return 4;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Projeto projeto = lista.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return projeto.getTitulo();
            case 1:
                return projeto.getAreaAtuacao() != null ? projeto.getAreaAtuacao() : "";
            case 2:
                return projeto.getDescricao() != null ? projeto.getDescricao() : "";
            case 3:
                return projeto.getStatus();
            default:
                return "erro";
        }
    }

    public Projeto getProjeto(int linha) {
        return lista.get(linha);
    }

    @Override
    public String getColumnName(int column) {
        switch (column) {
            case 0:
                return "Título";
            case 1:
                return "Área de atuação";
            case 2:
                return "Descrição";
            case 3:
                return "Status";
            default:
                return "erro";
        }
    }
}
