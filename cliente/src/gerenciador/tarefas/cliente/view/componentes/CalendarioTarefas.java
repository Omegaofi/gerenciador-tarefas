package gerenciador.tarefas.cliente.view.componentes;

import gerenciador.tarefas.cliente.view.tema.Tema;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import modelDominio.Tarefa;

/**
 * Calendário mensal com as tarefas pelo prazo de entrega.
 *
 * <p>O Criador vê as tarefas de todos os colaboradores dos seus projetos (com o nome do
 * responsável em cada tarefa); o Colaborador vê só as dele. Clique num dia para ver a lista
 * completa abaixo do mês.</p>
 *
 * <p>É um JavaBean (construtor sem parâmetros), então pode ser usado no Design do NetBeans.</p>
 */
public class CalendarioTarefas extends JPanel {

    private static final String[] MESES = {"Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho",
        "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"};
    private static final String[] DIAS_SEMANA = {"DOM", "SEG", "TER", "QUA", "QUI", "SEX", "SÁB"};
    private static final String[] NOMES_DIAS = {"segunda", "terça", "quarta", "quinta", "sexta", "sábado", "domingo"};
    private static final int TAREFAS_POR_DIA = 2;
    private static final Color VERMELHO_CLARO = new Color(0xFB, 0xE9, 0xE9);

    private final Map<LocalDate, List<Tarefa>> porDia = new TreeMap<>();
    private boolean mostrarResponsavel;
    private YearMonth mesExibido = YearMonth.now();
    private LocalDate diaSelecionado = LocalDate.now();
    private Runnable aoVerTodas;

    private final JLabel lblMes = new JLabel();
    private final JLabel lblResumo = new JLabel();
    private final JPanel grade = new JPanel(new GridLayout(0, 7, 4, 4));
    private final JPanel detalhe = new JPanel();

    public CalendarioTarefas() {
        super(new BorderLayout(0, 14));
        setBackground(Tema.CARD);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Tema.BORDA),
                BorderFactory.createEmptyBorder(16, 18, 16, 18)));
        setAlignmentX(LEFT_ALIGNMENT);

        add(cabecalho(), BorderLayout.NORTH);

        JPanel centro = new JPanel(new BorderLayout(0, 6));
        centro.setOpaque(false);
        centro.add(linhaDiasSemana(), BorderLayout.NORTH);
        grade.setOpaque(false);
        centro.add(grade, BorderLayout.CENTER);
        add(centro, BorderLayout.CENTER);

        detalhe.setOpaque(false);
        detalhe.setLayout(new BoxLayout(detalhe, BoxLayout.Y_AXIS));
        add(detalhe, BorderLayout.SOUTH);

        redesenhar();
    }

    // ---------- API ----------

    /**
     * Define as tarefas exibidas.
     *
     * @param mostrarResponsavel true para o Criador: cada tarefa mostra quem é o responsável
     */
    public void setTarefas(List<Tarefa> tarefas, boolean mostrarResponsavel) {
        this.mostrarResponsavel = mostrarResponsavel;
        porDia.clear();
        for (Tarefa t : tarefas) {
            LocalDate dia = paraData(t.getPrazo());
            if (dia != null) {
                porDia.computeIfAbsent(dia, d -> new ArrayList<>()).add(t);
            }
        }
        redesenhar();
    }

    /** Ação do link "Ver todas as tarefas" (por exemplo, abrir a tela de Tarefas). */
    public void setAoVerTodas(Runnable aoVerTodas) {
        this.aoVerTodas = aoVerTodas;
        redesenhar();
    }

    public boolean isMostrarResponsavel() {
        return mostrarResponsavel;
    }

    public void setMostrarResponsavel(boolean mostrarResponsavel) {
        this.mostrarResponsavel = mostrarResponsavel;
        redesenhar();
    }

    // ---------- Montagem ----------

    private JComponent cabecalho() {
        JPanel topo = new JPanel(new BorderLayout(12, 0));
        topo.setOpaque(false);

        JPanel textos = new JPanel();
        textos.setOpaque(false);
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));
        JLabel titulo = new NexRotulo("Calendário de tarefas");
        titulo.setForeground(Tema.TEXTO);
        textos.add(titulo);
        textos.add(Box.createVerticalStrut(4));
        lblResumo.setFont(Tema.fonte(Font.PLAIN, 12));
        lblResumo.setForeground(Tema.TEXTO_CLARO);
        textos.add(lblResumo);
        topo.add(textos, BorderLayout.WEST);

        JPanel navegacao = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        navegacao.setOpaque(false);
        navegacao.add(botaoNavegar("‹", () -> trocarMes(-1)));
        lblMes.setFont(Tema.fonte(Font.BOLD, 14));
        lblMes.setForeground(Tema.TEXTO);
        lblMes.setHorizontalAlignment(SwingConstants.CENTER);
        lblMes.setPreferredSize(new Dimension(170, 30));
        navegacao.add(lblMes);
        navegacao.add(botaoNavegar("›", () -> trocarMes(1)));
        JButton hoje = new BotaoSecundario("Hoje");
        hoje.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
        hoje.addActionListener(e -> {
            mesExibido = YearMonth.now();
            diaSelecionado = LocalDate.now();
            redesenhar();
        });
        navegacao.add(Box.createHorizontalStrut(6));
        navegacao.add(hoje);
        topo.add(navegacao, BorderLayout.EAST);
        return topo;
    }

    private JButton botaoNavegar(String seta, Runnable acao) {
        JButton botao = new BotaoSecundario(seta);
        botao.setFont(Tema.fonte(Font.BOLD, 16));
        botao.setBorder(BorderFactory.createEmptyBorder(2, 12, 4, 12));
        botao.addActionListener(e -> acao.run());
        return botao;
    }

    private JComponent linhaDiasSemana() {
        JPanel linha = new JPanel(new GridLayout(1, 7, 4, 0));
        linha.setOpaque(false);
        for (String dia : DIAS_SEMANA) {
            JLabel l = new JLabel(dia);
            l.setFont(Tema.fonte(Font.BOLD, 10));
            l.setForeground(Tema.TEXTO_CLARO);
            l.setBorder(BorderFactory.createEmptyBorder(0, 6, 0, 0));
            linha.add(l);
        }
        return linha;
    }

    private void trocarMes(int delta) {
        mesExibido = mesExibido.plusMonths(delta);
        LocalDate hoje = LocalDate.now();
        diaSelecionado = YearMonth.from(hoje).equals(mesExibido) ? hoje : mesExibido.atDay(1);
        redesenhar();
    }

    private void redesenhar() {
        lblMes.setText(MESES[mesExibido.getMonthValue() - 1] + " " + mesExibido.getYear());
        atualizarResumo();

        grade.removeAll();
        // a semana começa no domingo: recua até o domingo anterior ao dia 1
        LocalDate primeiro = mesExibido.atDay(1);
        LocalDate inicio = primeiro.minusDays(primeiro.getDayOfWeek().getValue() % 7);
        int semanas = (int) Math.ceil((primeiro.getDayOfWeek().getValue() % 7 + mesExibido.lengthOfMonth()) / 7.0);
        for (int i = 0; i < semanas * 7; i++) {
            grade.add(celula(inicio.plusDays(i)));
        }

        montarDetalhe();
        revalidate();
        repaint();
    }

    private void atualizarResumo() {
        int noMes = 0;
        int atrasadas = 0;
        for (Map.Entry<LocalDate, List<Tarefa>> e : porDia.entrySet()) {
            if (YearMonth.from(e.getKey()).equals(mesExibido)) {
                noMes += e.getValue().size();
            }
            for (Tarefa t : e.getValue()) {
                if ("atrasada".equals(statusEfetivo(t, e.getKey()))) {
                    atrasadas++;
                }
            }
        }
        String quem = mostrarResponsavel ? "Tarefas dos colaboradores" : "Suas tarefas";
        lblResumo.setText(quem + " · " + noMes + (noMes == 1 ? " tarefa neste mês" : " tarefas neste mês")
                + (atrasadas > 0 ? " · " + atrasadas + (atrasadas == 1 ? " atrasada" : " atrasadas") : ""));
    }

    private JComponent celula(LocalDate dia) {
        boolean doMes = YearMonth.from(dia).equals(mesExibido);
        boolean hoje = dia.equals(LocalDate.now());
        boolean selecionado = dia.equals(diaSelecionado);
        List<Tarefa> tarefas = porDia.getOrDefault(dia, List.of());

        JPanel cel = new JPanel();
        cel.setLayout(new BoxLayout(cel, BoxLayout.Y_AXIS));
        cel.setBackground(selecionado ? new Color(0xF7, 0xFC, 0xE6) : (doMes ? Tema.CARD : Tema.FUNDO));
        cel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(selecionado ? Tema.LIMAO_HOVER : Tema.BORDA, selecionado ? 2 : 1),
                BorderFactory.createEmptyBorder(selecionado ? 4 : 5, selecionado ? 5 : 6, 5, 6)));
        cel.setPreferredSize(new Dimension(60, 88));
        cel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel numero = new JLabel(String.valueOf(dia.getDayOfMonth()), SwingConstants.CENTER);
        numero.setFont(Tema.fonte(Font.BOLD, 11));
        numero.setForeground(doMes ? Tema.TEXTO : Tema.BORDA);
        if (hoje) {
            numero.setOpaque(true);
            numero.setBackground(Tema.LIMAO);
            numero.setForeground(Tema.TEXTO);
        }
        Dimension tamNumero = new Dimension(22, 18);
        numero.setPreferredSize(tamNumero);
        numero.setMaximumSize(tamNumero);
        numero.setAlignmentX(LEFT_ALIGNMENT);
        cel.add(numero);
        cel.add(Box.createVerticalStrut(4));

        int mostradas = Math.min(TAREFAS_POR_DIA, tarefas.size());
        for (int i = 0; i < mostradas; i++) {
            cel.add(chip(tarefas.get(i), dia));
            cel.add(Box.createVerticalStrut(3));
        }
        if (tarefas.size() > mostradas) {
            JLabel mais = new JLabel("+" + (tarefas.size() - mostradas) + " mais");
            mais.setFont(Tema.fonte(Font.BOLD, 10));
            mais.setForeground(Tema.TEXTO_CLARO);
            mais.setAlignmentX(LEFT_ALIGNMENT);
            cel.add(mais);
        }
        if (!tarefas.isEmpty()) {
            cel.setToolTipText(dicaDoDia(dia, tarefas));
        }

        cel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                diaSelecionado = dia;
                if (!YearMonth.from(dia).equals(mesExibido)) {
                    mesExibido = YearMonth.from(dia);
                }
                redesenhar();
            }
        });
        return cel;
    }

    /** Etiqueta pequena de uma tarefa dentro do dia, na cor do status. */
    private JComponent chip(Tarefa t, LocalDate dia) {
        String status = statusEfetivo(t, dia);
        JLabel chip = new JLabel(textoCurto(t));
        chip.setFont(Tema.fonte(Font.PLAIN, 10));
        chip.setOpaque(true);
        Color fundo;
        Color texto;
        Color borda;
        switch (status) {
            case "em_andamento":
                fundo = Tema.PRETO; texto = Color.WHITE; borda = Tema.PRETO;
                break;
            case "concluida":
                fundo = Tema.LIMAO; texto = Tema.TEXTO; borda = Tema.LIMAO;
                break;
            case "atrasada":
                fundo = VERMELHO_CLARO; texto = Tema.VERMELHO; borda = Tema.VERMELHO;
                break;
            default:
                fundo = Tema.CARD; texto = Tema.TEXTO; borda = Tema.BORDA;
        }
        chip.setBackground(fundo);
        chip.setForeground(texto);
        chip.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borda),
                BorderFactory.createEmptyBorder(1, 4, 1, 4)));
        // largura pequena de preferência: o JLabel corta o texto com "..." em vez de alargar o calendário
        chip.setPreferredSize(new Dimension(10, 18));
        chip.setMaximumSize(new Dimension(Integer.MAX_VALUE, 18));
        chip.setAlignmentX(LEFT_ALIGNMENT);
        return chip;
    }

    private void montarDetalhe() {
        detalhe.removeAll();
        JPanel separador = new JPanel();
        separador.setBackground(Tema.BORDA);
        separador.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        separador.setPreferredSize(new Dimension(1, 1));
        separador.setAlignmentX(LEFT_ALIGNMENT);
        detalhe.add(separador);
        detalhe.add(Box.createVerticalStrut(12));

        JPanel topo = new JPanel(new BorderLayout());
        topo.setOpaque(false);
        topo.setAlignmentX(LEFT_ALIGNMENT);
        JLabel titulo = new JLabel(nomeDoDia(diaSelecionado));
        titulo.setFont(Tema.fonte(Font.BOLD, 13));
        titulo.setForeground(Tema.TEXTO);
        topo.add(titulo, BorderLayout.WEST);
        if (aoVerTodas != null) {
            JLabel link = new NexLink("Ver todas as tarefas →");
            link.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    aoVerTodas.run();
                }
            });
            topo.add(link, BorderLayout.EAST);
        }
        topo.setMaximumSize(new Dimension(Integer.MAX_VALUE, topo.getPreferredSize().height));
        detalhe.add(topo);
        detalhe.add(Box.createVerticalStrut(8));

        List<Tarefa> tarefas = porDia.getOrDefault(diaSelecionado, List.of());
        if (tarefas.isEmpty()) {
            JLabel vazio = new NexSubtitulo("Nenhuma tarefa com prazo neste dia.");
            vazio.setAlignmentX(LEFT_ALIGNMENT);
            detalhe.add(vazio);
            return;
        }
        for (Tarefa t : tarefas) {
            detalhe.add(linhaDetalhe(t));
            detalhe.add(Box.createVerticalStrut(6));
        }
    }

    private JComponent linhaDetalhe(Tarefa t) {
        JPanel linha = new JPanel(new BorderLayout(12, 0));
        linha.setOpaque(false);
        linha.setAlignmentX(LEFT_ALIGNMENT);

        JPanel badge = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 2));
        badge.setOpaque(false);
        JLabel etiqueta = Tema.badge(statusEfetivo(t, diaSelecionado));
        etiqueta.setPreferredSize(new Dimension(130, etiqueta.getPreferredSize().height));
        badge.add(etiqueta);
        linha.add(badge, BorderLayout.WEST);

        JPanel textos = new JPanel();
        textos.setOpaque(false);
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));
        JLabel nome = new JLabel(t.getTitulo());
        nome.setFont(Tema.fonte(Font.BOLD, 12));
        nome.setForeground(Tema.TEXTO);
        textos.add(nome);
        StringBuilder sub = new StringBuilder();
        if (t.getProjeto() != null && t.getProjeto().getTitulo() != null) {
            sub.append(t.getProjeto().getTitulo());
        }
        if (mostrarResponsavel && t.getUsuario() != null) {
            sub.append(sub.length() > 0 ? " · " : "").append("Responsável: ").append(t.getUsuario().getNome());
        }
        if (sub.length() > 0) {
            textos.add(new NexSubtitulo(sub.toString()));
        }
        linha.add(textos, BorderLayout.CENTER);
        linha.setMaximumSize(new Dimension(Integer.MAX_VALUE, linha.getPreferredSize().height));
        return linha;
    }

    // ---------- Apoio ----------

    /** Tarefa não concluída com prazo vencido aparece como atrasada, mesmo que o banco diga pendente. */
    private static String statusEfetivo(Tarefa t, LocalDate prazo) {
        String status = t.getStatus() == null ? "pendente" : t.getStatus();
        if (!"concluida".equals(status) && prazo.isBefore(LocalDate.now())) {
            return "atrasada";
        }
        return status;
    }

    private String textoCurto(Tarefa t) {
        String titulo = t.getTitulo() == null ? "" : t.getTitulo();
        if (mostrarResponsavel && t.getUsuario() != null && t.getUsuario().getNome() != null) {
            return primeiroNome(t.getUsuario().getNome()) + ": " + titulo;
        }
        return titulo;
    }

    private String dicaDoDia(LocalDate dia, List<Tarefa> tarefas) {
        StringBuilder sb = new StringBuilder("<html><b>").append(nomeDoDia(dia)).append("</b>");
        for (Tarefa t : tarefas) {
            sb.append("<br>• ").append(escapar(textoCurto(t)))
                    .append(" <i>(").append(Tema.rotuloStatus(statusEfetivo(t, dia)).toLowerCase()).append(")</i>");
        }
        return sb.append("</html>").toString();
    }

    private static String nomeDoDia(LocalDate dia) {
        DayOfWeek semana = dia.getDayOfWeek();
        String nome = NOMES_DIAS[semana.getValue() - 1];
        return Character.toUpperCase(nome.charAt(0)) + nome.substring(1) + ", " + dia.getDayOfMonth()
                + " de " + MESES[dia.getMonthValue() - 1].toLowerCase() + " de " + dia.getYear();
    }

    private static String primeiroNome(String nome) {
        String limpo = nome.trim();
        int espaco = limpo.indexOf(' ');
        return espaco > 0 ? limpo.substring(0, espaco) : limpo;
    }

    private static String escapar(String s) {
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    /** java.sql.Date não aceita toInstant(), então converte pelos milissegundos. */
    private static LocalDate paraData(Date data) {
        if (data == null) {
            return null;
        }
        return Instant.ofEpochMilli(data.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
    }

    @Override
    public Dimension getMaximumSize() {
        return new Dimension(Integer.MAX_VALUE, getPreferredSize().height);
    }
}
