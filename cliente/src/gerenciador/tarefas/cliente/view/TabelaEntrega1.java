/*
 * Clique em nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt para alterar esta licença
 * Clique em nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java para editar este modelo
 */
package gerenciador.tarefas.cliente.view;

import gerenciador.tarefas.cliente.GerenciadorTarefasCliente;
import gerenciador.tarefas.cliente.controller.ConexaoController;
import gerenciador.tarefas.cliente.view.tablemodel.EntregaTableModel;
import gerenciador.tarefas.cliente.view.tema.Tema;
import java.awt.Desktop;
import java.net.URI;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import modelDominio.EntregaTarefa;

/**
 *
 * @author theis
 */
public class TabelaEntrega1 extends javax.swing.JFrame {

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(TabelaEntrega1.class.getName());

    private final ConexaoController conexao = GerenciadorTarefasCliente.conexaoCliente;
    private final boolean criador = conexao != null && conexao.isCriador();
    private EntregaTableModel entregaModel;

    // Tela de entregas: o criador vê as dos seus projetos, o participante vê as suas
    public TabelaEntrega1() {
        initComponents();
        estilizarComponentes();
        setLocationRelativeTo(null);
        getContentPane().setBackground(Tema.FUNDO);
        lblTitulo.setText(criador ? "Entregas" : "Minhas entregas");
        lblSubtitulo.setText(criador ? "Revise o que os colaboradores entregaram"
                : "Acompanhe a revisão das suas entregas");
        if (!criador) {
            painelAcoes.remove(botaoAprovar);
            painelAcoes.remove(botaoRejeitar);
        }
        espacarAcoes();
        atualizarTabelaEntregas();
        Tema.estilizarTabela(jTable1, jScrollPane1);
    }

    // Aplica o visual do NexProject (Tema) nos componentes criados pelo Design do NetBeans
    private void estilizarComponentes() {
        Tema.estilizarTitulo(lblTitulo);
        Tema.estilizarSubtitulo(lblSubtitulo);
        Tema.estilizarBotao(botaoAprovar, Tema.DESTAQUE);
        Tema.estilizarBotao(botaoRejeitar, Tema.PERIGO);
        Tema.estilizarBotao(botaoAbrir, Tema.SECUNDARIO);
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
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        painelAcoes = new javax.swing.JPanel();
        botaoAprovar = new javax.swing.JButton();
        botaoRejeitar = new javax.swing.JButton();
        botaoAbrir = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("NexProject - Entregas");
        setMinimumSize(new java.awt.Dimension(1000, 640));
        setPreferredSize(new java.awt.Dimension(1120, 700));

        menuLateral.setItemAtivo("ENTREGAS");

        lblTitulo.setText("Entregas");

        lblSubtitulo.setText("Revise o que os colaboradores entregaram");

        jScrollPane1.setViewportView(jTable1);

        painelAcoes.setOpaque(false);
        painelAcoes.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 0, 0));

        botaoAprovar.setText("Aprovar");
        botaoAprovar.addActionListener(this::botaoAprovarActionPerformed);
        painelAcoes.add(botaoAprovar);

        botaoRejeitar.setText("Rejeitar");
        botaoRejeitar.addActionListener(this::botaoRejeitarActionPerformed);
        painelAcoes.add(botaoRejeitar);

        botaoAbrir.setText("Abrir link");
        botaoAbrir.addActionListener(this::botaoAbrirActionPerformed);
        painelAcoes.add(botaoAbrir);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(menuLateral, javax.swing.GroupLayout.PREFERRED_SIZE, 230, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(32, 32, 32)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblTitulo)
                    .addComponent(lblSubtitulo)
                    .addComponent(jScrollPane1)
                    .addComponent(painelAcoes))
                .addGap(32, 32, 32))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(menuLateral, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addComponent(lblTitulo)
                .addGap(4, 4, 4)
                .addComponent(lblSubtitulo)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1)
                .addGap(18, 18, 18)
                .addComponent(painelAcoes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(28, 28, 28))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void botaoAprovarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botaoAprovarActionPerformed
        // Botão Aprovar a entrega selecionada
        avaliar("aprovada");
    }//GEN-LAST:event_botaoAprovarActionPerformed

    private void botaoRejeitarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botaoRejeitarActionPerformed
        // Botão Rejeitar a entrega selecionada
        avaliar("rejeitada");
    }//GEN-LAST:event_botaoRejeitarActionPerformed

    private void botaoAbrirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botaoAbrirActionPerformed
        // Botão Abrir: abre o link da entrega no navegador
        abrirLink();
    }//GEN-LAST:event_botaoAbrirActionPerformed

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

    // Busca as entregas no servidor e preenche a tabela
    private void atualizarTabelaEntregas() {
        int usuarioId = conexao.getUsuarioLogado().getId();
        ArrayList<EntregaTarefa> lista = criador
                ? conexao.entregaTarefaListaPorCriador(usuarioId)
                : conexao.entregaTarefaListaPorResponsavel(usuarioId);
        entregaModel = new EntregaTableModel(lista);
        jTable1.setModel(entregaModel);
        jTable1.getColumnModel().getColumn(EntregaTableModel.COLUNA_STATUS).setCellRenderer(Tema.rendererStatus());
    }

    // Entrega selecionada na tabela (avisa se nenhuma estiver)
    private EntregaTarefa selecionada() {
        int linha = jTable1.getSelectedRow();
        if (linha < 0) {
            JOptionPane.showMessageDialog(this, "Selecione uma entrega.");
            return null;
        }
        return entregaModel.getEntrega(linha);
    }

    // Muda o status da entrega (aprovada/rejeitada) no servidor
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

    // Abre no navegador o link enviado na entrega
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

    /**
     * @param args os argumentos da linha de comando
     */
    public static void main(String args[]) {
        Tema.aplicar();
        java.awt.EventQueue.invokeLater(() -> new TabelaEntrega1().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton botaoAbrir;
    private javax.swing.JButton botaoAprovar;
    private javax.swing.JButton botaoRejeitar;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JLabel lblSubtitulo;
    private javax.swing.JLabel lblTitulo;
    private gerenciador.tarefas.cliente.view.MenuLateral menuLateral;
    private javax.swing.JPanel painelAcoes;
    // End of variables declaration//GEN-END:variables
}
