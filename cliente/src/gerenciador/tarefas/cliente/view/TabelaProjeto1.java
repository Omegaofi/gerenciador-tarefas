/*
 * Clique em nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt para alterar esta licença
 * Clique em nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java para editar este modelo
 */
package gerenciador.tarefas.cliente.view;

import gerenciador.tarefas.cliente.GerenciadorTarefasCliente;
import gerenciador.tarefas.cliente.view.tablemodel.ProjetoTableModel;
import gerenciador.tarefas.cliente.view.tema.Tema;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import modelDominio.Projeto;

/**
 *
 * @author theis
 */
public class TabelaProjeto1 extends javax.swing.JFrame {

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(TabelaProjeto1.class.getName());

    private ProjetoTableModel projetoModel;

    // Tela de projetos do criador
    public TabelaProjeto1() {
        initComponents();
        estilizarComponentes();
        setLocationRelativeTo(null);
        getContentPane().setBackground(Tema.FUNDO);
        espacarAcoes();
        atualizarTabelaProjetos();
        atualizarContadorInscricoes();
        Tema.estilizarTabela(jTable1, jScrollPane1);
    }

    // Aplica o visual do NexProject (Tema) nos componentes criados pelo Design do NetBeans
    private void estilizarComponentes() {
        Tema.estilizarTitulo(lblTitulo);
        Tema.estilizarSubtitulo(lblSubtitulo);
        Tema.estilizarBotao(botaoNovo, Tema.PRIMARIO);
        Tema.estilizarBotao(botaoEditar, Tema.SECUNDARIO);
        Tema.estilizarBotao(botaoExcluir, Tema.PERIGO);
        Tema.estilizarBotao(botaoInscricoes, Tema.DESTAQUE);
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
        lblTitulo = new javax.swing.JLabel();
        lblSubtitulo = new javax.swing.JLabel();
        botaoNovo = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        painelAcoes = new javax.swing.JPanel();
        botaoEditar = new javax.swing.JButton();
        botaoExcluir = new javax.swing.JButton();
        botaoInscricoes = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("NexProject - Projetos");
        setMinimumSize(new java.awt.Dimension(1000, 640));
        setPreferredSize(new java.awt.Dimension(1120, 700));

        menuLateral.setItemAtivo("PROJETOS");

        lblTitulo.setText("Projetos");

        lblSubtitulo.setText("Projetos criados por você");

        botaoNovo.setText("+ Novo projeto");
        botaoNovo.addActionListener(this::botaoNovoActionPerformed);

        jTable1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable1MouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jTable1);

        painelAcoes.setOpaque(false);
        painelAcoes.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 0, 0));

        botaoEditar.setText("Editar");
        botaoEditar.addActionListener(this::botaoEditarActionPerformed);
        painelAcoes.add(botaoEditar);

        botaoExcluir.setText("Excluir");
        botaoExcluir.addActionListener(this::botaoExcluirActionPerformed);
        painelAcoes.add(botaoExcluir);

        botaoInscricoes.setText("Inscrições");
        botaoInscricoes.addActionListener(this::botaoInscricoesActionPerformed);
        painelAcoes.add(botaoInscricoes);

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
                        .addComponent(botaoNovo))
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
                    .addComponent(botaoNovo))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1)
                .addGap(18, 18, 18)
                .addComponent(painelAcoes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(28, 28, 28))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void botaoNovoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botaoNovoActionPerformed
        // Botão Novo: abre o formulário de projeto vazio
        new FormProjeto1(this, null, this::refreshTabelaProjeto).setVisible(true);
    }//GEN-LAST:event_botaoNovoActionPerformed

    private void botaoEditarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botaoEditarActionPerformed
        // Botão Editar o projeto selecionado
        editarSelecionado();
    }//GEN-LAST:event_botaoEditarActionPerformed

    private void botaoExcluirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botaoExcluirActionPerformed
        // Botão Excluir o projeto selecionado
        excluirSelecionado();
    }//GEN-LAST:event_botaoExcluirActionPerformed

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseClicked
        // Duplo clique na tabela abre a edição
        if (evt.getClickCount() == 2) {
            editarSelecionado();
        }
    }//GEN-LAST:event_jTable1MouseClicked

    private void botaoInscricoesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botaoInscricoesActionPerformed
        // Botão Inscrições: abre os pedidos de entrada nos projetos
        new TelaInscricoes(this, this::atualizarContadorInscricoes).setVisible(true);
    }//GEN-LAST:event_botaoInscricoesActionPerformed

    /** Mostra no botão quantas inscrições estão esperando resposta, ex.: "Inscrições (2)". */
    private void atualizarContadorInscricoes() {
        int pendentes = GerenciadorTarefasCliente.conexaoCliente.inscricaoPendentesCriador().size();
        botaoInscricoes.setText(pendentes > 0 ? "Inscrições (" + pendentes + ")" : "Inscrições");
    }

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

    // Busca os projetos do criador no servidor e preenche a tabela
    private void atualizarTabelaProjetos() {
        int criadorId = GerenciadorTarefasCliente.conexaoCliente.getUsuarioLogado().getId();
        ArrayList<Projeto> listaProjetos = GerenciadorTarefasCliente.conexaoCliente.projetoListaPorCriador(criadorId);
        projetoModel = new ProjetoTableModel(listaProjetos);
        jTable1.setModel(projetoModel);
        jTable1.getColumnModel().getColumn(ProjetoTableModel.COLUNA_STATUS).setCellRenderer(Tema.rendererStatus());
    }

    // Recarrega a tabela (chamado depois de salvar um projeto)
    public void refreshTabelaProjeto() {
        atualizarTabelaProjetos();
    }

    // Abre o formulário com o projeto selecionado
    private void editarSelecionado() {
        int linha = jTable1.getSelectedRow();
        if (linha < 0) {
            JOptionPane.showMessageDialog(this, "Selecione um projeto para editar.");
            return;
        }
        new FormProjeto1(this, projetoModel.getProjeto(linha), this::refreshTabelaProjeto).setVisible(true);
    }

    // Confirma e exclui o projeto selecionado
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

    /**
     * @param args os argumentos da linha de comando
     */
    public static void main(String args[]) {
        Tema.aplicar();
        java.awt.EventQueue.invokeLater(() -> new TabelaProjeto1().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton botaoEditar;
    private javax.swing.JButton botaoExcluir;
    private javax.swing.JButton botaoInscricoes;
    private javax.swing.JButton botaoNovo;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JLabel lblSubtitulo;
    private javax.swing.JLabel lblTitulo;
    private gerenciador.tarefas.cliente.view.MenuLateral menuLateral;
    private javax.swing.JPanel painelAcoes;
    // End of variables declaration//GEN-END:variables
}
