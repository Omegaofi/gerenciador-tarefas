package gerenciador.tarefas.cliente.view;

import gerenciador.tarefas.cliente.GerenciadorTarefasCliente;
import gerenciador.tarefas.cliente.controller.ConexaoController;
import gerenciador.tarefas.cliente.cripto.ChatCifrado;
import gerenciador.tarefas.cliente.cripto.CriptografiaUtil;
import gerenciador.tarefas.cliente.view.componentes.BotaoPrimario;
import gerenciador.tarefas.cliente.view.componentes.BotaoSecundario;
import gerenciador.tarefas.cliente.view.componentes.NexCampoSenha;
import gerenciador.tarefas.cliente.view.componentes.NexRotulo;
import gerenciador.tarefas.cliente.view.componentes.NexSubtitulo;
import gerenciador.tarefas.cliente.view.componentes.NexTexto;
import gerenciador.tarefas.cliente.view.componentes.NexTitulo;
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
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import modelDominio.Usuario;
import org.mindrot.jbcrypt.BCrypt;

/**
 * Troca de senha do usuário logado (Segurança da Informação / RF002).
 *
 * <p>Como fica a senha nova:</p>
 * <ol>
 * <li>O cliente calcula hashLogin = PBKDF2(senha, e-mail) da senha atual e da nova.
 * A senha em si nunca sai deste computador.</li>
 * <li>O servidor confere o hashLogin da senha atual com o BCrypt guardado e,
 * se conferir, grava BCrypt(hashLogin da nova).</li>
 * <li>A chave privada do chat é trancada de novo com a senha nova (PBKDF2 + AES-256-GCM)
 * e vai no mesmo pedido, para o chat continuar abrindo no próximo login.</li>
 * </ol>
 */
public class TelaTrocarSenha extends JDialog {

    private static final Logger logger = Logger.getLogger(TelaTrocarSenha.class.getName());
    private static final int TAMANHO_MINIMO = 8;

    private final NexCampoSenha inputAtual = new NexCampoSenha();
    private final NexCampoSenha inputNova = new NexCampoSenha();
    private final NexCampoSenha inputConfirmar = new NexCampoSenha();
    private final NexTexto textAlerta = new NexTexto(" ");
    private final BotaoPrimario botaoSalvar = new BotaoPrimario("Salvar nova senha");
    private final BotaoSecundario botaoCancelar = new BotaoSecundario("Cancelar");

    public TelaTrocarSenha(Window dono) {
        super(dono, "Alterar senha", ModalityType.APPLICATION_MODAL);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);
        montar();
        pack();
        setLocationRelativeTo(dono);
        getRootPane().setDefaultButton(botaoSalvar);
    }

    private void montar() {
        JPanel fundo = new JPanel(new BorderLayout());
        fundo.setBackground(Tema.FUNDO);
        fundo.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        JPanel card = Tema.card();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Tema.BORDA),
                BorderFactory.createEmptyBorder(22, 24, 22, 24)));

        card.add(alinhar(new NexTitulo("Alterar senha")));
        card.add(Box.createVerticalStrut(4));
        card.add(alinhar(new NexSubtitulo("Use ao menos " + TAMANHO_MINIMO + " caracteres.")));
        card.add(Box.createVerticalStrut(18));

        adicionarCampo(card, "Senha atual", inputAtual);
        adicionarCampo(card, "Nova senha", inputNova);
        adicionarCampo(card, "Confirmar nova senha", inputConfirmar);

        textAlerta.setNegrito(true);
        textAlerta.setTamanho(12);
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

    private void adicionarCampo(JPanel card, String rotulo, NexCampoSenha campo) {
        card.add(alinhar(new NexRotulo(rotulo)));
        card.add(Box.createVerticalStrut(6));
        campo.setPreferredSize(new Dimension(320, 38));
        campo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        card.add(alinhar(campo));
        card.add(Box.createVerticalStrut(14));
    }

    private static <T extends JComponent> T alinhar(T componente) {
        componente.setAlignmentX(Component.LEFT_ALIGNMENT);
        return componente;
    }

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

            String hashAtual = CriptografiaUtil.hashLogin(atual, logado.getEmail());
            String hashNovo = CriptografiaUtil.hashLogin(nova, logado.getEmail());

            Usuario novosDados = chat.trancarCofreComNovaSenha(nova);
            novosDados.setSenha(BCrypt.hashpw(hashNovo, BCrypt.gensalt()));

            if (!conexao.usuarioTrocarSenha(hashAtual, novosDados)) {
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

    private void alerta(String texto, NexCampoSenha campo) {
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
