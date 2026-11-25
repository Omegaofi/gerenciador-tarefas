/*
 * Clique em nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt para alterar esta licença
 * Clique em nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java para editar este modelo
 */
package gerenciador.tarefas.cliente.view;

import gerenciador.tarefas.cliente.GerenciadorTarefasCliente;
import gerenciador.tarefas.cliente.controller.ConexaoController;
import gerenciador.tarefas.cliente.view.tablemodel.TarefaTableModel;
import gerenciador.tarefas.cliente.view.tema.Tema;
import java.util.ArrayList;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import modelDominio.EntregaTarefa;
import modelDominio.Tarefa;
import modelDominio.Usuario;

/**
 *
 * @author theis
 */
public class TabelaTarefa1 extends javax.swing.JFrame {

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(TabelaTarefa1.class.getName());

    private static final String[][] FILTROS = {
        {"TODAS", null}, {"PENDENTE", "pendente"}, {"EM ANDAMENTO", "em_andamento"},
        {"CONCLUÍDA", "concluida"}, {"ATRASADA", "atrasada"}};

    private final ConexaoController conexao = GerenciadorTarefasCliente.conexaoCliente;
    private final boolean criador = conexao != null && conexao.isCriador();
    private TarefaTableModel tarefaModel;
    private String filtroAtual;

    // Tela de tarefas: o criador vê as dos seus projetos, o participante vê as dele
    public TabelaTarefa1() {
        initComponents();
        estilizarComponentes();
        setLocationRelativeTo(null);
        getContentPane().setBackground(Tema.FUNDO);
        setTitle("NexProject - " + (criador ? "Tarefas" : "Minhas tarefas"));
        lblTitulo.setText(criador ? "Tarefas" : "Minhas tarefas");
        lblSubtitulo.setText(criador ? "Tarefas dos seus projetos" : "Tarefas atribuídas a você");
        botaoNova.setVisible(criador);
        if (criador) {
            painelAcoes.remove(botaoEntregar);
        } else {
            painelAcoes.remove(botaoEditar);
            painelAcoes.remove(botaoExcluir);
        }
        espacarAcoes();
        atualizarTabelaTarefas();
        Tema.estilizarTabela(jTable1, jScrollPane1);
    }

    // Aplica o visual do NexProject (Tema) nos componentes criados pelo Design do NetBeans
    private void estilizarComponentes() {
        Tema.estilizarTitulo(lblTitulo);
        Tema.estilizarSubtitulo(lblSubtitulo);
        Tema.estilizarBotao(botaoNova, Tema.PRIMARIO);
        Tema.estilizarBotao(botaoEditar, Tema.SECUNDARIO);
        Tema.estilizarBotao(botaoExcluir, Tema.PERIGO);
        Tema.estilizarBotao(botaoEntregar, Tema.DESTAQUE);
        pack();   // recalcula o tamanho da janela com as fontes novas
    }

    /**
     * Este método é chamado pelo construtor para montar os componentes da tela.
     * ATENÇÃO: NÃO altere este código. O conteúdo deste método é sempre
     * gerado novamente pelo editor visual (Design) do NetBeans.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        menuLateral = new gerenciador.tarefas.cliente.view.MenuLateral();

        menuLateral.setItemAtivo("TAREFAS");
        lblTitulo = new javax.swing.JLabel();
        lblTitulo.setText("Tarefas");
        lblSubtitulo = new javax.swing.JLabel();
        lblSubtitulo.setText("Tarefas dos seus projetos");
        botaoNova = new javax.swing.JButton();
        botaoNova.setText("+ Nova");
        painelFiltros = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        painelAcoes = new javax.swing.JPanel();
        botaoEditar = new javax.swing.JButton();
        botaoEditar.setText("Editar");
        botaoExcluir = new javax.swing.JButton();
        botaoExcluir.setText("Excluir");
        botaoEntregar = new javax.swing.JButton();
        botaoEntregar.setText("Enviar entrega da tarefa selecionada");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("NexProject - Tarefas");
        setMinimumSize(new java.awt.Dimension(1000, 640));
        setPreferredSize(new java.awt.Dimension(1120, 700));




        botaoNova.addActionListener(this::botaoNovaActionPerformed);

        painelFiltros.setOpaque(false);

        painelFiltros.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 0, 0));

        jTable1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable1MouseClicked(evt);
            }
        });

        jScrollPane1.setViewportView(jTable1);

        botaoEditar.addActionListener(this::botaoEditarActionPerformed);

        botaoExcluir.addActionListener(this::botaoExcluirActionPerformed);

        botaoEntregar.addActionListener(this::botaoEntregarActionPerformed);

        painelAcoes.setOpaque(false);

        painelAcoes.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 0, 0));
        painelAcoes.add(botaoEditar);
        painelAcoes.add(botaoExcluir);
        painelAcoes.add(botaoEntregar);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(menuLateral, javax.swing.GroupLayout.PREFERRED_SIZE, 230, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(32, 32, 32)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblTitulo)
                            .addComponent(lblSubtitulo))
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(botaoNova))
                    .addComponent(painelFiltros)
                    .addComponent(jScrollPane1)
                    .addComponent(painelAcoes))
                .addGap(32, 32, 32))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(menuLateral, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblTitulo)
                        .addGap(4, 4, 4)
                        .addComponent(lblSubtitulo))
                    .addComponent(botaoNova))
                .addGap(16, 16, 16)
                .addComponent(painelFiltros, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(painelAcoes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(28, 28, 28))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void botaoNovaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botaoNovaActionPerformed
        // Botão Nova: abre o formulário de tarefa vazio
        new FormTarefa1(this, null, this::refreshTabelaTarefa).setVisible(true);
    }//GEN-LAST:event_botaoNovaActionPerformed

    private void botaoEditarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botaoEditarActionPerformed
        // Botão Editar a tarefa selecionada
        editarSelecionada();
    }//GEN-LAST:event_botaoEditarActionPerformed

    private void botaoExcluirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botaoExcluirActionPerformed
        // Botão Excluir a tarefa selecionada
        excluirSelecionada();
    }//GEN-LAST:event_botaoExcluirActionPerformed

    private void botaoEntregarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botaoEntregarActionPerformed
        // Botão Entregar: abre o formulário de entrega da tarefa selecionada
        entregarSelecionada();
    }//GEN-LAST:event_botaoEntregarActionPerformed

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseClicked
        // Duplo clique na tabela abre a edição (só para o criador)
        if (criador && evt.getClickCount() == 2) {
            editarSelecionada();
        }
    }//GEN-LAST:event_jTable1MouseClicked

    /** Põe 10px entre os botões que sobraram na barra de ações. */
    private void espacarAcoes() {
        java.awt.Component[] botoes = painelAcoes.getComponents();
        painelAcoes.removeAll();
        for (int i = 0; i < botoes.length; i++) {
            if (i > 0) {
                painelAcoes.add(javax.swing.Box.createHorizontalStrut(10));
            }
            painelAcoes.add(botoes[i]);
        }
    }

    // Monta os botões de filtro por status
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

    // Busca as tarefas no servidor, aplica o filtro e preenche a tabela
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

    // Recarrega a tabela (chamado depois de salvar uma tarefa)
    public void refreshTabelaTarefa() {
        atualizarTabelaTarefas();
    }

    // Tarefa selecionada na tabela (avisa se nenhuma estiver)
    private Tarefa selecionada(String acao) {
        int linha = jTable1.getSelectedRow();
        if (linha < 0) {
            JOptionPane.showMessageDialog(this, "Selecione uma tarefa para " + acao + ".");
            return null;
        }
        return tarefaModel.getTarefa(linha);
    }

    // Abre o formulário com a tarefa selecionada
    private void editarSelecionada() {
        Tarefa tarefa = selecionada("editar");
        if (tarefa != null) {
            new FormTarefa1(this, tarefa, this::refreshTabelaTarefa).setVisible(true);
        }
    }

    // Confirma e exclui a tarefa selecionada
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

    // Abre o formulário de entrega da tarefa selecionada
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
        new FormEntrega1(this, tarefa, this::refreshTabelaTarefa).setVisible(true);
    }

    /**
     * @param args os argumentos da linha de comando
     */
    public static void main(String args[]) {
        Tema.aplicar();
        java.awt.EventQueue.invokeLater(() -> new TabelaTarefa1().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton botaoEditar;
    private javax.swing.JButton botaoEntregar;
    private javax.swing.JButton botaoExcluir;
    private javax.swing.JButton botaoNova;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JLabel lblSubtitulo;
    private javax.swing.JLabel lblTitulo;
    private gerenciador.tarefas.cliente.view.MenuLateral menuLateral;
    private javax.swing.JPanel painelAcoes;
    private javax.swing.JPanel painelFiltros;
    // End of variables declaration//GEN-END:variables
}
