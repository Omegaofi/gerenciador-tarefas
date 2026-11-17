package gerenciador.tarefas.cliente.view.tablemodel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import javax.swing.table.AbstractTableModel;
import modelDominio.Inscricao;

/** Inscrições pendentes nos projetos do criador (tela Inscrições). */
public class InscricaoTableModel extends AbstractTableModel {

    private static final String[] COLUNAS = {"Projeto", "Participante", "E-mail", "Inscrito em"};
    private final SimpleDateFormat formatoData = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    private final ArrayList<Inscricao> lista;

    public InscricaoTableModel(ArrayList<Inscricao> lista) {
        this.lista = lista != null ? lista : new ArrayList<>();
    }

    @Override
    public int getRowCount() {
        return lista.size();
    }

    @Override
    public int getColumnCount() {
        return COLUNAS.length;
    }

    @Override
    public String getColumnName(int column) {
        return COLUNAS[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Inscricao inscricao = lista.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return inscricao.getProjeto().getTitulo();
            case 1:
                return inscricao.getUsuario().getNome();
            case 2:
                return inscricao.getUsuario().getEmail();
            case 3:
                return inscricao.getDataInscricao() != null ? formatoData.format(inscricao.getDataInscricao()) : "";
            default:
                return "";
        }
    }

    public Inscricao getInscricao(int linha) {
        return lista.get(linha);
    }
}
