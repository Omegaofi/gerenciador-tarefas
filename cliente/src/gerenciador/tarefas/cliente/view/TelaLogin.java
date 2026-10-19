package gerenciador.tarefas.cliente.view;

import gerenciador.tarefas.cliente.GerenciadorTarefasCliente;
import gerenciador.tarefas.cliente.view.tema.Tema;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import modelDominio.Usuario;

public class TelaLogin extends JFrame {

    private final JTextField inputEmail = Tema.campoTexto();
    private final JPasswordField inputSenha = Tema.campoSenha();
    private final JLabel textAlerta = new JLabel(" ");
    private JButton botaoEntrar;

    public TelaLogin() {
        this("");
    }

    public TelaLogin(String emailInicial) {
        setTitle("NexProject - Entrar");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(980, 620);
        setMinimumSize(new Dimension(860, 560));
        setLocationRelativeTo(null);

        JPanel raiz = new JPanel(new GridLayout(1, 2));
        raiz.add(painelMarca());
        raiz.add(painelFormulario());
        setContentPane(raiz);

        inputEmail.setText(emailInicial);
        getRootPane().setDefaultButton(botaoEntrar);
    }

    private JPanel painelFormulario() {
        JPanel lado = new JPanel(new GridBagLayout());
        lado.setBackground(Tema.FUNDO);

        JPanel form = colunaFormulario();
        form.add(Tema.logo(16, false));
        form.add(Box.createVerticalStrut(2));
        form.add(Tema.subtitulo("Plataforma de gestão colaborativa"));
        form.add(Box.createVerticalStrut(28));

        JLabel titulo = new JLabel("Entrar na conta");
        titulo.setFont(Tema.fonte(Font.BOLD, 17));
        form.add(alinhar(titulo));
        form.add(Box.createVerticalStrut(6));

        textAlerta.setFont(Tema.fonte(Font.BOLD, 12));
        textAlerta.setForeground(Tema.VERMELHO);
        form.add(alinhar(textAlerta));
        form.add(Box.createVerticalStrut(6));

        adicionarCampo(form, "E-mail", inputEmail);
        adicionarCampo(form, "Senha", inputSenha);
        form.add(Box.createVerticalStrut(8));

        botaoEntrar = Tema.botaoPrimario("Entrar  →");
        botaoEntrar.addActionListener(e -> entrar());
        form.add(larguraTotal(botaoEntrar));
        form.add(Box.createVerticalStrut(16));

        JPanel cadastro = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 0));
        cadastro.setOpaque(false);
        cadastro.add(Tema.subtitulo("Não tem conta?"));
        JLabel linkCadastro = Tema.link("Cadastre-se");
        linkCadastro.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                TelaCadastro telaCadastro = new TelaCadastro();
                telaCadastro.setBounds(getBounds());
                telaCadastro.setVisible(true);
                dispose();
            }
        });
        cadastro.add(linkCadastro);
        form.add(larguraTotal(cadastro));

        lado.add(form);
        return lado;
    }

    private void entrar() {
        String email = inputEmail.getText().trim();
        String senha = new String(inputSenha.getPassword());
        if (email.isEmpty() || senha.isEmpty()) {
            textAlerta.setText("Informe e-mail e senha.");
            return;
        }

        Usuario userLogado = GerenciadorTarefasCliente.conexaoCliente.usuarioLogin(new Usuario(email, senha));
        if (userLogado != null) {
            GerenciadorTarefasCliente.conexaoCliente.setUsuarioLogado(userLogado);
            TelaPrincipal telaPrincipal = new TelaPrincipal();
            telaPrincipal.setVisible(true);
            dispose();
        } else {
            textAlerta.setText("E-mail ou senha incorretos.");
            inputSenha.setText("");
            inputSenha.requestFocus();
        }
    }

    // ---------- Partes reaproveitadas pela TelaCadastro ----------

    /** Painel preto da esquerda, com a marca e os destaques da plataforma. */
    static JPanel painelMarca() {
        JPanel painel = new JPanel(new BorderLayout());
        painel.setBackground(Tema.PRETO);
        painel.setBorder(BorderFactory.createEmptyBorder(36, 40, 30, 40));

        painel.add(Tema.logo(22, true), BorderLayout.NORTH);

        JPanel meio = new JPanel();
        meio.setOpaque(false);
        meio.setLayout(new BoxLayout(meio, BoxLayout.Y_AXIS));
        meio.add(Box.createVerticalGlue());
        JLabel chamada = new JLabel("<html>Colabore em projetos, gerencie tarefas e<br>"
                + "acompanhe entregas — tudo em um só lugar.</html>");
        chamada.setFont(Tema.fonte(Font.PLAIN, 13));
        chamada.setForeground(new Color(0xDD, 0xDD, 0xDD));
        meio.add(chamada);
        meio.add(Box.createVerticalStrut(22));
        for (String destaque : new String[]{"Projetos colaborativos", "Atribuição de tarefas",
            "Entrega e revisão de tarefas", "Gestão de arquivos"}) {
            JLabel item = new JLabel("<html><span style='color:#C6F033'>■</span>&nbsp; " + destaque + "</html>");
            item.setFont(Tema.fonte(Font.PLAIN, 12));
            item.setForeground(Tema.TEXTO_MENU);
            meio.add(item);
            meio.add(Box.createVerticalStrut(10));
        }
        meio.add(Box.createVerticalGlue());
        painel.add(meio, BorderLayout.CENTER);

        JLabel copyright = new JLabel("© 2026 NexProject");
        copyright.setFont(Tema.fonte(Font.PLAIN, 11));
        copyright.setForeground(new Color(0x55, 0x55, 0x55));
        painel.add(copyright, BorderLayout.SOUTH);
        return painel;
    }

    /** Coluna de largura fixa (320px) onde os campos são empilhados. */
    static JPanel colunaFormulario() {
        JPanel form = new JPanel() {
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(320, super.getPreferredSize().height);
            }
        };
        form.setOpaque(false);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        return form;
    }

    static void adicionarCampo(JPanel form, String rotulo, JComponent campo) {
        form.add(alinhar(Tema.rotuloCampo(rotulo)));
        form.add(Box.createVerticalStrut(5));
        form.add(larguraTotal(campo));
        form.add(Box.createVerticalStrut(14));
    }

    static <T extends JComponent> T alinhar(T componente) {
        componente.setAlignmentX(Component.LEFT_ALIGNMENT);
        return componente;
    }

    static <T extends JComponent> T larguraTotal(T componente) {
        componente.setAlignmentX(Component.LEFT_ALIGNMENT);
        componente.setMaximumSize(new Dimension(Integer.MAX_VALUE, componente.getPreferredSize().height));
        return componente;
    }
}
