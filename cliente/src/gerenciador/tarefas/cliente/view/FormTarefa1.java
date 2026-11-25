/*
 * Clique em nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt para alterar esta licença
 * Clique em nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JDialog.java para editar este modelo
 */
package gerenciador.tarefas.cliente.view;

import gerenciador.tarefas.cliente.GerenciadorTarefasCliente;
import gerenciador.tarefas.cliente.controller.ConexaoController;
import gerenciador.tarefas.cliente.view.tema.Tema;
import java.awt.Component;
import java.awt.Font;
import java.awt.Frame;
import java.util.ArrayList;
import java.util.Date;
import java.util.function.Function;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JSpinner;
import modelDominio.Projeto;
import modelDominio.Tarefa;
import modelDominio.Usuario;

/**
 *
 * @author theis
 */
public class FormTarefa1 extends javax.swing.JDialog {

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(FormTarefa1.class.getName());

    private final ConexaoController conexao = GerenciadorTarefasCliente.conexaoCliente;
    private final Tarefa tarefa;
    private final Runnable aoSalvar;

    /**
     * @param tarefa tarefa a editar, ou null para criar uma nova
     * @param aoSalvar executado depois de salvar com sucesso (ex.: atualizar a tabela)
     */
    public FormTarefa1(Frame dono, Tarefa tarefa, Runnable aoSalvar) {
        super(dono, true);
        initComponents();
        estilizarComponentes();
        this.tarefa = tarefa;
        this.aoSalvar = aoSalvar;

        getContentPane().setBackground(Tema.FUNDO);
        getRootPane().setDefaultButton(botaoSalvar);
        setTitle(tarefa == null ? "Nova tarefa" : "Editar tarefa");
        lblCabecalho.setText(getTitle());
        lblSubtitulo.setText("Defina o prazo e o colaborador responsável");
        botaoSalvar.setText(tarefa == null ? "Criar tarefa" : "Salvar");

        carregarCombos();
        jSpinnerPrazo.setEditor(new JSpinner.DateEditor(jSpinnerPrazo, "dd/MM/yyyy"));
        jSpinnerPrazo.setBorder(javax.swing.BorderFactory.createLineBorder(Tema.BORDA));
        Tema.estilizarCombo(jComboBoxProjeto);
        Tema.estilizarCombo(jComboBoxResponsavel);
        Tema.estilizarCombo(jComboBoxStatus);

        if (tarefa != null) {
            preencher(tarefa);
        }
        finalizarLayout();
    }

    // Preenche os combos com os projetos do criador e os possíveis responsáveis
    private void carregarCombos() {
        Usuario logado = conexao.getUsuarioLogado();
        for (Projeto projeto : conexao.projetoListaPorCriador(logado.getId())) {
            jComboBoxProjeto.addItem(projeto);
        }
        jComboBoxProjeto.setRenderer(renderer(Projeto::getTitulo));
        jComboBoxResponsavel.setRenderer(FormTarefa1.<Usuario>renderer(u -> u.getNome() + "  (" + u.getEmail() + ")"));

        // Os responsáveis dependem do projeto: só quem foi aceito nele pode receber tarefa
        jComboBoxProjeto.addActionListener(e -> carregarResponsaveis());
        carregarResponsaveis();
    }

    /** Preenche o combo de responsáveis com os membros aceitos do projeto escolhido. */
    private void carregarResponsaveis() {
        jComboBoxResponsavel.removeAllItems();
        Projeto projeto = (Projeto) jComboBoxProjeto.getSelectedItem();
        if (projeto == null) {
            textAlerta.setText("Crie um projeto antes de cadastrar tarefas.");
            return;
        }
        for (Usuario usuario : conexao.projetoParticipantes(projeto.getId())) {
            jComboBoxResponsavel.addItem(usuario);
        }
        textAlerta.setText(jComboBoxResponsavel.getItemCount() == 0
                ? "<html>Ninguém foi aceito neste projeto ainda.<br>Aprove inscrições em Projetos → Inscrições.</html>"
                : " ");
    }

    // Na edição, preenche os campos com os dados da tarefa
    private void preencher(Tarefa t) {
        jTextFieldTitulo.setText(t.getTitulo());
        if (t.getPrazo() != null) {
            jSpinnerPrazo.setValue(t.getPrazo());
        }
        Opcao.selecionarValor(jComboBoxStatus, t.getStatus());
        if (t.getProjeto() != null) {
            for (int i = 0; i < jComboBoxProjeto.getItemCount(); i++) {
                if (jComboBoxProjeto.getItemAt(i).getId() == t.getProjeto().getId()) {
                    jComboBoxProjeto.setSelectedIndex(i);
                    break;
                }
            }
        }
        if (t.getUsuario() != null) {
            for (int i = 0; i < jComboBoxResponsavel.getItemCount(); i++) {
                if (jComboBoxResponsavel.getItemAt(i).getId() == t.getUsuario().getId()) {
                    jComboBoxResponsavel.setSelectedIndex(i);
                    break;
                }
            }
        }
    }

    // Define como cada item aparece no combo (o texto mostrado)
    private static <T> DefaultListCellRenderer renderer(Function<T, String> texto) {
        return new DefaultListCellRenderer() {
            @Override
            @SuppressWarnings("unchecked")
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setText(value != null ? texto.apply((T) value) : "");
                return this;
            }
        };
    }

    // Aplica o visual do NexProject (Tema) nos componentes criados pelo Design do NetBeans
    private void estilizarComponentes() {
        Tema.estilizarTitulo(lblCabecalho, 18);
        Tema.estilizarSubtitulo(lblSubtitulo);
        textAlerta.setFont(Tema.fonte(java.awt.Font.BOLD, 12));
        Tema.estilizarRotulo(lblTitulo);
        Tema.estilizarCampo(jTextFieldTitulo);
        Tema.estilizarRotulo(lblProjeto);
        Tema.estilizarRotulo(lblResponsavel);
        Tema.estilizarRotulo(lblPrazo);
        Tema.estilizarRotulo(lblStatus);
        Tema.estilizarBotao(botaoCancelar, Tema.SECUNDARIO);
        Tema.estilizarBotao(botaoSalvar, Tema.PRIMARIO);
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

        lblCabecalho = new javax.swing.JLabel();
        lblSubtitulo = new javax.swing.JLabel();
        textAlerta = new javax.swing.JLabel();
        lblTitulo = new javax.swing.JLabel();
        jTextFieldTitulo = new javax.swing.JTextField();
        lblProjeto = new javax.swing.JLabel();
        jComboBoxProjeto = new javax.swing.JComboBox<>();
        lblResponsavel = new javax.swing.JLabel();
        jComboBoxResponsavel = new javax.swing.JComboBox<>();
        lblPrazo = new javax.swing.JLabel();
        jSpinnerPrazo = new javax.swing.JSpinner(new javax.swing.SpinnerDateModel());
        lblStatus = new javax.swing.JLabel();
        jComboBoxStatus = new javax.swing.JComboBox<>(Opcao.deStatus("pendente", "em_andamento", "concluida"));
        botaoCancelar = new javax.swing.JButton();
        botaoSalvar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        lblCabecalho.setText("Nova tarefa");

        lblSubtitulo.setText("Defina o prazo e o colaborador responsável");
        textAlerta.setForeground(new java.awt.Color(214, 59, 59));
        textAlerta.setText(" ");

        lblTitulo.setText("Título");

        lblProjeto.setText("Projeto");

        lblResponsavel.setText("Colaborador responsável");

        lblPrazo.setText("Prazo de entrega");

        lblStatus.setText("Status");

        botaoCancelar.setText("Cancelar");
        botaoCancelar.addActionListener(this::botaoCancelarActionPerformed);

        botaoSalvar.setText("Salvar");
        botaoSalvar.addActionListener(this::botaoSalvarActionPerformed);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblCabecalho, javax.swing.GroupLayout.DEFAULT_SIZE, 820, Short.MAX_VALUE)
                    .addComponent(lblSubtitulo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(textAlerta, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblTitulo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jTextFieldTitulo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblProjeto, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jComboBoxProjeto, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblResponsavel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jComboBoxResponsavel, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblPrazo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jSpinnerPrazo)
                    .addComponent(lblStatus, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jComboBoxStatus, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(botaoCancelar)
                        .addGap(10, 10, 10)
                        .addComponent(botaoSalvar)))
                .addGap(28, 28, 28))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addComponent(lblCabecalho)
                .addGap(4, 4, 4)
                .addComponent(lblSubtitulo)
                .addGap(8, 8, 8)
                .addComponent(textAlerta)
                .addGap(16, 16, 16)
                .addComponent(lblTitulo)
                .addGap(5, 5, 5)
                .addComponent(jTextFieldTitulo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(14, 14, 14)
                .addComponent(lblProjeto)
                .addGap(5, 5, 5)
                .addComponent(jComboBoxProjeto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(14, 14, 14)
                .addComponent(lblResponsavel)
                .addGap(5, 5, 5)
                .addComponent(jComboBoxResponsavel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(14, 14, 14)
                .addComponent(lblPrazo)
                .addGap(5, 5, 5)
                .addComponent(jSpinnerPrazo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(14, 14, 14)
                .addComponent(lblStatus)
                .addGap(5, 5, 5)
                .addComponent(jComboBoxStatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(30, 30, 30)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(botaoCancelar)
                    .addComponent(botaoSalvar))
                .addGap(22, 22, 22))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void botaoCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botaoCancelarActionPerformed
        // Botão Cancelar: fecha sem salvar
        dispose();
    }//GEN-LAST:event_botaoCancelarActionPerformed

    private void botaoSalvarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botaoSalvarActionPerformed
        // Botão Salvar: valida e cria ou atualiza a tarefa no servidor
        String titulo = jTextFieldTitulo.getText().trim();
        Projeto projeto = (Projeto) jComboBoxProjeto.getSelectedItem();
        Usuario responsavel = (Usuario) jComboBoxResponsavel.getSelectedItem();
        if (titulo.isEmpty()) {
            textAlerta.setText("Informe o título da tarefa.");
            jTextFieldTitulo.requestFocus();
            return;
        }
        if (projeto == null) {
            textAlerta.setText("Selecione um projeto.");
            return;
        }
        if (responsavel == null) {
            textAlerta.setText("Selecione o colaborador responsável.");
            return;
        }
        Date prazo = (Date) jSpinnerPrazo.getValue();
        String status = ((Opcao) jComboBoxStatus.getSelectedItem()).valor();

        boolean ok;
        if (tarefa != null) {
            ok = conexao.tarefaAtualizar(new Tarefa(tarefa.getId(), titulo, prazo, status, projeto,
                    responsavel, tarefa.getCriadoEm()));
        } else {
            ok = conexao.tarefaInserir(new Tarefa(titulo, prazo, status, projeto, responsavel));
        }

        if (ok) {
            JOptionPane.showMessageDialog(this, tarefa != null ? "Tarefa atualizada com sucesso!" : "Tarefa criada com sucesso!");
            dispose();
            if (aoSalvar != null) {
                aoSalvar.run();
            }
        } else {
            textAlerta.setText("Erro ao salvar a tarefa. Tente novamente.");
        }
    }//GEN-LAST:event_botaoSalvarActionPerformed


    /** Ajusta o tamanho ao conteúdo (largura mínima de 460px) e centraliza na tela de origem. */
    private void finalizarLayout() {
        pack();
        setSize(Math.max(460, getWidth()), getHeight());
        setLocationRelativeTo(getOwner());
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton botaoCancelar;
    private javax.swing.JButton botaoSalvar;
    private javax.swing.JComboBox<Projeto> jComboBoxProjeto;
    private javax.swing.JComboBox<Usuario> jComboBoxResponsavel;
    private javax.swing.JComboBox<Opcao> jComboBoxStatus;
    private javax.swing.JSpinner jSpinnerPrazo;
    private javax.swing.JTextField jTextFieldTitulo;
    private javax.swing.JLabel lblCabecalho;
    private javax.swing.JLabel lblPrazo;
    private javax.swing.JLabel lblProjeto;
    private javax.swing.JLabel lblResponsavel;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JLabel lblSubtitulo;
    private javax.swing.JLabel lblTitulo;
    private javax.swing.JLabel textAlerta;
    // End of variables declaration//GEN-END:variables
}
