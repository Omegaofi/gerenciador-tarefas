/*
 * Clique em nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt para alterar esta licença
 * Clique em nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JDialog.java para editar este modelo
 */
package gerenciador.tarefas.cliente.view;

import gerenciador.tarefas.cliente.GerenciadorTarefasCliente;
import gerenciador.tarefas.cliente.view.tema.Tema;
import java.awt.Font;
import java.awt.Frame;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JOptionPane;
import modelDominio.Arquivo;
import modelDominio.EntregaTarefa;
import modelDominio.Tarefa;
import modelDominio.Usuario;

/**
 *
 * @author theis
 */
public class FormEntrega1 extends javax.swing.JDialog {

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(FormEntrega1.class.getName());

    private final Tarefa tarefa;
    private final Runnable aoEnviar;

    // Abre o formulário de entrega da tarefa informada; aoEnviar recarrega a tela de trás
    public FormEntrega1(Frame dono, Tarefa tarefa, Runnable aoEnviar) {
        super(dono, true);
        initComponents();
        estilizarComponentes();
        this.tarefa = tarefa;
        this.aoEnviar = aoEnviar;

        getContentPane().setBackground(Tema.FUNDO);
        getRootPane().setDefaultButton(botaoSalvar);
        setTitle("Enviar entrega");
        lblCabecalho.setText(getTitle());
        lblSubtitulo.setText(subtitulo(tarefa));
        botaoSalvar.setText("Enviar entrega");

        jTextFieldDescricao.setText("Entrega - " + tarefa.getTitulo());
        finalizarLayout();
    }

    // Texto abaixo do título: prazo e projeto da tarefa
    private static String subtitulo(Tarefa tarefa) {
        String prazo = tarefa.getPrazo() != null ? new SimpleDateFormat("dd/MM/yyyy").format(tarefa.getPrazo()) : "sem prazo";
        String projeto = tarefa.getProjeto() != null ? tarefa.getProjeto().getTitulo() : "";
        return tarefa.getTitulo() + " · " + projeto + " · prazo " + prazo;
    }

    // Aplica o visual do NexProject (Tema) nos componentes criados pelo Design do NetBeans
    private void estilizarComponentes() {
        Tema.estilizarTitulo(lblCabecalho, 18);
        Tema.estilizarSubtitulo(lblSubtitulo);
        textAlerta.setFont(Tema.fonte(java.awt.Font.BOLD, 12));
        Tema.estilizarRotulo(lblDescricao);
        Tema.estilizarCampo(jTextFieldDescricao);
        Tema.estilizarRotulo(lblLink);
        Tema.estilizarCampo(jTextFieldLink);
        Tema.estilizarSubtitulo(lblDica);
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

        lblCabecalho.setText("Enviar entrega");
        lblSubtitulo = new javax.swing.JLabel();
        lblSubtitulo.setText("Tarefa: título da tarefa");
        textAlerta = new javax.swing.JLabel();
        lblDescricao = new javax.swing.JLabel();
        lblDescricao.setText("Descrição da entrega");
        jTextFieldDescricao = new javax.swing.JTextField();
        lblLink = new javax.swing.JLabel();
        lblLink.setText("Link do arquivo ou repositório");
        jTextFieldLink = new javax.swing.JTextField();
        lblDica = new javax.swing.JLabel();
        lblDica.setText("Ex.: https://github.com/usuario/projeto ou link do Google Drive");
        botaoCancelar = new javax.swing.JButton();
        botaoCancelar.setText("Cancelar");
        botaoSalvar = new javax.swing.JButton();
        botaoSalvar.setText("Salvar");

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);



        textAlerta.setForeground(new java.awt.Color(214, 59, 59));
        textAlerta.setText(" ");






        botaoCancelar.addActionListener(this::botaoCancelarActionPerformed);

        botaoSalvar.addActionListener(this::botaoSalvarActionPerformed);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblCabecalho, javax.swing.GroupLayout.DEFAULT_SIZE, 404, Short.MAX_VALUE)
                    .addComponent(lblSubtitulo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(textAlerta, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblDescricao, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jTextFieldDescricao, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblLink, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jTextFieldLink, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblDica, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
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
                .addComponent(lblDescricao)
                .addGap(5, 5, 5)
                .addComponent(jTextFieldDescricao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(14, 14, 14)
                .addComponent(lblLink)
                .addGap(5, 5, 5)
                .addComponent(jTextFieldLink, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(14, 14, 14)
                .addComponent(lblDica)
                .addGap(14, 14, 14)
                .addGap(16, 16, 16)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(botaoCancelar, javax.swing.GroupLayout.Alignment.BASELINE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE)
                    .addComponent(botaoSalvar, javax.swing.GroupLayout.Alignment.BASELINE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE))
                .addGap(22, 22, 22))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void botaoCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botaoCancelarActionPerformed
        // Botão Cancelar: fecha sem salvar
        dispose();
    }//GEN-LAST:event_botaoCancelarActionPerformed

    private void botaoSalvarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botaoSalvarActionPerformed
        // Botão Enviar: valida a descrição e o link e envia a entrega ao servidor
        String descricao = jTextFieldDescricao.getText().trim();
        String link = jTextFieldLink.getText().trim();
        if (descricao.isEmpty()) {
            textAlerta.setText("Descreva a entrega.");
            jTextFieldDescricao.requestFocus();
            return;
        }
        if (!link.matches("(?i)^https?://\\S+$")) {
            textAlerta.setText("Informe um link válido, começando com http:// ou https://");
            jTextFieldLink.requestFocus();
            return;
        }

        Usuario logado = GerenciadorTarefasCliente.conexaoCliente.getUsuarioLogado();
        Arquivo arquivo = new Arquivo(descricao, link, logado, tarefa);
        EntregaTarefa entrega = new EntregaTarefa(new Date(), "enviada", tarefa, arquivo);
        if (GerenciadorTarefasCliente.conexaoCliente.entregaTarefaEnviar(entrega)) {
            JOptionPane.showMessageDialog(this, "Entrega enviada! O criador do projeto vai revisar.");
            dispose();
            if (aoEnviar != null) {
                aoEnviar.run();
            }
        } else {
            textAlerta.setText("Erro ao enviar a entrega. Tente novamente.");
        }
    }//GEN-LAST:event_botaoSalvarActionPerformed

    /**
     * Ajusta o tamanho ao conteúdo (largura mínima de 460px) e centraliza na
     * tela de origem.
     */
    private void finalizarLayout() {
        pack();
        setSize(Math.max(460, getWidth()), getHeight());
        setLocationRelativeTo(getOwner());
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton botaoCancelar;
    private javax.swing.JButton botaoSalvar;
    private javax.swing.JTextField jTextFieldDescricao;
    private javax.swing.JTextField jTextFieldLink;
    private javax.swing.JLabel lblCabecalho;
    private javax.swing.JLabel lblDescricao;
    private javax.swing.JLabel lblDica;
    private javax.swing.JLabel lblLink;
    private javax.swing.JLabel lblSubtitulo;
    private javax.swing.JLabel textAlerta;
    // End of variables declaration//GEN-END:variables
}
