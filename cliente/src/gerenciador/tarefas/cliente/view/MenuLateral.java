package gerenciador.tarefas.cliente.view;

import gerenciador.tarefas.cliente.GerenciadorTarefasCliente;
import gerenciador.tarefas.cliente.view.tema.Tema;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import modelDominio.Usuario;

/**
 * Menu lateral preto do NexProject, usado por Dashboard, Projetos, Tarefas, Entregas e Chat.
 * Nos formulários do NetBeans entra como "Custom Creation Code": {@code new MenuLateral(MenuLateral.PROJETOS)}.
 */
public class MenuLateral extends JPanel {

    public static final String DASHBOARD = "DASHBOARD";
    public static final String PROJETOS = "PROJETOS";
    public static final String TAREFAS = "TAREFAS";
    public static final String ENTREGAS = "ENTREGAS";
    public static final String CHAT = "CHAT";
    public static final String EXPLORAR = "EXPLORAR";

    private String itemAtivo = DASHBOARD;

    /** Usado pelo Design do NetBeans; troque o item marcado pela propriedade itemAtivo. */
    public MenuLateral() {
        this(DASHBOARD);
    }

    // Cria o menu marcando qual item está ativo
    public MenuLateral(String itemAtivo) {
        super(new BorderLayout());
        setItemAtivo(itemAtivo);
    }

    public String getItemAtivo() {
        return itemAtivo;
    }

    /** DASHBOARD, PROJETOS, TAREFAS, ENTREGAS, CHAT ou EXPLORAR. */
    public void setItemAtivo(String itemAtivo) {
        this.itemAtivo = itemAtivo == null ? DASHBOARD : itemAtivo.toUpperCase();
        removeAll();
        montar(this.itemAtivo);
        revalidate();
        repaint();
    }

    // Monta o menu: logo, itens de navegação, usuário logado, trocar senha e sair
    private void montar(String itemAtivo) {
        // conexaoCliente é null só no editor visual do NetBeans (tempo de projeto)
        boolean emExecucao = GerenciadorTarefasCliente.conexaoCliente != null;
        Usuario logado = emExecucao ? GerenciadorTarefasCliente.conexaoCliente.getUsuarioLogado() : null;
        boolean criador = !emExecucao || GerenciadorTarefasCliente.conexaoCliente.isCriador();

        setBackground(Tema.PRETO);
        setPreferredSize(new Dimension(230, 0));

        JPanel topo = new JPanel();
        topo.setOpaque(false);
        topo.setLayout(new BoxLayout(topo, BoxLayout.Y_AXIS));
        topo.setBorder(BorderFactory.createEmptyBorder(22, 14, 0, 14));

        JLabel logo = Tema.logo(18, true);
        logo.setBorder(BorderFactory.createEmptyBorder(0, 8, 20, 0));
        logo.setAlignmentX(LEFT_ALIGNMENT);
        topo.add(logo);
        topo.add(separador());
        topo.add(Box.createVerticalStrut(14));

        topo.add(itemMenu("DASHBOARD", DASHBOARD.equals(itemAtivo), () -> abrir(new TelaPrincipal1())));
        if (criador) {
            topo.add(itemMenu("PROJETOS", PROJETOS.equals(itemAtivo), () -> abrir(new TabelaProjeto1())));
            topo.add(itemMenu("TAREFAS", TAREFAS.equals(itemAtivo), () -> abrir(new TabelaTarefa1())));
        } else {
            topo.add(itemMenu("MINHAS TAREFAS", TAREFAS.equals(itemAtivo), () -> abrir(new TabelaTarefa1())));
            topo.add(itemMenu("EXPLORAR PROJETOS", EXPLORAR.equals(itemAtivo), () -> abrir(new FormProcura())));
        }
        topo.add(itemMenu("ENTREGAS", ENTREGAS.equals(itemAtivo), () -> abrir(new TabelaEntrega1())));
        topo.add(itemMenu("CHAT", CHAT.equals(itemAtivo), () -> abrir(new TelaChat1())));
        add(topo, BorderLayout.NORTH);

        JPanel rodape = new JPanel();
        rodape.setOpaque(false);
        rodape.setLayout(new BoxLayout(rodape, BoxLayout.Y_AXIS));
        rodape.setBorder(BorderFactory.createEmptyBorder(0, 14, 16, 14));
        rodape.add(separador());
        rodape.add(Box.createVerticalStrut(8));
        rodape.add(itemMenu("ALTERAR SENHA", false, this::alterarSenha));
        rodape.add(itemMenu("SAIR", false, this::sair));
        rodape.add(Box.createVerticalStrut(8));
        rodape.add(separador());
        rodape.add(Box.createVerticalStrut(12));
        rodape.add(caixaUsuario(logado));
        add(rodape, BorderLayout.SOUTH);
    }

    // Linha separadora do menu
    private JComponent separador() {
        JPanel linha = new JPanel();
        linha.setBackground(new Color(0x2A, 0x2A, 0x2A));
        linha.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        linha.setPreferredSize(new Dimension(1, 1));
        linha.setAlignmentX(LEFT_ALIGNMENT);
        return linha;
    }

    // Cria um item clicável do menu (verde quando ativo)
    private JComponent itemMenu(String texto, boolean ativo, Runnable acao) {
        JLabel item = new JLabel(texto);
        item.setFont(Tema.fonte(Font.BOLD, 12));
        item.setOpaque(true);
        item.setBackground(ativo ? Tema.LIMAO : Tema.PRETO);
        item.setForeground(ativo ? Tema.TEXTO : Tema.TEXTO_MENU);
        item.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
        item.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        item.setAlignmentX(LEFT_ALIGNMENT);
        item.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        item.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!ativo) {
                    acao.run();
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                if (!ativo) {
                    item.setForeground(Color.WHITE);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (!ativo) {
                    item.setForeground(Tema.TEXTO_MENU);
                }
            }
        });
        return item;
    }

    // Caixa com a inicial e o nome do usuário logado
    private JComponent caixaUsuario(Usuario logado) {
        JPanel caixa = new JPanel(new BorderLayout(10, 0));
        caixa.setOpaque(false);
        caixa.setAlignmentX(LEFT_ALIGNMENT);
        caixa.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        JLabel avatar = new JLabel(inicial(logado), JLabel.CENTER);
        avatar.setOpaque(true);
        avatar.setBackground(Tema.LIMAO);
        avatar.setForeground(Tema.TEXTO);
        avatar.setFont(Tema.fonte(Font.BOLD, 14));
        avatar.setPreferredSize(new Dimension(30, 30));
        caixa.add(avatar, BorderLayout.WEST);

        JPanel textos = new JPanel();
        textos.setOpaque(false);
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));
        JLabel nome = new JLabel(logado != null ? primeiroNome(logado.getNome()) : "Você");
        nome.setFont(Tema.fonte(Font.BOLD, 12));
        nome.setForeground(Color.WHITE);
        JLabel tipo = new JLabel(logado != null ? Tema.rotuloStatus(logado.getTipo()) : "");
        tipo.setFont(Tema.fonte(Font.PLAIN, 11));
        tipo.setForeground(Tema.TEXTO_MENU);
        textos.add(nome);
        textos.add(tipo);
        caixa.add(textos, BorderLayout.CENTER);
        return caixa;
    }

    // Primeira letra do nome, para o "avatar"
    private static String inicial(Usuario logado) {
        String nome = logado != null ? primeiroNome(logado.getNome()) : "Você";
        return nome.substring(0, 1).toUpperCase();
    }

    // Primeiro nome do usuário
    private static String primeiroNome(String nome) {
        if (nome == null || nome.isBlank()) {
            return "Você";
        }
        return nome.trim().split("\\s+")[0];
    }

    /** Troca a tela atual pela próxima, mantendo a janela no mesmo lugar. */
    public static void abrir(Component origem, JFrame proxima) {
        Window atual = SwingUtilities.getWindowAncestor(origem);
        if (atual != null) {
            proxima.setBounds(atual.getBounds());
        }
        proxima.setVisible(true);
        if (atual != null) {
            atual.dispose();
        }
    }

    // Abre outra tela no lugar da atual
    private void abrir(JFrame proxima) {
        abrir(this, proxima);
    }

    // Abre a janela de trocar senha
    private void alterarSenha() {
        new TelaTrocarSenha(SwingUtilities.getWindowAncestor(this)).setVisible(true);
    }

    // Logout: esquece o usuário, abre o login e fecha a tela atual
    private void sair() {
        GerenciadorTarefasCliente.conexaoCliente.setUsuarioLogado(null);
        new TelaLogin1().setVisible(true);
        Window atual = SwingUtilities.getWindowAncestor(this);
        if (atual != null) {
            atual.dispose();
        }
    }
}
