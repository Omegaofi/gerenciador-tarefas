package gerenciador.tarefas.cliente.view;

import gerenciador.tarefas.cliente.GerenciadorTarefasCliente;
import gerenciador.tarefas.cliente.view.tema.Tema;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import modelDominio.Usuario;

/**
 * Janela padrão do NexProject: menu lateral preto à esquerda e conteúdo à direita.
 * As telas internas (Dashboard, Projetos, Tarefas, Entregas) estendem esta classe
 * e montam a sua parte dentro de {@link #conteudo}.
 */
public abstract class TelaBase extends JFrame {

    public static final String DASHBOARD = "DASHBOARD";
    public static final String PROJETOS = "PROJETOS";
    public static final String TAREFAS = "TAREFAS";
    public static final String ENTREGAS = "ENTREGAS";

    protected final JPanel conteudo;

    protected TelaBase(String tituloJanela, String itemAtivo) {
        setTitle("NexProject - " + tituloJanela);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1000, 640));
        setSize(1120, 700);
        setLocationRelativeTo(null);

        JPanel raiz = new JPanel(new BorderLayout());
        raiz.add(criarMenuLateral(itemAtivo), BorderLayout.WEST);

        conteudo = new JPanel(new BorderLayout(0, 18));
        conteudo.setBackground(Tema.FUNDO);
        conteudo.setBorder(BorderFactory.createEmptyBorder(28, 32, 28, 32));
        raiz.add(conteudo, BorderLayout.CENTER);

        setContentPane(raiz);
    }

    /** Cabeçalho com título, subtítulo e (opcional) um botão de ação à direita. */
    protected JPanel cabecalho(String titulo, String subtitulo, JComponent acao) {
        JPanel painel = new JPanel(new BorderLayout());
        painel.setOpaque(false);

        JPanel textos = new JPanel();
        textos.setOpaque(false);
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));
        textos.add(Tema.titulo(titulo));
        if (subtitulo != null) {
            textos.add(Box.createVerticalStrut(4));
            textos.add(Tema.subtitulo(subtitulo));
        }
        painel.add(textos, BorderLayout.WEST);

        if (acao != null) {
            JPanel direita = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
            direita.setOpaque(false);
            direita.add(acao);
            painel.add(direita, BorderLayout.EAST);
        }
        return painel;
    }

    /** Linha de botões alinhados à esquerda, usada abaixo das tabelas. */
    protected JPanel barraAcoes(JComponent... botoes) {
        JPanel barra = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        barra.setOpaque(false);
        for (int i = 0; i < botoes.length; i++) {
            if (i > 0) {
                barra.add(Box.createHorizontalStrut(10));
            }
            barra.add(botoes[i]);
        }
        return barra;
    }

    /** Tabela com o estilo do NexProject e a coluna de status desenhada como etiqueta. */
    protected JScrollPane tabelaEstilizada(JTable tabela, int colunaStatus) {
        JScrollPane rolagem = new JScrollPane(tabela);
        Tema.estilizarTabela(tabela, rolagem);
        tabela.getColumnModel().getColumn(colunaStatus).setCellRenderer(Tema.rendererStatus());
        return rolagem;
    }

    private JPanel criarMenuLateral(String itemAtivo) {
        Usuario logado = GerenciadorTarefasCliente.conexaoCliente.getUsuarioLogado();
        boolean criador = GerenciadorTarefasCliente.conexaoCliente.isCriador();

        JPanel menu = new JPanel(new BorderLayout());
        menu.setBackground(Tema.PRETO);
        menu.setPreferredSize(new Dimension(230, 0));

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

        topo.add(itemMenu("DASHBOARD", DASHBOARD.equals(itemAtivo), () -> abrir(new TelaPrincipal())));
        if (criador) {
            topo.add(itemMenu("PROJETOS", PROJETOS.equals(itemAtivo), () -> abrir(new TabelaProjeto())));
            topo.add(itemMenu("TAREFAS", TAREFAS.equals(itemAtivo), () -> abrir(new TabelaTarefa())));
        } else {
            topo.add(itemMenu("MINHAS TAREFAS", TAREFAS.equals(itemAtivo), () -> abrir(new TabelaTarefa())));
        }
        topo.add(itemMenu("ENTREGAS", ENTREGAS.equals(itemAtivo), () -> abrir(new TabelaEntrega())));
        menu.add(topo, BorderLayout.NORTH);

        JPanel rodape = new JPanel();
        rodape.setOpaque(false);
        rodape.setLayout(new BoxLayout(rodape, BoxLayout.Y_AXIS));
        rodape.setBorder(BorderFactory.createEmptyBorder(0, 14, 16, 14));
        rodape.add(separador());
        rodape.add(Box.createVerticalStrut(8));
        rodape.add(itemMenu("SAIR", false, this::sair));
        rodape.add(Box.createVerticalStrut(8));
        rodape.add(separador());
        rodape.add(Box.createVerticalStrut(12));
        rodape.add(caixaUsuario(logado));
        menu.add(rodape, BorderLayout.SOUTH);

        return menu;
    }

    private JComponent separador() {
        JPanel linha = new JPanel();
        linha.setBackground(new Color(0x2A, 0x2A, 0x2A));
        linha.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        linha.setPreferredSize(new Dimension(1, 1));
        linha.setAlignmentX(LEFT_ALIGNMENT);
        return linha;
    }

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

    private static String inicial(Usuario logado) {
        String nome = logado != null ? primeiroNome(logado.getNome()) : "Você";
        return nome.substring(0, 1).toUpperCase();
    }

    private static String primeiroNome(String nome) {
        if (nome == null || nome.isBlank()) {
            return "Você";
        }
        return nome.trim().split("\\s+")[0];
    }

    /** Troca a tela atual pela próxima, mantendo a janela no mesmo lugar. */
    protected void abrir(JFrame proxima) {
        proxima.setBounds(getBounds());
        proxima.setVisible(true);
        dispose();
    }

    private void sair() {
        GerenciadorTarefasCliente.conexaoCliente.setUsuarioLogado(null);
        TelaLogin login = new TelaLogin();
        login.setVisible(true);
        dispose();
    }
}
