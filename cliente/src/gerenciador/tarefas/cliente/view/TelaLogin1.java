/*
 * Clique em nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt para alterar esta licença
 * Clique em nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java para editar este modelo
 */
package gerenciador.tarefas.cliente.view;

import gerenciador.tarefas.cliente.GerenciadorTarefasCliente;
import gerenciador.tarefas.cliente.controller.ConexaoController;
import gerenciador.tarefas.cliente.view.tema.Tema;
import modelDominio.Usuario;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

/**
 *
 * @author theis
 */
public class TelaLogin1 extends javax.swing.JFrame {

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(TelaLogin1.class.getName());

    /**
     * Criptografia da senha (código da disciplina de Segurança da Informação):
     * SHA-256 da senha em hexadecimal. É esse valor que vai para o servidor, que confere
     * com o SHA-256 guardado no banco. Usado também na troca de senha.
     */
    public static String criptografarSenha(char[] senhaChars) throws java.security.GeneralSecurityException {
        // Captura e gera o hash
        String senha = new String(senhaChars);
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte messageDigest[] = md.digest(senha.getBytes(StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();
        for (byte b : messageDigest) {
            sb.append(String.format("%02x", 0xFF & b));
        }
        String senhaCriptografada = sb.toString();
        return senhaCriptografada;
    }

    /**
     * Cria a tela TelaLogin1
     */
    public TelaLogin1() {
        this("");
    }

    // Abre a tela de login já com o e-mail preenchido (usado ao voltar do cadastro)
    public TelaLogin1(String emailInicial) {
        initComponents();
        estilizarComponentes();
        setLocationRelativeTo(null);
        inputEmail.setText(emailInicial);
        getRootPane().setDefaultButton(botaoEntrar);
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
        Tema.estilizarRotulo(lblEmail);
        Tema.estilizarCampo(inputEmail);
        Tema.estilizarRotulo(lblSenha);
        Tema.estilizarCampo(inputSenha);
        Tema.estilizarLink(linkEsqueciSenha);
        Tema.estilizarBotao(botaoEntrar, Tema.PRIMARIO);
        Tema.estilizarSubtitulo(lblNaoTemConta);
        Tema.estilizarLink(linkCadastro);
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
        lblSubtitulo.setText("Plataforma de gestão colaborativa");
        lblTitulo = new javax.swing.JLabel();
        textAlerta = new javax.swing.JLabel();
        lblEmail = new javax.swing.JLabel();
        lblEmail.setText("E-mail");
        inputEmail = new javax.swing.JTextField();
        lblSenha = new javax.swing.JLabel();
        lblSenha.setText("Senha");
        inputSenha = new javax.swing.JPasswordField();
        linkEsqueciSenha = new javax.swing.JLabel();
        linkEsqueciSenha.setText("Esqueceu a senha?");
        botaoEntrar = new javax.swing.JButton();
        botaoEntrar.setText("Entrar  →");
        painelCadastro = new javax.swing.JPanel();
        lblNaoTemConta = new javax.swing.JLabel();
        lblNaoTemConta.setText("Não tem conta?");
        linkCadastro = new javax.swing.JLabel();
        linkCadastro.setText("Cadastre-se");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("NexProject - Entrar");
        setMinimumSize(new java.awt.Dimension(860, 560));
        setPreferredSize(new java.awt.Dimension(980, 620));
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
                    .addComponent(lblLogoMarca, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblChamada, javax.swing.GroupLayout.DEFAULT_SIZE, 308, Short.MAX_VALUE)
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
                .addComponent(lblLogoMarca)
                .addGap(0, 104, Short.MAX_VALUE)
                .addComponent(lblChamada, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(22, 22, 22)
                .addComponent(lblDestaque1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(lblDestaque2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(lblDestaque3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(lblDestaque4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 109, Short.MAX_VALUE)
                .addComponent(lblCopyright)
                .addGap(30, 30, 30))
        );

        getContentPane().add(painelMarca);

        painelLado.setBackground(new java.awt.Color(242, 239, 236));

        painelForm.setOpaque(false);

        lblTitulo.setText("Entrar na conta");

        textAlerta.setForeground(new java.awt.Color(214, 59, 59));
        textAlerta.setText(" ");

        linkEsqueciSenha.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                linkEsqueciSenhaMouseClicked(evt);
            }
        });

        botaoEntrar.addActionListener(this::botaoEntrarActionPerformed);

        painelCadastro.setOpaque(false);
        painelCadastro.add(lblNaoTemConta);

        linkCadastro.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                linkCadastroMouseClicked(evt);
            }
        });
        painelCadastro.add(linkCadastro);

        javax.swing.GroupLayout painelFormLayout = new javax.swing.GroupLayout(painelForm);
        painelForm.setLayout(painelFormLayout);
        painelFormLayout.setHorizontalGroup(
            painelFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(lblLogo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(lblSubtitulo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(lblTitulo, javax.swing.GroupLayout.DEFAULT_SIZE, 320, Short.MAX_VALUE)
            .addComponent(textAlerta, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(lblEmail, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(inputEmail)
            .addComponent(lblSenha, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(inputSenha)
            .addComponent(linkEsqueciSenha, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(botaoEntrar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(painelCadastro, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        painelFormLayout.setVerticalGroup(
            painelFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(painelFormLayout.createSequentialGroup()
                .addComponent(lblLogo)
                .addGap(2, 2, 2)
                .addComponent(lblSubtitulo)
                .addGap(28, 28, 28)
                .addComponent(lblTitulo)
                .addGap(6, 6, 6)
                .addComponent(textAlerta)
                .addGap(6, 6, 6)
                .addComponent(lblEmail)
                .addGap(5, 5, 5)
                .addComponent(inputEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(14, 14, 14)
                .addComponent(lblSenha)
                .addGap(5, 5, 5)
                .addComponent(inputSenha, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(8, 8, 8)
                .addComponent(linkEsqueciSenha, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(16, 16, 16)
                .addComponent(botaoEntrar)
                .addGap(16, 16, 16)
                .addComponent(painelCadastro, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout painelLadoLayout = new javax.swing.GroupLayout(painelLado);
        painelLado.setLayout(painelLadoLayout);
        painelLadoLayout.setHorizontalGroup(
            painelLadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(painelLadoLayout.createSequentialGroup()
                .addGap(0, 34, Short.MAX_VALUE)
                .addComponent(painelForm, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 34, Short.MAX_VALUE))
        );
        painelLadoLayout.setVerticalGroup(
            painelLadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(painelLadoLayout.createSequentialGroup()
                .addGap(0, 126, Short.MAX_VALUE)
                .addComponent(painelForm, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 127, Short.MAX_VALUE))
        );

        getContentPane().add(painelLado);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void botaoEntrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botaoEntrarActionPerformed
        // Botão Entrar: criptografa a senha, faz o login no servidor, abre o chat e vai para a tela certa
        String email = inputEmail.getText().trim();
        String senha = new String(inputSenha.getPassword());
        if (email.isEmpty() || senha.isEmpty()) {
            textAlerta.setText("Informe e-mail e senha.");
            return;
        }

        ConexaoController conexao = GerenciadorTarefasCliente.conexaoCliente;
        char[] senhaChars = inputSenha.getPassword();
        try {
            // 1) O servidor recebe o SHA-256 da senha, nunca a senha
            String senhaCriptografada = criptografarSenha(senhaChars);
            Usuario userLogado = conexao.usuarioLoginSha(email, senhaCriptografada);

            if (userLogado == null) {
                textAlerta.setText("E-mail ou senha incorretos.");
                inputSenha.setText("");
                inputSenha.requestFocus();
                return;
            }
            conexao.setUsuarioLogado(userLogado);

            // 2) Destranca a chave privada do chat (ou cria o par no primeiro login)
            try {
                conexao.getChatCifrado().abrirCofre(userLogado, senhaChars);
            } catch (Exception e) {
                logger.log(java.util.logging.Level.WARNING, "Não foi possível abrir as chaves do chat", e);
                javax.swing.JOptionPane.showMessageDialog(this,
                        "Login feito, mas as chaves do chat não abriram.\nO chat ficará indisponível nesta sessão.",
                        "Chat cifrado", javax.swing.JOptionPane.WARNING_MESSAGE);
            }

            // Participante que ainda não está em nenhum projeto vai direto procurar um
            if (!conexao.isCriador() && !conexao.usuarioParticipaDeProjeto()) {
                new FormProcura().setVisible(true);
            } else {
                new TelaPrincipal1().setVisible(true);
            }
            dispose();
        } catch (java.security.GeneralSecurityException e) {
            logger.log(java.util.logging.Level.SEVERE, "Falha ao criptografar a senha", e);
            textAlerta.setText("Erro ao proteger a senha. Tente novamente.");
        } finally {
            java.util.Arrays.fill(senhaChars, '\0');   // a senha não fica na memória
        }
    }//GEN-LAST:event_botaoEntrarActionPerformed

    private void linkCadastroMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_linkCadastroMouseClicked
        // Link "Cadastre-se": abre a tela de cadastro no mesmo lugar e fecha o login
        TelaCadastro1 telaCadastro = new TelaCadastro1();
        telaCadastro.setBounds(getBounds());
        telaCadastro.setVisible(true);
        dispose();
    }//GEN-LAST:event_linkCadastroMouseClicked

    private void linkEsqueciSenhaMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_linkEsqueciSenhaMouseClicked
        // TODO: fluxo de recuperação por e-mail (código enviado pelo servidor via SMTP)
        javax.swing.JOptionPane.showMessageDialog(this,
                "A recuperação de senha por e-mail estará disponível em breve.\n"
                + "Se você lembra a senha atual, troque-a pelo menu ALTERAR SENHA depois de entrar.",
                "Esqueceu a senha?", javax.swing.JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_linkEsqueciSenhaMouseClicked

    /**
     * @param args os argumentos da linha de comando
     */
    public static void main(String args[]) {
        /* Aplica o tema do NexProject (no lugar do Nimbus padrão do NetBeans) */
        Tema.aplicar();

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new TelaLogin1().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton botaoEntrar;
    private javax.swing.JTextField inputEmail;
    private javax.swing.JPasswordField inputSenha;
    private javax.swing.JLabel lblChamada;
    private javax.swing.JLabel lblCopyright;
    private javax.swing.JLabel lblDestaque1;
    private javax.swing.JLabel lblDestaque2;
    private javax.swing.JLabel lblDestaque3;
    private javax.swing.JLabel lblDestaque4;
    private javax.swing.JLabel lblEmail;
    private javax.swing.JLabel lblLogo;
    private javax.swing.JLabel lblLogoMarca;
    private javax.swing.JLabel lblNaoTemConta;
    private javax.swing.JLabel lblSenha;
    private javax.swing.JLabel lblSubtitulo;
    private javax.swing.JLabel lblTitulo;
    private javax.swing.JLabel linkCadastro;
    private javax.swing.JLabel linkEsqueciSenha;
    private javax.swing.JPanel painelCadastro;
    private javax.swing.JPanel painelForm;
    private javax.swing.JPanel painelLado;
    private javax.swing.JPanel painelMarca;
    private javax.swing.JLabel textAlerta;
    // End of variables declaration//GEN-END:variables
}
