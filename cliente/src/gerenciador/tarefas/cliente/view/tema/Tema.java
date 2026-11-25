package gerenciador.tarefas.cliente.view.tema;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.util.Arrays;
import java.util.List;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.ButtonModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.plaf.basic.BasicTextFieldUI;
import javax.swing.text.JTextComponent;
import javax.swing.plaf.metal.DefaultMetalTheme;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;

/**
 * Identidade visual do NexProject, baseada nos wireframes do trabalho:
 * menu lateral preto, destaque verde-limão, fundo creme e fonte monoespaçada.
 */
public final class Tema {

    public static final Color PRETO = new Color(0x0B, 0x0B, 0x0B);
    public static final Color PRETO_HOVER = new Color(0x26, 0x26, 0x26);
    public static final Color LIMAO = new Color(0xC6, 0xF0, 0x33);
    public static final Color LIMAO_HOVER = new Color(0xB2, 0xDB, 0x22);
    public static final Color FUNDO = new Color(0xF2, 0xEF, 0xEC);
    public static final Color CARD = Color.WHITE;
    public static final Color CAMPO = new Color(0xE9, 0xE7, 0xE2);
    public static final Color BORDA = new Color(0xD9, 0xD5, 0xCF);
    public static final Color TEXTO = new Color(0x11, 0x11, 0x11);
    public static final Color TEXTO_CLARO = new Color(0x6E, 0x6A, 0x64);
    public static final Color TEXTO_MENU = new Color(0xB5, 0xB5, 0xB5);
    public static final Color VERMELHO = new Color(0xD6, 0x3B, 0x3B);
    public static final Color LARANJA = new Color(0xF2, 0x9D, 0x1D);
    public static final Color AZUL = new Color(0x5B, 0xC0, 0xDE);

    private static final String FAMILIA = escolherFonte();

    // Classe só de funções estáticas: não pode ser instanciada
    private Tema() {
    }

    // Escolhe a primeira fonte monoespaçada instalada no computador
    private static String escolherFonte() {
        List<String> instaladas = Arrays.asList(
                GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames());
        for (String preferida : new String[]{"JetBrains Mono", "Cascadia Mono", "Consolas"}) {
            if (instaladas.contains(preferida)) {
                return preferida;
            }
        }
        return Font.MONOSPACED;
    }

    // Fonte do tema no estilo e tamanho pedidos
    public static Font fonte(int estilo, int tamanho) {
        return new Font(FAMILIA, estilo, tamanho);
    }

    /** Deve ser chamado uma vez, antes de abrir a primeira tela. */
    public static void aplicar() {
        try {
            MetalLookAndFeel.setCurrentTheme(new DefaultMetalTheme());
            UIManager.setLookAndFeel(new MetalLookAndFeel());
        } catch (Exception e) {
            e.printStackTrace();
        }
        Font normal = fonte(Font.PLAIN, 13);
        for (String chave : new String[]{"Label.font", "Button.font", "TextField.font", "PasswordField.font",
            "TextArea.font", "ComboBox.font", "Table.font", "TableHeader.font", "OptionPane.messageFont",
            "OptionPane.buttonFont", "List.font", "Spinner.font", "FormattedTextField.font", "ToolTip.font"}) {
            UIManager.put(chave, normal);
        }
        UIManager.put("Panel.background", FUNDO);
        UIManager.put("OptionPane.background", FUNDO);
        UIManager.put("ComboBox.selectionBackground", LIMAO);
        UIManager.put("ComboBox.selectionForeground", TEXTO);
        UIManager.put("List.selectionBackground", LIMAO);
        UIManager.put("List.selectionForeground", TEXTO);
        UIManager.put("TextField.selectionBackground", LIMAO);
        UIManager.put("ScrollBar.width", 10);
    }

    // ---------- Textos ----------
    // As telas usam JLabel comuns (criados no Design do NetBeans) e chamam estes métodos para aplicar o visual.

    /** Logotipo "NEX" + "PROJECT" (PROJECT em verde-limão). */
    public static JLabel logo(int tamanho, boolean fundoEscuro) {
        JLabel logo = new JLabel();
        estilizarLogo(logo, tamanho, fundoEscuro);
        return logo;
    }

    // Transforma um JLabel no logotipo; fundoEscuro = true deixa o "NEX" branco
    public static void estilizarLogo(JLabel logo, int tamanho, boolean fundoEscuro) {
        String corNex = fundoEscuro ? "#FFFFFF" : "#0B0B0B";
        logo.setText("<html><span style='color:" + corNex + "'>NEX</span>"
                + "<span style='color:#C6F033'>PROJECT</span></html>");
        logo.setFont(fonte(Font.BOLD, tamanho));
    }

    // Título de tela
    public static JLabel titulo(String texto) {
        JLabel titulo = new JLabel(texto);
        estilizarTitulo(titulo);
        return titulo;
    }

    // Título padrão: negrito, 22pt
    public static void estilizarTitulo(JLabel titulo) {
        estilizarTitulo(titulo, 22);
    }

    // Título em negrito com o tamanho pedido
    public static void estilizarTitulo(JLabel titulo, int tamanho) {
        titulo.setFont(fonte(Font.BOLD, tamanho));
        titulo.setForeground(TEXTO);
    }

    // Texto de apoio cinza
    public static JLabel subtitulo(String texto) {
        JLabel subtitulo = new JLabel(texto);
        estilizarSubtitulo(subtitulo);
        return subtitulo;
    }

    // Texto de apoio: cinza, 12pt
    public static void estilizarSubtitulo(JLabel subtitulo) {
        subtitulo.setFont(fonte(Font.PLAIN, 12));
        subtitulo.setForeground(TEXTO_CLARO);
    }

    // Texto comum na fonte do tema, com tamanho e negrito escolhidos
    public static void estilizarTexto(JLabel texto, int tamanho, boolean negrito) {
        texto.setFont(fonte(negrito ? Font.BOLD : Font.PLAIN, tamanho));
        texto.setForeground(TEXTO);
    }

    /** Rótulo pequeno em caixa alta, usado acima dos campos (ex.: "E-MAIL"). */
    public static JLabel rotuloCampo(String texto) {
        JLabel rotulo = new JLabel(texto);
        estilizarRotulo(rotulo);
        return rotulo;
    }

    // Rótulo: cinza, negrito, 11pt e sempre em maiúsculas
    public static void estilizarRotulo(JLabel rotulo) {
        rotulo.setFont(fonte(Font.BOLD, 11));
        rotulo.setForeground(TEXTO_CLARO);
        sempreMaiusculo(rotulo);
    }

    /** Texto clicável, como "Cadastre-se". */
    public static JLabel link(String texto) {
        JLabel link = new JLabel(texto);
        estilizarLink(link);
        return link;
    }

    // Link: negrito, sublinhado e com a mãozinha do mouse
    public static void estilizarLink(JLabel link) {
        java.util.Map<java.awt.font.TextAttribute, Object> atributos = new java.util.HashMap<>(fonte(Font.BOLD, 12).getAttributes());
        atributos.put(java.awt.font.TextAttribute.UNDERLINE, java.awt.font.TextAttribute.UNDERLINE_ON);
        link.setFont(fonte(Font.BOLD, 12).deriveFont(atributos));
        link.setForeground(TEXTO);
        link.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    // Converte o texto para maiúsculas agora e toda vez que ele mudar (setText)
    private static void sempreMaiusculo(JComponent componente) {
        if (componente.getClientProperty("nex.maiusculo") != null) {
            return;   // já está configurado
        }
        componente.putClientProperty("nex.maiusculo", Boolean.TRUE);
        componente.addPropertyChangeListener("text", e -> converterParaMaiusculo(componente));
        converterParaMaiusculo(componente);
    }

    // Troca o texto do componente pela versão em maiúsculas (se ainda não estiver)
    private static void converterParaMaiusculo(JComponent componente) {
        if (componente instanceof JLabel rotulo && rotulo.getText() != null
                && !rotulo.getText().equals(rotulo.getText().toUpperCase())) {
            rotulo.setText(rotulo.getText().toUpperCase());
        } else if (componente instanceof AbstractButton botao && botao.getText() != null
                && !botao.getText().equals(botao.getText().toUpperCase())) {
            botao.setText(botao.getText().toUpperCase());
        }
    }

    // ---------- Botões ----------

    public static final String PRIMARIO = "primario";
    public static final String DESTAQUE = "destaque";
    public static final String SECUNDARIO = "secundario";
    public static final String PERIGO = "perigo";

    // Botão preto
    public static JButton botaoPrimario(String texto) {
        return botao(texto, PRIMARIO);
    }

    // Botão verde-limão
    public static JButton botaoDestaque(String texto) {
        return botao(texto, DESTAQUE);
    }

    // Botão branco com borda
    public static JButton botaoSecundario(String texto) {
        return botao(texto, SECUNDARIO);
    }

    // Botão vermelho (excluir)
    public static JButton botaoPerigo(String texto) {
        return botao(texto, PERIGO);
    }

    // Cria um JButton comum já com o visual do estilo pedido
    private static JButton botao(String texto, String estilo) {
        JButton botao = new JButton(texto);
        estilizarBotao(botao, estilo);
        return botao;
    }

    /**
     * Deixa um botão comum (JButton) reto e plano, nas cores do estilo:
     * PRIMARIO (preto), DESTAQUE (verde-limão), SECUNDARIO (branco com borda) ou PERIGO (vermelho).
     * Pode ser chamado de novo para trocar o estilo (ex.: botão selecionado).
     */
    public static void estilizarBotao(AbstractButton botao, String estilo) {
        Color fundo;
        Color hover;
        Color borda;
        switch (estilo == null ? PRIMARIO : estilo) {
            case DESTAQUE:
                fundo = LIMAO; hover = LIMAO_HOVER; borda = null; botao.setForeground(TEXTO);
                break;
            case SECUNDARIO:
                fundo = CARD; hover = CAMPO; borda = BORDA; botao.setForeground(TEXTO);
                break;
            case PERIGO:
                fundo = CARD; hover = new Color(0xFB, 0xE9, 0xE9); borda = VERMELHO; botao.setForeground(VERMELHO);
                break;
            default:
                fundo = PRETO; hover = PRETO_HOVER; borda = null; botao.setForeground(Color.WHITE);
        }
        botao.setUI(new BotaoUI(fundo, hover, borda));
        botao.setFont(fonte(Font.BOLD, 12));
        botao.setContentAreaFilled(false);
        botao.setFocusPainted(false);
        botao.setBorderPainted(false);
        botao.setOpaque(false);
        botao.setRolloverEnabled(true);
        botao.setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 18));
        botao.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        sempreMaiusculo(botao);   // texto dos botões sempre em caixa alta, como nos wireframes
        botao.repaint();
    }

    /** Desenha o botão: fundo (mais escuro com o mouse em cima), borda opcional e o texto. */
    private static final class BotaoUI extends BasicButtonUI {

        private final Color fundo;
        private final Color hover;
        private final Color borda;

        // Guarda as cores do estilo escolhido
        BotaoUI(Color fundo, Color hover, Color borda) {
            this.fundo = fundo;
            this.hover = hover;
            this.borda = borda;
        }

        // Pinta o fundo e a borda; o texto é desenhado pelo BasicButtonUI
        @Override
        public void paint(Graphics g, JComponent c) {
            ButtonModel modelo = ((AbstractButton) c).getModel();
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setColor(modelo.isRollover() || modelo.isPressed() ? hover : fundo);
            g2.fillRect(0, 0, c.getWidth(), c.getHeight());
            if (borda != null) {
                g2.setColor(borda);
                g2.setStroke(new BasicStroke(1));
                g2.drawRect(0, 0, c.getWidth() - 1, c.getHeight() - 1);
            }
            g2.dispose();
            super.paint(g, c);
        }
    }

    // ---------- Campos ----------

    // Borda fina dos campos, com espaço interno
    private static Border bordaCampo() {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDA),
                BorderFactory.createEmptyBorder(8, 10, 8, 10));
    }

    // Aplica cor, borda e fonte do tema em um campo
    public static void estilizarCampo(JComponent campo) {
        campo.setBackground(CAMPO);
        campo.setForeground(TEXTO);
        campo.setBorder(bordaCampo());
        campo.setFont(fonte(Font.PLAIN, 13));
        if (campo instanceof JTextComponent texto) {
            texto.setCaretColor(TEXTO);
        }
    }

    // Área de texto: quebra a linha sozinha, nas palavras, e usa o estilo dos campos
    public static void estilizarAreaTexto(JTextArea area) {
        if (area.getRows() == 0) {
            area.setRows(4);
        }
        if (area.getColumns() == 0) {
            area.setColumns(20);
        }
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        estilizarCampo(area);
    }

    /** Mostra um texto cinza (placeholder) no campo enquanto ele está vazio, ex.: "Buscar projetos...". */
    public static void dica(JTextField campo, String dica) {
        campo.setUI(new BasicTextFieldUI() {
            // Desenha o campo normalmente e, se estiver vazio, escreve a dica em cinza
            @Override
            protected void paintSafely(Graphics g) {
                super.paintSafely(g);
                if (campo.getText().isEmpty() && dica != null && !dica.isEmpty()) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                    g2.setColor(TEXTO_CLARO);
                    g2.setFont(campo.getFont());
                    Insets margem = campo.getInsets();
                    int y = (campo.getHeight() + g2.getFontMetrics().getAscent() - g2.getFontMetrics().getDescent()) / 2;
                    g2.drawString(dica, margem.left, y);
                    g2.dispose();
                }
            }
        });
        estilizarCampo(campo);
        campo.repaint();
    }

    // Campo de texto no estilo do tema
    public static JTextField campoTexto() {
        JTextField campo = new JTextField();
        estilizarCampo(campo);
        return campo;
    }

    // Campo de senha no estilo do tema
    public static JPasswordField campoSenha() {
        JPasswordField campo = new JPasswordField();
        estilizarCampo(campo);
        return campo;
    }

    // Área de texto com a quantidade de linhas pedida
    public static JTextArea areaTexto(int linhas) {
        JTextArea area = new JTextArea(linhas, 20);
        estilizarAreaTexto(area);
        return area;
    }

    // Aplica o estilo do tema em um combo
    public static <T> void estilizarCombo(JComboBox<T> combo) {
        combo.setBackground(CAMPO);
        combo.setForeground(TEXTO);
        combo.setFont(fonte(Font.PLAIN, 13));
        combo.setBorder(BorderFactory.createLineBorder(BORDA));
        combo.setPreferredSize(new Dimension(combo.getPreferredSize().width, 36));
    }

    // ---------- Painéis ----------

    /** Cartão branco com borda fina, como os do Dashboard. */
    public static JPanel card() {
        JPanel painel = new JPanel();
        painel.setBackground(CARD);
        painel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDA),
                BorderFactory.createEmptyBorder(14, 16, 14, 16)));
        return painel;
    }

    /** Quadradinho colorido usado na frente dos cards de projeto. */
    public static JComponent marcador(Color cor) {
        JComponent quadrado = new JComponent() {
            @Override
            protected void paintComponent(Graphics g) {
                g.setColor(cor);
                g.fillRect(0, 0, 10, 10);
            }
        };
        quadrado.setPreferredSize(new Dimension(10, 10));
        return quadrado;
    }

    /** Barra de progresso fina, verde-limão sobre cinza. */
    public static JComponent barraProgresso(double fracao) {
        JComponent barra = new JComponent() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(CAMPO);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(LIMAO);
                g2.fillRect(0, 0, (int) (getWidth() * Math.max(0, Math.min(1, fracao))), getHeight());
                g2.dispose();
            }
        };
        barra.setPreferredSize(new Dimension(100, 5));
        return barra;
    }

    // ---------- Status ----------

    /** Converte o valor do banco ("em_andamento") em texto de tela ("Em andamento"). */
    public static String rotuloStatus(String status) {
        if (status == null) {
            return "";
        }
        switch (status) {
            case "planejado": return "Planejamento";
            case "em_andamento": return "Em andamento";
            case "concluido": return "Concluído";
            case "cancelado": return "Cancelado";
            case "pendente": return "Pendente";
            case "concluida": return "Concluída";
            case "atrasada": return "Atrasada";
            case "enviada": return "Aguardando revisão";
            case "em_revisao": return "Em revisão";
            case "aprovada": return "Aprovada";
            case "rejeitada": return "Rejeitada";
            case "criador": return "Criador";
            case "participante": return "Participante";
            default: return status;
        }
    }

    /** Etiqueta de status, nas cores dos wireframes. */
    public static JLabel badge(String status) {
        JLabel label = new JLabel(rotuloStatus(status).toUpperCase(), SwingConstants.CENTER);
        label.setFont(fonte(Font.BOLD, 10));
        label.setOpaque(true);
        Color fundo;
        Color texto;
        Color borda;
        switch (status == null ? "" : status) {
            case "em_andamento":
            case "enviada":
            case "em_revisao":
                fundo = PRETO; texto = Color.WHITE; borda = PRETO;
                break;
            case "concluido":
            case "concluida":
            case "aprovada":
                fundo = LIMAO; texto = TEXTO; borda = LIMAO;
                break;
            case "atrasada":
            case "cancelado":
            case "rejeitada":
                fundo = Color.WHITE; texto = VERMELHO; borda = VERMELHO;
                break;
            default:
                fundo = Color.WHITE; texto = TEXTO_CLARO; borda = BORDA;
        }
        label.setBackground(fundo);
        label.setForeground(texto);
        label.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borda),
                BorderFactory.createEmptyBorder(3, 8, 3, 8)));
        return label;
    }

    // Cor do projeto conforme o status
    public static Color corProjeto(String status) {
        switch (status == null ? "" : status) {
            case "em_andamento": return LIMAO;
            case "planejado": return LARANJA;
            case "concluido": return AZUL;
            default: return BORDA;
        }
    }

    // ---------- Tabelas ----------

    // Aplica o estilo do tema em uma tabela (linhas, cabeçalho, seleção)
    public static void estilizarTabela(JTable tabela, JScrollPane rolagem) {
        tabela.setRowHeight(38);
        tabela.setShowVerticalLines(false);
        tabela.setGridColor(BORDA);
        tabela.setIntercellSpacing(new Dimension(0, 1));
        tabela.setSelectionBackground(new Color(0xEE, 0xF9, 0xC8));
        tabela.setSelectionForeground(TEXTO);
        tabela.setBackground(CARD);
        tabela.setForeground(TEXTO);
        tabela.setFillsViewportHeight(true);
        tabela.setFont(fonte(Font.PLAIN, 13));
        tabela.setAutoCreateColumnsFromModel(true);
        // A primeira coluna (título) ganha mais espaço que as demais
        tabela.addPropertyChangeListener("model", e -> {
            if (tabela.getColumnCount() > 0) {
                tabela.getColumnModel().getColumn(0).setPreferredWidth(260);
            }
        });
        if (tabela.getColumnCount() > 0) {
            tabela.getColumnModel().getColumn(0).setPreferredWidth(260);
        }

        tabela.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(JTable t, Object valor, boolean selecionada,
                    boolean foco, int linha, int coluna) {
                JLabel c = (JLabel) super.getTableCellRendererComponent(t, valor, selecionada, false, linha, coluna);
                c.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));
                return c;
            }
        });

        JTableHeader cabecalho = tabela.getTableHeader();
        cabecalho.setReorderingAllowed(false);
        cabecalho.setPreferredSize(new Dimension(0, 34));
        cabecalho.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(JTable t, Object valor, boolean selecionada,
                    boolean foco, int linha, int coluna) {
                JLabel c = (JLabel) super.getTableCellRendererComponent(t, valor, false, false, linha, coluna);
                c.setText(valor == null ? "" : valor.toString().toUpperCase());
                c.setFont(fonte(Font.BOLD, 11));
                c.setForeground(TEXTO_CLARO);
                c.setBackground(FUNDO);
                c.setOpaque(true);
                c.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 1, 0, BORDA),
                        BorderFactory.createEmptyBorder(0, 12, 0, 12)));
                return c;
            }
        });

        rolagem.setBorder(BorderFactory.createLineBorder(BORDA));
        rolagem.getViewport().setBackground(CARD);
    }

    /** Renderizador que mostra o valor da coluna como etiqueta de status. */
    public static DefaultTableCellRenderer rendererStatus() {
        return new DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(JTable t, Object valor, boolean selecionada,
                    boolean foco, int linha, int coluna) {
                JPanel celula = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 12, 9));
                celula.setBackground(selecionada ? t.getSelectionBackground() : t.getBackground());
                celula.add(badge(valor == null ? "" : valor.toString()));
                return celula;
            }
        };
    }

    // Margem padrão do conteúdo das telas
    public static Insets margemConteudo() {
        return new Insets(28, 32, 28, 32);
    }
}
