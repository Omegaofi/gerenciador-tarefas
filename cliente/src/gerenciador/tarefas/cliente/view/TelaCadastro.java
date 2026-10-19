package gerenciador.tarefas.cliente.view;

import static gerenciador.tarefas.cliente.view.TelaLogin.adicionarCampo;
import static gerenciador.tarefas.cliente.view.TelaLogin.alinhar;
import static gerenciador.tarefas.cliente.view.TelaLogin.larguraTotal;

import gerenciador.tarefas.cliente.GerenciadorTarefasCliente;
import gerenciador.tarefas.cliente.view.tema.Tema;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import modelDominio.Usuario;

/** RF001: cadastro de usuário como Criador (idealizador) ou Participante (colaborador). */
public class TelaCadastro extends JFrame {

    private final JTextField inputNome = Tema.campoTexto();
    private final JTextField inputEmail = Tema.campoTexto();
    private final JPasswordField inputSenha = Tema.campoSenha();
    private final JPasswordField inputConfirmar = Tema.campoSenha();
    private final JLabel textAlerta = new JLabel(" ");
    private String tipoSelecionado = "criador";
    private JButton botaoCriador;
    private JButton botaoParticipante;

    public TelaCadastro() {
        setTitle("NexProject - Criar conta");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(980, 680);
        setMinimumSize(new Dimension(860, 640));
        setLocationRelativeTo(null);

        JPanel raiz = new JPanel(new GridLayout(1, 2));
        raiz.add(TelaLogin.painelMarca());
        raiz.add(painelFormulario());
        setContentPane(raiz);
    }

    private JPanel painelFormulario() {
        JPanel lado = new JPanel(new GridBagLayout());
        lado.setBackground(Tema.FUNDO);

        JPanel form = TelaLogin.colunaFormulario();
        form.add(Tema.logo(16, false));
        form.add(Box.createVerticalStrut(2));
        form.add(Tema.subtitulo("Plataforma de gestão colaborativa"));
        form.add(Box.createVerticalStrut(22));

        JLabel titulo = new JLabel("Criar conta");
        titulo.setFont(Tema.fonte(Font.BOLD, 17));
        form.add(alinhar(titulo));
        form.add(Box.createVerticalStrut(6));

        textAlerta.setFont(Tema.fonte(Font.BOLD, 12));
        textAlerta.setForeground(Tema.VERMELHO);
        form.add(alinhar(textAlerta));
        form.add(Box.createVerticalStrut(6));

        adicionarCampo(form, "Nome completo", inputNome);
        adicionarCampo(form, "E-mail", inputEmail);
        adicionarCampo(form, "Senha", inputSenha);
        adicionarCampo(form, "Confirmar senha", inputConfirmar);

        form.add(alinhar(Tema.rotuloCampo("Tipo de conta")));
        form.add(Box.createVerticalStrut(5));
        JPanel tipos = new JPanel(new GridLayout(1, 2, 8, 0));
        tipos.setOpaque(false);
        botaoCriador = Tema.botaoPrimario("Criador");
        botaoParticipante = Tema.botaoSecundario("Participante");
        botaoCriador.addActionListener(e -> selecionarTipo("criador"));
        botaoParticipante.addActionListener(e -> selecionarTipo("participante"));
        tipos.add(botaoCriador);
        tipos.add(botaoParticipante);
        form.add(larguraTotal(tipos));
        form.add(Box.createVerticalStrut(20));

        JButton botaoCriar = Tema.botaoPrimario("Criar conta  →");
        botaoCriar.addActionListener(e -> cadastrar());
        form.add(larguraTotal(botaoCriar));
        form.add(Box.createVerticalStrut(16));

        JPanel login = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 0));
        login.setOpaque(false);
        login.add(Tema.subtitulo("Já tem conta?"));
        JLabel linkEntrar = Tema.link("Entrar");
        linkEntrar.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                voltarParaLogin("");
            }
        });
        login.add(linkEntrar);
        form.add(larguraTotal(login));

        lado.add(form);
        getRootPane().setDefaultButton(botaoCriar);
        return lado;
    }

    /** Alterna o botão escolhido entre Criador e Participante (o escolhido fica preto). */
    private void selecionarTipo(String tipo) {
        tipoSelecionado = tipo;
        JPanel tipos = (JPanel) botaoCriador.getParent();
        tipos.removeAll();
        botaoCriador = "criador".equals(tipo) ? Tema.botaoPrimario("Criador") : Tema.botaoSecundario("Criador");
        botaoParticipante = "participante".equals(tipo) ? Tema.botaoPrimario("Participante") : Tema.botaoSecundario("Participante");
        botaoCriador.addActionListener(e -> selecionarTipo("criador"));
        botaoParticipante.addActionListener(e -> selecionarTipo("participante"));
        tipos.add(botaoCriador);
        tipos.add(botaoParticipante);
        tipos.revalidate();
        tipos.repaint();
    }

    private void cadastrar() {
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
        if (senha.length() < 6) {
            textAlerta.setText("A senha precisa ter ao menos 6 caracteres.");
            inputSenha.requestFocus();
            return;
        }
        if (!senha.equals(confirmar)) {
            textAlerta.setText("As senhas não conferem.");
            inputConfirmar.setText("");
            inputConfirmar.requestFocus();
            return;
        }

        Usuario novo = new Usuario(nome, email, senha, null, tipoSelecionado);
        if (GerenciadorTarefasCliente.conexaoCliente.usuarioInserir(novo)) {
            JOptionPane.showMessageDialog(this, "Conta criada com sucesso! Faça login para continuar.");
            voltarParaLogin(email);
        } else {
            textAlerta.setText("Não foi possível criar a conta. Este e-mail já está em uso?");
        }
    }

    private void voltarParaLogin(String email) {
        TelaLogin telaLogin = new TelaLogin(email);
        telaLogin.setBounds(getBounds());
        telaLogin.setVisible(true);
        dispose();
    }
}
