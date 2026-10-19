package gerenciador.tarefas.cliente.view;

import gerenciador.tarefas.cliente.GerenciadorTarefasCliente;
import gerenciador.tarefas.cliente.view.tablemodel.ProjetoTableModel;
import gerenciador.tarefas.cliente.view.tema.Tema;
import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import modelDominio.Projeto;

/** Projetos do Criador logado: criar, editar e excluir. */
public class TabelaProjeto extends TelaBase {

    private final JTable jTable1 = new JTable();
    private ProjetoTableModel projetoModel;

    public TabelaProjeto() {
        super("Projetos", PROJETOS);

        JButton botaoNovo = Tema.botaoPrimario("+ Novo projeto");
        botaoNovo.addActionListener(e -> new FormProjeto(this, null, this::refreshTabelaProjeto).setVisible(true));
        conteudo.add(cabecalho("Projetos", "Projetos criados por você", botaoNovo), BorderLayout.NORTH);

        atualizarTabelaProjetos();
        conteudo.add(tabelaEstilizada(jTable1, ProjetoTableModel.COLUNA_STATUS), BorderLayout.CENTER);

        JButton botaoEditar = Tema.botaoSecundario("Editar");
        botaoEditar.addActionListener(e -> editarSelecionado());
        JButton botaoExcluir = Tema.botaoPerigo("Excluir");
        botaoExcluir.addActionListener(e -> excluirSelecionado());
        conteudo.add(barraAcoes(botaoEditar, botaoExcluir), BorderLayout.SOUTH);

        jTable1.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    editarSelecionado();
                }
            }
        });
    }

    private void atualizarTabelaProjetos() {
        int criadorId = GerenciadorTarefasCliente.conexaoCliente.getUsuarioLogado().getId();
        ArrayList<Projeto> listaProjetos = GerenciadorTarefasCliente.conexaoCliente.projetoListaPorCriador(criadorId);
        projetoModel = new ProjetoTableModel(listaProjetos);
        jTable1.setModel(projetoModel);
        jTable1.getColumnModel().getColumn(ProjetoTableModel.COLUNA_STATUS).setCellRenderer(Tema.rendererStatus());
    }

    public void refreshTabelaProjeto() {
        atualizarTabelaProjetos();
    }

    private void editarSelecionado() {
        int linha = jTable1.getSelectedRow();
        if (linha < 0) {
            JOptionPane.showMessageDialog(this, "Selecione um projeto para editar.");
            return;
        }
        new FormProjeto(this, projetoModel.getProjeto(linha), this::refreshTabelaProjeto).setVisible(true);
    }

    private void excluirSelecionado() {
        int linha = jTable1.getSelectedRow();
        if (linha < 0) {
            JOptionPane.showMessageDialog(this, "Selecione um projeto para excluir.");
            return;
        }
        Projeto projeto = projetoModel.getProjeto(linha);
        int resposta = JOptionPane.showConfirmDialog(this, "Deseja excluir o projeto \"" + projeto.getTitulo()
                + "\"?\nTodas as tarefas dele também serão excluídas.",
                "Confirmar exclusão", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (resposta == JOptionPane.YES_OPTION) {
            if (GerenciadorTarefasCliente.conexaoCliente.projetoExcluir(projeto)) {
                JOptionPane.showMessageDialog(this, "Projeto excluído com sucesso!");
                atualizarTabelaProjetos();
            } else {
                JOptionPane.showMessageDialog(this, "Erro ao excluir projeto!");
            }
        }
    }
}
