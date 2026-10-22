package gerenciador.tarefas.cliente.view;

import gerenciador.tarefas.cliente.GerenciadorTarefasCliente;
import gerenciador.tarefas.cliente.view.tema.Tema;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import modelDominio.Usuario;

/**
 * Parte comum das telas com menu lateral (Dashboard, Projetos, Tarefas, Entregas).
 * Os componentes ficam no designer de cada JFrame; aqui entram só o estilo do
 * NexProject e o comportamento do menu.
 */
final class PainelTela {

    static final String DASHBOARD = "DASHBOARD";
    static final String PROJETOS = "PROJETOS";
    static final String TAREFAS = "TAREFAS";
    static final String ENTREGAS = "ENTREGAS";

    /** Os rótulos do menu lateral, como estão no designer de cada tela. */
    record Menu(JLabel logo, JLabel dashboard, JLabel projetos, JLabel tarefas, JLabel entregas,
            JLabel sair, JLabel avatar, JLabel nome, JLabel tipo) {

    }

    private PainelTela() {
    }

    static void configurarJanela(JFrame janela, String tituloJanela) {
        janela.setTitle("NexProject - " + tituloJanela);
        janela.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        janela.setMinimumSize(new Dimension(1000, 640));
        janela.setSize(1120, 700);
        janela.setLocationRelativeTo(null);
    }

    /** Tabela com o estilo do NexProject (o modelo e a coluna de status são definidos depois). */
    static void estilizarTabela(JScrollPane rolagem, JTable tabela) {
        Tema.estilizarTabela(tabela, rolagem);
    }

    /** Estilo do painel preto da esquerda (login e cadastro). */
    static void estilizarMarca(JLabel logo, JLabel chamada, JLabel copyright, JLabel... destaques) {
        Tema.logo(logo, 22, true);
        chamada.setFont(Tema.fonte(Font.PLAIN, 13));
        for (JLabel destaque : destaques) {
            destaque.setFont(Tema.fonte(Font.PLAIN, 12));
        }
        copyright.setFont(Tema.fonte(Font.PLAIN, 11));
    }

    /** Troca a tela atual pela próxima, mantendo a janela no mesmo lugar. */
    static void abrir(JFrame atual, JFrame proxima) {
        proxima.setBounds(atual.getBounds());
        proxima.setVisible(true);
        atual.dispose();
    }

    static void configurarMenu(JFrame janela, String itemAtivo, Menu menu) {
        Usuario logado = GerenciadorTarefasCliente.conexaoCliente.getUsuarioLogado();
        boolean criador = GerenciadorTarefasCliente.conexaoCliente.isCriador();

        Tema.logo(menu.logo(), 18, true);
        menu.logo().setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 0));

        item(menu.dashboard(), "DASHBOARD", DASHBOARD.equals(itemAtivo), () -> abrir(janela, new TelaPrincipal()));
        menu.projetos().setVisible(criador);
        if (criador) {
            item(menu.projetos(), "PROJETOS", PROJETOS.equals(itemAtivo), () -> abrir(janela, new TabelaProjeto()));
        }
        item(menu.tarefas(), criador ? "TAREFAS" : "MINHAS TAREFAS", TAREFAS.equals(itemAtivo),
                () -> abrir(janela, new TabelaTarefa()));
        item(menu.entregas(), "ENTREGAS", ENTREGAS.equals(itemAtivo), () -> abrir(janela, new TabelaEntrega()));
        item(menu.sair(), "SAIR", false, () -> sair(janela));

        menu.avatar().setText(inicial(logado));
        menu.avatar().setFont(Tema.fonte(Font.BOLD, 14));
        menu.nome().setText(logado != null ? primeiroNome(logado.getNome()) : "Você");
        menu.nome().setFont(Tema.fonte(Font.BOLD, 12));
        menu.tipo().setText(logado != null ? Tema.rotuloStatus(logado.getTipo()) : "");
        menu.tipo().setFont(Tema.fonte(Font.PLAIN, 11));
    }

    private static void item(JLabel item, String texto, boolean ativo, Runnable acao) {
        item.setText(texto);
        item.setFont(Tema.fonte(Font.BOLD, 12));
        item.setOpaque(true);
        item.setBackground(ativo ? Tema.LIMAO : Tema.PRETO);
        item.setForeground(ativo ? Tema.TEXTO : Tema.TEXTO_MENU);
        item.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
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

    private static void sair(JFrame janela) {
        GerenciadorTarefasCliente.conexaoCliente.setUsuarioLogado(null);
        TelaLogin login = new TelaLogin();
        login.setVisible(true);
        janela.dispose();
    }
}
