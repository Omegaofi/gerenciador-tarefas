package gerenciador.tarefas.cliente.view;

import gerenciador.tarefas.cliente.GerenciadorTarefasCliente;
import gerenciador.tarefas.cliente.controller.ConexaoController;
import gerenciador.tarefas.cliente.view.tablemodel.TarefaTableModel;
import gerenciador.tarefas.cliente.view.tema.Tema;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import modelDominio.EntregaTarefa;
import modelDominio.Tarefa;
import modelDominio.Usuario;

/**
 * Criador: todas as tarefas dos seus projetos (criar, editar, excluir).
 * Participante (RF005): somente as tarefas atribuídas a ele, com o envio da entrega (RF006).
 */
public class TabelaTarefa extends TelaBase {

    private static final String[][] FILTROS = {
        {"TODAS", null}, {"PENDENTE", "pendente"}, {"EM ANDAMENTO", "em_andamento"},
        {"CONCLUÍDA", "concluida"}, {"ATRASADA", "atrasada"}};

    private final ConexaoController conexao = GerenciadorTarefasCliente.conexaoCliente;
    private final boolean criador = conexao.isCriador();
    private final JTable jTable1 = new JTable();
    private final JPanel painelFiltros = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
    private TarefaTableModel tarefaModel;
    private String filtroAtual;

    public TabelaTarefa() {
        super(conexao().isCriador() ? "Tarefas" : "Minhas tarefas", TAREFAS);

        JButton botaoNova = null;
        if (criador) {
            botaoNova = Tema.botaoPrimario("+ Nova");
            botaoNova.addActionListener(e -> new FormTarefa(this, null, this::refreshTabelaTarefa).setVisible(true));
        }
        JPanel topo = new JPanel(new BorderLayout(0, 16));
        topo.setOpaque(false);
        topo.add(cabecalho(criador ? "Tarefas" : "Minhas tarefas",
                criador ? "Tarefas dos seus projetos" : "Tarefas atribuídas a você", botaoNova), BorderLayout.NORTH);
        painelFiltros.setOpaque(false);
        topo.add(painelFiltros, BorderLayout.SOUTH);
        conteudo.add(topo, BorderLayout.NORTH);

        atualizarTabelaTarefas();
        conteudo.add(tabelaEstilizada(jTable1, TarefaTableModel.COLUNA_STATUS), BorderLayout.CENTER);

        if (criador) {
            JButton botaoEditar = Tema.botaoSecundario("Editar");
            botaoEditar.addActionListener(e -> editarSelecionada());
            JButton botaoExcluir = Tema.botaoPerigo("Excluir");
            botaoExcluir.addActionListener(e -> excluirSelecionada());
            conteudo.add(barraAcoes(botaoEditar, botaoExcluir), BorderLayout.SOUTH);
            jTable1.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent evt) {
                    if (evt.getClickCount() == 2) {
                        editarSelecionada();
                    }
                }
            });
        } else {
            JButton botaoEntregar = Tema.botaoDestaque("Enviar entrega da tarefa selecionada");
            botaoEntregar.addActionListener(e -> entregarSelecionada());
            conteudo.add(barraAcoes(botaoEntregar), BorderLayout.SOUTH);
        }
    }

    private static ConexaoController conexao() {
        return GerenciadorTarefasCliente.conexaoCliente;
    }

    private void montarFiltros() {
        painelFiltros.removeAll();
        for (String[] filtro : FILTROS) {
            boolean ativo = filtroAtual == null ? filtro[1] == null : filtroAtual.equals(filtro[1]);
            JButton chip = ativo ? Tema.botaoPrimario(filtro[0]) : Tema.botaoSecundario(filtro[0]);
            chip.addActionListener(e -> {
                filtroAtual = filtro[1];
                atualizarTabelaTarefas();
            });
            painelFiltros.add(chip);
            painelFiltros.add(Box.createHorizontalStrut(6));
        }
        painelFiltros.revalidate();
        painelFiltros.repaint();
    }

    private void atualizarTabelaTarefas() {
        Usuario logado = conexao.getUsuarioLogado();
        ArrayList<Tarefa> todas = criador
                ? conexao.tarefaListaPorCriador(logado.getId())
                : conexao.tarefaListaPorResponsavel(logado.getId());
        ArrayList<Tarefa> visiveis = new ArrayList<>();
        for (Tarefa t : todas) {
            if (filtroAtual == null || filtroAtual.equals(TarefaTableModel.statusExibido(t))) {
                visiveis.add(t);
            }
        }
        tarefaModel = new TarefaTableModel(visiveis);
        jTable1.setModel(tarefaModel);
        jTable1.getColumnModel().getColumn(TarefaTableModel.COLUNA_STATUS).setCellRenderer(Tema.rendererStatus());
        montarFiltros();
    }

    public void refreshTabelaTarefa() {
        atualizarTabelaTarefas();
    }

    private Tarefa selecionada(String acao) {
        int linha = jTable1.getSelectedRow();
        if (linha < 0) {
            JOptionPane.showMessageDialog(this, "Selecione uma tarefa para " + acao + ".");
            return null;
        }
        return tarefaModel.getTarefa(linha);
    }

    private void editarSelecionada() {
        Tarefa tarefa = selecionada("editar");
        if (tarefa != null) {
            new FormTarefa(this, tarefa, this::refreshTabelaTarefa).setVisible(true);
        }
    }

    private void excluirSelecionada() {
        Tarefa tarefa = selecionada("excluir");
        if (tarefa == null) {
            return;
        }
        int resposta = JOptionPane.showConfirmDialog(this, "Deseja excluir a tarefa \"" + tarefa.getTitulo() + "\"?",
                "Confirmar exclusão", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (resposta == JOptionPane.YES_OPTION) {
            if (conexao.tarefaExcluir(tarefa)) {
                JOptionPane.showMessageDialog(this, "Tarefa excluída com sucesso!");
                atualizarTabelaTarefas();
            } else {
                JOptionPane.showMessageDialog(this, "Erro ao excluir tarefa!");
            }
        }
    }

    private void entregarSelecionada() {
        Tarefa tarefa = selecionada("entregar");
        if (tarefa == null) {
            return;
        }
        if ("concluida".equals(tarefa.getStatus())) {
            JOptionPane.showMessageDialog(this, "Esta tarefa já foi concluída.");
            return;
        }
        for (EntregaTarefa entrega : conexao.entregaTarefaListaPorResponsavel(conexao.getUsuarioLogado().getId())) {
            boolean aguardando = "enviada".equals(entrega.getStatus()) || "em_revisao".equals(entrega.getStatus());
            if (aguardando && entrega.getTarefa().getId() == tarefa.getId()) {
                JOptionPane.showMessageDialog(this, "Já existe uma entrega desta tarefa aguardando revisão do criador.");
                return;
            }
        }
        new FormEntrega(this, tarefa, this::refreshTabelaTarefa).setVisible(true);
    }
}
