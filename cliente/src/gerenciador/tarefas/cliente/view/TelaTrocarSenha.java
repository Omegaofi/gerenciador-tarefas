package gerenciador.tarefas.cliente.view;

import gerenciador.tarefas.cliente.GerenciadorTarefasCliente;
import gerenciador.tarefas.cliente.controller.ConexaoController;
import gerenciador.tarefas.cliente.cripto.ChatCifrado;
import gerenciador.tarefas.cliente.view.tema.Tema;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Window;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import modelDominio.Usuario;

/**
 * Troca de senha do usuário logado (Segurança da Informação / RF002).
 *
 * <p>Como fica a senha nova:</p>
 * <ol>
 * <li>O cliente calcula o SHA-256 da senha atual e da nova, com a mesma
 * criptografia da tela de login. A senha em si nunca sai deste computador.</li>
 * <li>O servidor confere o valor da senha atual com o guardado e, se conferir,
 * grava o valor da nova.</li>
 * <li>A chave privada do chat é trancada de novo com a senha nova (PBKDF2 + AES-256-GCM)
 * e vai no mesmo pedido, para o chat continuar abrindo no próximo login.</li>
 * </ol>
 */
public class TelaTrocarSenha extends JDialog {

    private static final Logger logger = Logger.getLogger(TelaTrocarSenha.class.getName());
    private static final int TAMANHO_MINIMO = 8;

    private final JPasswordField inputAtual = Tema.campoSenha();
    private final JPasswordField inputNova = Tema.campoSenha();
    private final JPasswordField inputConfirmar = Tema.campoSenha();
    private final JLabel textAlerta = new JLabel(" ");
    private final JButton botaoSalvar = Tema.botaoPrimario("Salvar nova senha");
    private final JButton botaoCancelar = Tema.botaoSecundario("Cancelar");

    // Abre a janela de troca de senha por cima da tela atual (bloqueia a de trás)
    public TelaTrocarSenha(Window dono) {
        super(dono, "Alterar senha", ModalityType.APPLICATION_MODAL);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);
        montar();
        pack();
        setLocationRelativeTo(dono);
        getRootPane().setDefaultButton(botaoSalvar);
    }

    // Monta os componentes da janela: título, três campos de senha e botões
    private void montar() {
        JPanel fundo = new JPanel(new BorderLayout());
        fundo.setBackground(Tema.FUNDO);
        fundo.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        JPanel card = Tema.card();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Tema.BORDA),
                BorderFactory.createEmptyBorder(22, 24, 22, 24)));

        card.add(alinhar(Tema.titulo("Alterar senha")));
        card.add(Box.createVerticalStrut(4));
        card.add(alinhar(Tema.subtitulo("Use ao menos " + TAMANHO_MINIMO + " caracteres.")));
        card.add(Box.createVerticalStrut(18));

        adicionarCampo(card, "Senha atual", inputAtual);
        adicionarCampo(card, "Nova senha", inputNova);
        adicionarCampo(card, "Confirmar nova senha", inputConfirmar);

        textAlerta.setFont(Tema.fonte(java.awt.Font.BOLD, 12));
        textAlerta.setForeground(Tema.VERMELHO);
        card.add(alinhar(textAlerta));
        card.add(Box.createVerticalStrut(14));

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        botoes.setOpaque(false);
        botoes.add(botaoCancelar);
        botoes.add(botaoSalvar);
        card.add(alinhar(botoes));

        botaoCancelar.addActionListener(e -> dispose());
        botaoSalvar.addActionListener(e -> salvar());

        fundo.add(card, BorderLayout.CENTER);
        setContentPane(fundo);
    }

    // Adiciona um rótulo e um campo de senha ao formulário
    private void adicionarCampo(JPanel card, String rotulo, JPasswordField campo) {
        card.add(alinhar(Tema.rotuloCampo(rotulo)));
        card.add(Box.createVerticalStrut(6));
        campo.setPreferredSize(new Dimension(320, 38));
        campo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        card.add(alinhar(campo));
        card.add(Box.createVerticalStrut(14));
    }

    // Alinha o componente à esquerda dentro do formulário
    private static <T extends JComponent> T alinhar(T componente) {
        componente.setAlignmentX(Component.LEFT_ALIGNMENT);
        return componente;
    }

    // Botão Salvar: valida as senhas, criptografa a atual e a nova e pede a troca ao servidor
    private void salvar() {
        char[] atual = inputAtual.getPassword();
        char[] nova = inputNova.getPassword();
        char[] confirmar = inputConfirmar.getPassword();
        try {
            if (atual.length == 0 || nova.length == 0 || confirmar.length == 0) {
                alerta("Preencha os três campos.", inputAtual.getPassword().length == 0 ? inputAtual : inputNova);
                return;
            }
            if (nova.length < TAMANHO_MINIMO) {
                alerta("A nova senha precisa ter ao menos " + TAMANHO_MINIMO + " caracteres.", inputNova);
                return;
            }
            if (!Arrays.equals(nova, confirmar)) {
                alerta("A confirmação não confere com a nova senha.", inputConfirmar);
                return;
            }
            if (Arrays.equals(nova, atual)) {
                alerta("A nova senha precisa ser diferente da atual.", inputNova);
                return;
            }

            ConexaoController conexao = GerenciadorTarefasCliente.conexaoCliente;
            Usuario logado = conexao.getUsuarioLogado();
            ChatCifrado chat = conexao.getChatCifrado();

            // Se o cofre do chat não abriu no login, abre agora com a senha atual.
            // Isso também confere a senha atual antes de mandar qualquer coisa ao servidor.
            if (!chat.isCofreAberto()) {
                try {
                    chat.abrirCofre(logado, atual);
                } catch (GeneralSecurityException e) {
                    alerta("Senha atual incorreta.", inputAtual);
                    return;
                }
            }

            String shaAtual = TelaLogin1.criptografarSenha(atual);
            String shaNovo = TelaLogin1.criptografarSenha(nova);

            Usuario novosDados = chat.trancarCofreComNovaSenha(nova);
            novosDados.setSenha(shaNovo);

            if (!conexao.usuarioTrocarSenha(shaAtual, novosDados)) {
                alerta("Senha atual incorreta.", inputAtual);
                return;
            }
            chat.confirmarNovoCofre(novosDados);

            JOptionPane.showMessageDialog(this, "Senha alterada com sucesso.\nUse a nova senha no próximo login.",
                    "Alterar senha", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } catch (GeneralSecurityException | RuntimeException e) {
            logger.log(Level.SEVERE, "Falha ao trocar a senha", e);
            alerta("Não foi possível trocar a senha. Tente novamente.", inputAtual);
        } finally {
            // a senha não fica na memória
            Arrays.fill(atual, '\0');
            Arrays.fill(nova, '\0');
            Arrays.fill(confirmar, '\0');
        }
    }

    // Mostra o erro, limpa o campo e coloca o cursor nele
    private void alerta(String texto, JPasswordField campo) {
        textAlerta.setText(texto);
        campo.setText("");
        campo.requestFocus();
    }

    /** Só para ver o visual sem servidor. */
    public static void main(String[] args) {
        Tema.aplicar();
        java.awt.EventQueue.invokeLater(() -> {
            TelaTrocarSenha tela = new TelaTrocarSenha(null);
            tela.getContentPane().setBackground(Color.WHITE);
            tela.setVisible(true);
        });
    }
}
