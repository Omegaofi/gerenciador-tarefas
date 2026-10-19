package gerenciador.tarefas.cliente.view;

import gerenciador.tarefas.cliente.GerenciadorTarefasCliente;
import gerenciador.tarefas.cliente.controller.ConexaoController;
import gerenciador.tarefas.cliente.view.tablemodel.EntregaTableModel;
import gerenciador.tarefas.cliente.view.tema.Tema;
import java.awt.BorderLayout;
import java.awt.Desktop;
import java.net.URI;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import modelDominio.EntregaTarefa;

/**
 * Criador: revisa as entregas das tarefas dos seus projetos (aprovar/rejeitar).
 * Participante: acompanha o resultado das entregas que enviou.
 */
public class TabelaEntrega extends TelaBase {

    private final ConexaoController conexao = GerenciadorTarefasCliente.conexaoCliente;
    private final boolean criador = conexao.isCriador();
    private final JTable jTable1 = new JTable();
    private EntregaTableModel entregaModel;

    public TabelaEntrega() {
        super("Entregas", ENTREGAS);

        conteudo.add(cabecalho(criador ? "Entregas" : "Minhas entregas",
                criador ? "Revise o que os colaboradores entregaram" : "Acompanhe a revisão das suas entregas",
                null), BorderLayout.NORTH);

        atualizarTabelaEntregas();
        conteudo.add(tabelaEstilizada(jTable1, EntregaTableModel.COLUNA_STATUS), BorderLayout.CENTER);

        JButton botaoAbrir = Tema.botaoSecundario("Abrir link");
        botaoAbrir.addActionListener(e -> abrirLink());
        if (criador) {
            JButton botaoAprovar = Tema.botaoDestaque("Aprovar");
            botaoAprovar.addActionListener(e -> avaliar("aprovada"));
            JButton botaoRejeitar = Tema.botaoPerigo("Rejeitar");
            botaoRejeitar.addActionListener(e -> avaliar("rejeitada"));
            conteudo.add(barraAcoes(botaoAprovar, botaoRejeitar, botaoAbrir), BorderLayout.SOUTH);
        } else {
            conteudo.add(barraAcoes(botaoAbrir), BorderLayout.SOUTH);
        }
    }

    private void atualizarTabelaEntregas() {
        int usuarioId = conexao.getUsuarioLogado().getId();
        ArrayList<EntregaTarefa> lista = criador
                ? conexao.entregaTarefaListaPorCriador(usuarioId)
                : conexao.entregaTarefaListaPorResponsavel(usuarioId);
        entregaModel = new EntregaTableModel(lista);
        jTable1.setModel(entregaModel);
        jTable1.getColumnModel().getColumn(EntregaTableModel.COLUNA_STATUS).setCellRenderer(Tema.rendererStatus());
    }

    private EntregaTarefa selecionada() {
        int linha = jTable1.getSelectedRow();
        if (linha < 0) {
            JOptionPane.showMessageDialog(this, "Selecione uma entrega.");
            return null;
        }
        return entregaModel.getEntrega(linha);
    }

    private void avaliar(String novoStatus) {
        EntregaTarefa entrega = selecionada();
        if (entrega == null) {
            return;
        }
        if (!"enviada".equals(entrega.getStatus()) && !"em_revisao".equals(entrega.getStatus())) {
            JOptionPane.showMessageDialog(this, "Esta entrega já foi " + Tema.rotuloStatus(entrega.getStatus()).toLowerCase() + ".");
            return;
        }
        String acao = "aprovada".equals(novoStatus)
                ? "Aprovar a entrega? A tarefa será marcada como concluída."
                : "Rejeitar a entrega? A tarefa volta para \"em andamento\" para o colaborador refazer.";
        int resposta = JOptionPane.showConfirmDialog(this, acao, "Revisar entrega", JOptionPane.YES_NO_OPTION);
        if (resposta != JOptionPane.YES_OPTION) {
            return;
        }
        entrega.setStatus(novoStatus);
        if (conexao.entregaTarefaAvaliar(entrega)) {
            atualizarTabelaEntregas();
        } else {
            JOptionPane.showMessageDialog(this, "Erro ao salvar a revisão!");
        }
    }

    private void abrirLink() {
        EntregaTarefa entrega = selecionada();
        if (entrega == null) {
            return;
        }
        if (entrega.getArquivo() == null || entrega.getArquivo().getArquivoUrl() == null) {
            JOptionPane.showMessageDialog(this, "Esta entrega não tem link.");
            return;
        }
        String url = entrega.getArquivo().getArquivoUrl();
        try {
            Desktop.getDesktop().browse(new URI(url));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Não foi possível abrir o navegador. Link:\n" + url);
        }
    }
}
