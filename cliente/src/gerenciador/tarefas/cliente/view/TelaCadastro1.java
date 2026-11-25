/*
 * Clique em nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt para alterar esta licença
 * Clique em nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java para editar este modelo
 */
package gerenciador.tarefas.cliente.view;

import gerenciador.tarefas.cliente.GerenciadorTarefasCliente;
import gerenciador.tarefas.cliente.view.tema.Tema;
import modelDominio.Usuario;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import javax.swing.JButton;

/**
 *
 * @author theis
 */
public class TelaCadastro1 extends javax.swing.JFrame {

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(TelaCadastro1.class.getName());

    /**
     * Cria a tela TelaCadastro1
     */
    public TelaCadastro1() {
        initComponents();
        estilizarComponentes();
        setLocationRelativeTo(null);
        getRootPane().setDefaultButton(botaoCriar);
    }

    // Aplica o visual do NexProject (Tema) nos componentes criados pelo Design do NetBeans
    private void estilizarComponentes() {
        Tema.estilizarLogo(lblLogoMarca, 22, true);
        lblChamada.setFont(Tema.fonte(java.awt.Font.PLAIN, 13));
        lblDestaque1.setFont(Tema.fonte(java.awt.Font.PLAIN, 13));
        lblDestaque2.setFont(Tema.fonte(java.awt.Font.PLAIN, 13));
        lblDestaque3.setFont(Tema.fonte(java.awt.Font.PLAIN, 13));
        lblDestaque4.setFont(Tema.fonte(java.awt.Font.PLAIN, 13));
        lblCopyright.setFont(Tema.fonte(java.awt.Font.PLAIN, 13));
        Tema.estilizarLogo(lblLogo, 16, false);
        Tema.estilizarSubtitulo(lblSubtitulo);
        Tema.estilizarTitulo(lblTitulo, 17);
        textAlerta.setFont(Tema.fonte(java.awt.Font.BOLD, 12));
        Tema.estilizarRotulo(lblNome);
        Tema.estilizarCampo(inputNome);
        Tema.estilizarRotulo(lblEmail);
        Tema.estilizarCampo(inputEmail);
        Tema.estilizarRotulo(lblSenha);
        Tema.estilizarCampo(inputSenha);
        Tema.estilizarRotulo(lblConfirmar);
        Tema.estilizarCampo(inputConfirmar);
        Tema.estilizarRotulo(lblTipo);
        Tema.estilizarBotao(botaoCriador, Tema.PRIMARIO);
        Tema.estilizarBotao(botaoParticipante, Tema.SECUNDARIO);
        Tema.estilizarBotao(botaoCriar, Tema.PRIMARIO);
        Tema.estilizarSubtitulo(lblJaTemConta);
        Tema.estilizarLink(linkEntrar);
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

        painelMarca = new javax.swing.JPanel();
        lblLogoMarca = new javax.swing.JLabel();
        lblChamada = new javax.swing.JLabel();
        lblDestaque1 = new javax.swing.JLabel();
        lblDestaque2 = new javax.swing.JLabel();
        lblDestaque3 = new javax.swing.JLabel();
        lblDestaque4 = new javax.swing.JLabel();
        lblCopyright = new javax.swing.JLabel();
        painelLado = new javax.swing.JPanel();
        painelForm = new javax.swing.JPanel();
        lblLogo = new javax.swing.JLabel();
        lblSubtitulo = new javax.swing.JLabel();
        lblTitulo = new javax.swing.JLabel();
        textAlerta = new javax.swing.JLabel();
        lblNome = new javax.swing.JLabel();
        inputNome = new javax.swing.JTextField();
        lblEmail = new javax.swing.JLabel();
        inputEmail = new javax.swing.JTextField();
        lblSenha = new javax.swing.JLabel();
        inputSenha = new javax.swing.JPasswordField();
        lblConfirmar = new javax.swing.JLabel();
        inputConfirmar = new javax.swing.JPasswordField();
        lblTipo = new javax.swing.JLabel();
        painelTipos = new javax.swing.JPanel();
        botaoCriador = new javax.swing.JButton();
        botaoParticipante = new javax.swing.JButton();
        botaoCriar = new javax.swing.JButton();
        painelLogin = new javax.swing.JPanel();
        lblJaTemConta = new javax.swing.JLabel();
        linkEntrar = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("NexProject - Criar conta");
        setMinimumSize(new java.awt.Dimension(860, 640));
        setPreferredSize(new java.awt.Dimension(980, 680));
        getContentPane().setLayout(new java.awt.GridLayout(1, 2));

        painelMarca.setBackground(new java.awt.Color(11, 11, 11));

        lblChamada.setForeground(new java.awt.Color(221, 221, 221));
        lblChamada.setText("<html>Colabore em projetos, gerencie tarefas e<br>acompanhe entregas — tudo em um só lugar.</html>");

        lblDestaque1.setForeground(new java.awt.Color(181, 181, 181));
        lblDestaque1.setText("<html><span style='color:#C6F033'>■</span>&nbsp; Projetos colaborativos</html>");

        lblDestaque2.setForeground(new java.awt.Color(181, 181, 181));
        lblDestaque2.setText("<html><span style='color:#C6F033'>■</span>&nbsp; Atribuição de tarefas</html>");

        lblDestaque3.setForeground(new java.awt.Color(181, 181, 181));
        lblDestaque3.setText("<html><span style='color:#C6F033'>■</span>&nbsp; Entrega e revisão de tarefas</html>");

        lblDestaque4.setForeground(new java.awt.Color(181, 181, 181));
        lblDestaque4.setText("<html><span style='color:#C6F033'>■</span>&nbsp; Gestão de arquivos</html>");

        lblCopyright.setForeground(new java.awt.Color(85, 85, 85));
        lblCopyright.setText("© 2026 NexProject");

        javax.swing.GroupLayout painelMarcaLayout = new javax.swing.GroupLayout(painelMarca);
        painelMarca.setLayout(painelMarcaLayout);
        painelMarcaLayout.setHorizontalGroup(
            painelMarcaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(painelMarcaLayout.createSequentialGroup()
                .addGap(40, 40, 40)
                .addGroup(painelMarcaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblLogoMarca)
                    .addComponent(lblChamada, javax.swing.GroupLayout.DEFAULT_SIZE, 419, Short.MAX_VALUE)
                    .addComponent(lblDestaque1)
                    .addComponent(lblDestaque2)
                    .addComponent(lblDestaque3)
                    .addComponent(lblDestaque4)
                    .addComponent(lblCopyright, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(40, 40, 40))
        );
        painelMarcaLayout.setVerticalGroup(
            painelMarcaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(painelMarcaLayout.createSequentialGroup()
                .addGap(36, 36, 36)
                .addComponent(lblLogoMarca, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 149, Short.MAX_VALUE)
                .addComponent(lblChamada, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(22, 22, 22)
                .addComponent(lblDestaque1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(lblDestaque2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(lblDestaque3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(lblDestaque4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 153, Short.MAX_VALUE)
                .addComponent(lblCopyright, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(30, 30, 30))
        );

        getContentPane().add(painelMarca);

        painelLado.setBackground(new java.awt.Color(242, 239, 236));

        painelForm.setOpaque(false);

        lblSubtitulo.setText("Plataforma de gestão colaborativa");
        lblTitulo.setText("Criar conta");
        textAlerta.setForeground(new java.awt.Color(214, 59, 59));
        textAlerta.setText(" ");

        lblNome.setText("Nome completo");

        lblEmail.setText("E-mail");

        lblSenha.setText("Senha");

        lblConfirmar.setText("Confirmar senha");

        lblTipo.setText("Tipo de conta");

        painelTipos.setOpaque(false);
        painelTipos.setLayout(new java.awt.GridLayout(1, 2, 8, 0));

        botaoCriador.setText("Criador");
        botaoCriador.addActionListener(this::botaoCriadorActionPerformed);
        painelTipos.add(botaoCriador);
        botaoParticipante.setText("Participante");
        botaoParticipante.addActionListener(this::botaoParticipanteActionPerformed);
        painelTipos.add(botaoParticipante);

        botaoCriar.setText("Criar conta  →");
        botaoCriar.addActionListener(this::botaoCriarActionPerformed);

        painelLogin.setOpaque(false);
        painelLogin.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 4, 0));

        lblJaTemConta.setText("Já tem conta?");
        painelLogin.add(lblJaTemConta);

        linkEntrar.setText("Entrar");
        linkEntrar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                linkEntrarMouseClicked(evt);
            }
        });
        painelLogin.add(linkEntrar);

        javax.swing.GroupLayout painelFormLayout = new javax.swing.GroupLayout(painelForm);
        painelForm.setLayout(painelFormLayout);
        painelFormLayout.setHorizontalGroup(
            painelFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(lblLogo)
            .addComponent(lblSubtitulo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(lblTitulo, javax.swing.GroupLayout.DEFAULT_SIZE, 320, Short.MAX_VALUE)
            .addComponent(textAlerta, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(lblNome, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(inputNome, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(lblEmail, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(inputEmail, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(lblSenha, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(inputSenha, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(lblConfirmar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(inputConfirmar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(lblTipo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(painelTipos, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(botaoCriar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(painelLogin, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        painelFormLayout.setVerticalGroup(
            painelFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(painelFormLayout.createSequentialGroup()
                .addComponent(lblLogo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2)
                .addComponent(lblSubtitulo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(22, 22, 22)
                .addComponent(lblTitulo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6)
                .addComponent(textAlerta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6)
                .addComponent(lblNome, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5)
                .addComponent(inputNome, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(14, 14, 14)
                .addComponent(lblEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5)
                .addComponent(inputEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(14, 14, 14)
                .addComponent(lblSenha, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5)
                .addComponent(inputSenha, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(14, 14, 14)
                .addComponent(lblConfirmar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5)
                .addComponent(inputConfirmar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(14, 14, 14)
                .addComponent(lblTipo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5)
                .addComponent(painelTipos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20)
                .addComponent(botaoCriar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(16, 16, 16)
                .addComponent(painelLogin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout painelLadoLayout = new javax.swing.GroupLayout(painelLado);
        painelLado.setLayout(painelLadoLayout);
        painelLadoLayout.setHorizontalGroup(
            painelLadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(painelLadoLayout.createSequentialGroup()
                .addGap(0, 89, Short.MAX_VALUE)
                .addComponent(painelForm, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 90, Short.MAX_VALUE))
        );
        painelLadoLayout.setVerticalGroup(
            painelLadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(painelLadoLayout.createSequentialGroup()
                .addGap(0, 14, Short.MAX_VALUE)
                .addComponent(painelForm, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 14, Short.MAX_VALUE))
        );

        getContentPane().add(painelLado);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /** RF001: cadastro de usuário como Criador (idealizador) ou Participante (colaborador). */
    private void botaoCriarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botaoCriarActionPerformed
        // Botão Criar conta: valida os campos, criptografa a senha e cadastra no servidor
        String nome = inputNome.getText().trim();
        String email = inputEmail.getText().trim();
        String senha = new String(inputSenha.getPassword());
        String confirmar = new String(inputConfirmar.getPassword());

        if (nome.isEmpty() || email.isEmpty() || senha.isEmpty()) {
            textAlerta.setText("Preencha nome, e-mail e senha.");
            return;
        }
        if (!email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
            textAlerta.setText("Informe um e-mail válido.");
            inputEmail.requestFocus();
            return;
        }
        // A senha também tranca a chave privada do chat: senha fraca, chave fraca (seção 13)
        if (senha.length() < 8) {
            textAlerta.setText("A senha precisa ter ao menos 8 caracteres.");
            inputSenha.requestFocus();
            return;
        }
        if (!senha.equals(confirmar)) {
            textAlerta.setText("As senhas não conferem.");
            inputConfirmar.setText("");
            inputConfirmar.requestFocus();
            return;
        }

        // Código da disciplina de Segurança da Informação: o servidor nunca recebe a senha,
        // recebe o SHA-256 da senha em hexadecimal e guarda esse SHA-256 no banco
        String senhaCriptografada;
        try {
            // 1. Captura e gera o hash
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte messageDigest[] = md.digest(senha.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : messageDigest) {
                sb.append(String.format("%02x", 0xFF & b));
            }
            senhaCriptografada = sb.toString();
        } catch (java.security.GeneralSecurityException e) {
            logger.log(java.util.logging.Level.SEVERE, "Falha ao criptografar a senha", e);
            textAlerta.setText("Erro ao proteger a senha. Tente novamente.");
            return;
        }
        Usuario novo = new Usuario(nome, email, senhaCriptografada, null, tipoSelecionado);
        if (GerenciadorTarefasCliente.conexaoCliente.usuarioInserir(novo)) {
            javax.swing.JOptionPane.showMessageDialog(this, "Conta criada com sucesso! Faça login para continuar.");
            voltarParaLogin(email);
        } else {
            textAlerta.setText("Não foi possível criar a conta. Este e-mail já está em uso?");
        }
    }//GEN-LAST:event_botaoCriarActionPerformed

    private void botaoCriadorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botaoCriadorActionPerformed
        // Escolhe o tipo de conta Criador
        selecionarTipo("criador");
    }//GEN-LAST:event_botaoCriadorActionPerformed

    private void botaoParticipanteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botaoParticipanteActionPerformed
        // Escolhe o tipo de conta Participante
        selecionarTipo("participante");
    }//GEN-LAST:event_botaoParticipanteActionPerformed

    private void linkEntrarMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_linkEntrarMouseClicked
        // Link "Entrar": volta para a tela de login
        voltarParaLogin("");
    }//GEN-LAST:event_linkEntrarMouseClicked

    /** Alterna o botão escolhido entre Criador e Participante (o escolhido fica preto). */
    private void selecionarTipo(String tipo) {
        tipoSelecionado = tipo;
        Tema.estilizarBotao(botaoCriador, "criador".equals(tipo) ? Tema.PRIMARIO : Tema.SECUNDARIO);
        Tema.estilizarBotao(botaoParticipante, "participante".equals(tipo) ? Tema.PRIMARIO : Tema.SECUNDARIO);
    }

    // Abre a tela de login (com o e-mail preenchido) e fecha o cadastro
    private void voltarParaLogin(String email) {
        TelaLogin1 telaLogin = new TelaLogin1(email);
        telaLogin.setBounds(getBounds());
        telaLogin.setVisible(true);
        dispose();
    }

    /**
     * @param args os argumentos da linha de comando
     */
    public static void main(String args[]) {
        /* Aplica o tema do NexProject (no lugar do Nimbus padrão do NetBeans) */
        Tema.aplicar();

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new TelaCadastro1().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton botaoCriador;
    private javax.swing.JButton botaoCriar;
    private javax.swing.JButton botaoParticipante;
    private javax.swing.JPasswordField inputConfirmar;
    private javax.swing.JTextField inputEmail;
    private javax.swing.JTextField inputNome;
    private javax.swing.JPasswordField inputSenha;
    private javax.swing.JLabel lblChamada;
    private javax.swing.JLabel lblConfirmar;
    private javax.swing.JLabel lblCopyright;
    private javax.swing.JLabel lblDestaque1;
    private javax.swing.JLabel lblDestaque2;
    private javax.swing.JLabel lblDestaque3;
    private javax.swing.JLabel lblDestaque4;
    private javax.swing.JLabel lblEmail;
    private javax.swing.JLabel lblJaTemConta;
    private javax.swing.JLabel lblLogo;
    private javax.swing.JLabel lblLogoMarca;
    private javax.swing.JLabel lblNome;
    private javax.swing.JLabel lblSenha;
    private javax.swing.JLabel lblSubtitulo;
    private javax.swing.JLabel lblTipo;
    private javax.swing.JLabel lblTitulo;
    private javax.swing.JLabel linkEntrar;
    private javax.swing.JPanel painelForm;
    private javax.swing.JPanel painelLado;
    private javax.swing.JPanel painelLogin;
    private javax.swing.JPanel painelMarca;
    private javax.swing.JPanel painelTipos;
    private javax.swing.JLabel textAlerta;
    // End of variables declaration//GEN-END:variables

    private String tipoSelecionado = "criador";
}
