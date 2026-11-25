/*
 * Clique em nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt para alterar esta licença
 * Clique em nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java para editar este modelo
 */
package gerenciador.tarefas.cliente.view;

import gerenciador.tarefas.cliente.GerenciadorTarefasCliente;
import gerenciador.tarefas.cliente.controller.ConexaoController;
import gerenciador.tarefas.cliente.view.tema.Tema;
import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.awt.Color;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.TreeMap;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.ScrollPaneConstants;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.SwingConstants;
import modelDominio.EntregaTarefa;
import modelDominio.Projeto;
import modelDominio.Tarefa;
import modelDominio.Usuario;

/**
 *
 * @author theis
 */
public class TelaPrincipal1 extends javax.swing.JFrame {

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(TelaPrincipal1.class.getName());

    private final ConexaoController conexao = GerenciadorTarefasCliente.conexaoCliente;
    private final SimpleDateFormat formatoData = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    // Dashboard: monta a tela com resumo, projetos e atividade recente
    public TelaPrincipal1() {
        initComponents();
        estilizarComponentes();
        setLocationRelativeTo(null);
        getContentPane().setBackground(Tema.FUNDO);
        jScrollPane1.setBorder(null);
        jScrollPane1.getViewport().setOpaque(false);
        jScrollPane1.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane1.getVerticalScrollBar().setUnitIncrement(16);
        montar();
    }

    // Aplica o visual do NexProject (Tema) nos componentes criados pelo Design do NetBeans
    private void estilizarComponentes() {
        Tema.estilizarTitulo(lblTitulo);
        Tema.estilizarSubtitulo(lblSubtitulo);
        Tema.estilizarBotao(botaoNovoProjeto, Tema.PRIMARIO);
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

        menuLateral = new gerenciador.tarefas.cliente.view.MenuLateral();
        lblTitulo = new javax.swing.JLabel();
        lblSubtitulo = new javax.swing.JLabel();
        botaoNovoProjeto = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        painelCorpo = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("NexProject - Dashboard");
        setMinimumSize(new java.awt.Dimension(1000, 640));
        setPreferredSize(new java.awt.Dimension(1120, 700));

        lblTitulo.setText("Meus Projetos");

        lblSubtitulo.setText("Projetos");

        botaoNovoProjeto.setText("+ Novo projeto");
        botaoNovoProjeto.addActionListener(this::botaoNovoProjetoActionPerformed);

        painelCorpo.setOpaque(false);
        painelCorpo.setLayout(new javax.swing.BoxLayout(painelCorpo, javax.swing.BoxLayout.PAGE_AXIS));
        jScrollPane1.setViewportView(painelCorpo);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(menuLateral, javax.swing.GroupLayout.PREFERRED_SIZE, 230, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(32, 32, 32)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblTitulo)
                            .addComponent(lblSubtitulo))
                        .addGap(0, 403, Short.MAX_VALUE)
                        .addComponent(botaoNovoProjeto))
                    .addComponent(jScrollPane1))
                .addGap(32, 32, 32))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(menuLateral, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblTitulo)
                        .addGap(4, 4, 4)
                        .addComponent(lblSubtitulo))
                    .addComponent(botaoNovoProjeto))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 423, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void botaoNovoProjetoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botaoNovoProjetoActionPerformed
        // Botão Novo projeto: abre o formulário e recarrega o dashboard ao salvar
        new FormProjeto1(this, null, () -> MenuLateral.abrir(this, new TelaPrincipal1())).setVisible(true);
    }//GEN-LAST:event_botaoNovoProjetoActionPerformed

    // Busca projetos, tarefas e entregas do usuário e monta o conteúdo do dashboard
    private void montar() {
        Usuario logado = conexao.getUsuarioLogado();
        boolean criador = conexao.isCriador();

        ArrayList<Tarefa> tarefas;
        ArrayList<Projeto> projetos;
        ArrayList<EntregaTarefa> entregas;
        if (criador) {
            projetos = conexao.projetoListaPorCriador(logado.getId());
            tarefas = conexao.tarefaListaPorCriador(logado.getId());
            entregas = conexao.entregaTarefaListaPorCriador(logado.getId());
        } else {
            tarefas = conexao.tarefaListaPorResponsavel(logado.getId());
            projetos = projetosDoParticipante(tarefas);
            entregas = conexao.entregaTarefaListaPorResponsavel(logado.getId());
        }

        if (criador) {
            long ativos = projetos.stream()
                    .filter(p -> !"concluido".equals(p.getStatus()) && !"cancelado".equals(p.getStatus()))
                    .count();
            lblSubtitulo.setText(ativos + (ativos == 1 ? " projeto ativo" : " projetos ativos"));
        } else {
            lblSubtitulo.setText(projetos.size() + (projetos.size() == 1
                    ? " projeto em que você participa" : " projetos em que você participa"));
        }
        botaoNovoProjeto.setVisible(criador);

        painelCorpo.add(gradeProjetos(projetos, tarefas, criador));
        painelCorpo.add(Box.createVerticalStrut(18));
        painelCorpo.add(calendario(tarefas, criador));
        painelCorpo.add(Box.createVerticalStrut(18));
        painelCorpo.add(atividadeRecente(entregas, criador));
    }

    /**
     * Para o Participante: projetos em que ele foi aceito + projetos das tarefas atribuídas a ele
     * (assim o projeto aparece mesmo antes de ele receber a primeira tarefa).
     */
    private ArrayList<Projeto> projetosDoParticipante(ArrayList<Tarefa> tarefas) {
        Map<Integer, Projeto> porId = new LinkedHashMap<>();
        for (Projeto p : conexao.projetoListaParticipando()) {
            porId.put(p.getId(), p);
        }
        for (Tarefa t : tarefas) {
            if (t.getProjeto() != null) {
                porId.putIfAbsent(t.getProjeto().getId(), t.getProjeto());
            }
        }
        return new ArrayList<>(porId.values());
    }

    // Grade de cartões com os projetos (2 por linha)
    private JPanel gradeProjetos(ArrayList<Projeto> projetos, ArrayList<Tarefa> tarefas, boolean criador) {
        JPanel grade = new JPanel(new GridLayout(0, 2, 14, 14));
        grade.setOpaque(false);
        grade.setAlignmentX(LEFT_ALIGNMENT);

        if (projetos.isEmpty()) {
            JPanel vazio = Tema.card();
            vazio.setLayout(new BorderLayout());
            vazio.add(Tema.subtitulo(criador
                    ? "Você ainda não criou projetos. Clique em \"+ NOVO PROJETO\" para começar."
                    : "Você ainda não participa de nenhum projeto. Use EXPLORAR PROJETOS para encontrar um."), BorderLayout.CENTER);
            grade.add(vazio);
        }
        for (Projeto projeto : projetos) {
            grade.add(cardProjeto(projeto, tarefas));
        }
        grade.setMaximumSize(new Dimension(Integer.MAX_VALUE, grade.getPreferredSize().height));
        return grade;
    }

    // Cartão de um projeto: progresso das tarefas e número de membros
    private JPanel cardProjeto(Projeto projeto, ArrayList<Tarefa> tarefas) {
        int total = 0;
        int concluidas = 0;
        Set<Integer> membros = new HashSet<>();
        for (Tarefa t : tarefas) {
            if (t.getProjeto() != null && t.getProjeto().getId() == projeto.getId()) {
                total++;
                if ("concluida".equals(t.getStatus())) {
                    concluidas++;
                }
                if (t.getUsuario() != null) {
                    membros.add(t.getUsuario().getId());
                }
            }
        }

        JPanel card = Tema.card();
        card.setLayout(new BorderLayout(0, 10));
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JPanel topo = new JPanel(new BorderLayout());
        topo.setOpaque(false);
        JPanel marcador = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 4));
        marcador.setOpaque(false);
        marcador.add(Tema.marcador(Tema.corProjeto(projeto.getStatus())));
        topo.add(marcador, BorderLayout.WEST);
        if (projeto.getStatus() != null) {
            topo.add(Tema.badge(projeto.getStatus()), BorderLayout.EAST);
        }
        card.add(topo, BorderLayout.NORTH);

        JPanel meio = new JPanel();
        meio.setOpaque(false);
        meio.setLayout(new BoxLayout(meio, BoxLayout.Y_AXIS));
        JLabel titulo = new JLabel(projeto.getTitulo());
        titulo.setFont(Tema.fonte(Font.BOLD, 14));
        titulo.setAlignmentX(LEFT_ALIGNMENT);
        meio.add(titulo);
        if (projeto.getAreaAtuacao() != null && !projeto.getAreaAtuacao().isBlank()) {
            JLabel area = Tema.subtitulo(projeto.getAreaAtuacao());
            area.setAlignmentX(LEFT_ALIGNMENT);
            meio.add(Box.createVerticalStrut(3));
            meio.add(area);
        }
        meio.add(Box.createVerticalStrut(12));
        javax.swing.JComponent barra = Tema.barraProgresso(total == 0 ? 0 : (double) concluidas / total);
        barra.setAlignmentX(LEFT_ALIGNMENT);
        barra.setMaximumSize(new Dimension(Integer.MAX_VALUE, 5));
        meio.add(barra);
        card.add(meio, BorderLayout.CENTER);

        JPanel rodape = new JPanel(new BorderLayout());
        rodape.setOpaque(false);
        rodape.add(Tema.subtitulo(concluidas + "/" + total + " tarefas"), BorderLayout.WEST);
        rodape.add(Tema.subtitulo(membros.size() + (membros.size() == 1 ? " membro" : " membros")), BorderLayout.EAST);
        card.add(rodape, BorderLayout.SOUTH);

        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                MenuLateral.abrir(TelaPrincipal1.this, new TabelaTarefa1());
            }
        });
        return card;
    }

    /**
     * Calendário pelo prazo das tarefas. O Criador recebe as tarefas de todos os colaboradores
     * dos seus projetos (com o responsável); o Colaborador, só as atribuídas a ele.
     */
    private CalendarioTarefas calendario(ArrayList<Tarefa> tarefas, boolean criador) {
        CalendarioTarefas calendario = new CalendarioTarefas();
        calendario.setTarefas(tarefas, criador);
        calendario.setAoVerTodas(() -> MenuLateral.abrir(this, new TabelaTarefa1()));
        return calendario;
    }

    // Lista das últimas entregas (atividade recente)
    private JPanel atividadeRecente(ArrayList<EntregaTarefa> entregas, boolean criador) {
        JPanel card = Tema.card();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setAlignmentX(LEFT_ALIGNMENT);

        JLabel titulo = Tema.rotuloCampo("Atividade recente");
        titulo.setForeground(Tema.TEXTO);
        card.add(titulo);
        card.add(Box.createVerticalStrut(10));

        if (entregas.isEmpty()) {
            card.add(Tema.subtitulo("Nenhuma entrega registrada ainda."));
        }
        int limite = Math.min(5, entregas.size());
        for (int i = 0; i < limite; i++) {
            EntregaTarefa entrega = entregas.get(i);
            String quem = criador && entrega.getTarefa().getUsuario() != null
                    ? entrega.getTarefa().getUsuario().getNome() : "Você";
            JPanel linha = new JPanel(new BorderLayout(10, 0));
            linha.setOpaque(false);
            linha.setAlignmentX(LEFT_ALIGNMENT);
            linha.setBorder(BorderFactory.createEmptyBorder(4, 0, 4, 0));
            JLabel texto = new JLabel("<html><span style='color:#8FB51A'>■</span>&nbsp; " + quem
                    + " enviou «" + entrega.getTarefa().getTitulo() + "» — "
                    + Tema.rotuloStatus(entrega.getStatus()).toLowerCase() + "</html>");
            texto.setFont(Tema.fonte(Font.PLAIN, 12));
            linha.add(texto, BorderLayout.CENTER);
            linha.add(Tema.subtitulo(entrega.getDataEntrega() != null
                    ? formatoData.format(entrega.getDataEntrega()) : ""), BorderLayout.EAST);
            linha.setMaximumSize(new Dimension(Integer.MAX_VALUE, linha.getPreferredSize().height));
            card.add(linha);
        }
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, card.getPreferredSize().height));
        return card;
    }

    /**
     * @param args os argumentos da linha de comando
     */
    public static void main(String args[]) {
        Tema.aplicar();
        java.awt.EventQueue.invokeLater(() -> new TelaPrincipal1().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton botaoNovoProjeto;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblSubtitulo;
    private javax.swing.JLabel lblTitulo;
    private gerenciador.tarefas.cliente.view.MenuLateral menuLateral;
    private javax.swing.JPanel painelCorpo;
    // End of variables declaration//GEN-END:variables

    /**
     * Calendário mensal com as tarefas pelo prazo de entrega.
     *
     * <p>O Criador vê as tarefas de todos os colaboradores dos seus projetos (com o nome do
     * responsável em cada tarefa); o Colaborador vê só as dele. Clique num dia para ver a lista
     * completa abaixo do mês.</p>
     *
     * <p>Fica dentro da TelaPrincipal1 porque só ela usa o calendário.</p>
     */
    private static class CalendarioTarefas extends JPanel {

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

        // Monta o calendário: cabeçalho, dias da semana, grade do mês e detalhe do dia
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

        // Mostra ou não o nome do responsável em cada tarefa
        public void setMostrarResponsavel(boolean mostrarResponsavel) {
            this.mostrarResponsavel = mostrarResponsavel;
            redesenhar();
        }

        // ---------- Montagem ----------

        // Cabeçalho com mês/ano, resumo e setas para trocar de mês
        private JComponent cabecalho() {
            JPanel topo = new JPanel(new BorderLayout(12, 0));
            topo.setOpaque(false);

            JPanel textos = new JPanel();
            textos.setOpaque(false);
            textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));
            JLabel titulo = Tema.rotuloCampo("Calendário de tarefas");
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
            JButton hoje = Tema.botaoSecundario("Hoje");
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

        // Botão de seta para avançar ou voltar o mês
        private JButton botaoNavegar(String seta, Runnable acao) {
            JButton botao = Tema.botaoSecundario(seta);
            botao.setFont(Tema.fonte(Font.BOLD, 16));
            botao.setBorder(BorderFactory.createEmptyBorder(2, 12, 4, 12));
            botao.addActionListener(e -> acao.run());
            return botao;
        }

        // Linha com os nomes dos dias da semana
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

        // Avança ou volta meses no calendário
        private void trocarMes(int delta) {
            mesExibido = mesExibido.plusMonths(delta);
            LocalDate hoje = LocalDate.now();
            diaSelecionado = YearMonth.from(hoje).equals(mesExibido) ? hoje : mesExibido.atDay(1);
            redesenhar();
        }

        // Redesenha a grade do mês com as tarefas de cada dia
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

        // Atualiza o resumo: tarefas no mês e quantas estão atrasadas
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

        // Célula de um dia, com as tarefas que vencem nele
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
                // Clique no dia: seleciona e mostra as tarefas dele embaixo
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

        // Monta a lista de tarefas do dia selecionado
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
                JLabel link = Tema.link("Ver todas as tarefas →");
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
                JLabel vazio = Tema.subtitulo("Nenhuma tarefa com prazo neste dia.");
                vazio.setAlignmentX(LEFT_ALIGNMENT);
                detalhe.add(vazio);
                return;
            }
            for (Tarefa t : tarefas) {
                detalhe.add(linhaDetalhe(t));
                detalhe.add(Box.createVerticalStrut(6));
            }
        }

        // Linha com uma tarefa no detalhe do dia
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
                textos.add(Tema.subtitulo(sub.toString()));
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

        // Texto curto da tarefa (com o responsável, se for o caso)
        private String textoCurto(Tarefa t) {
            String titulo = t.getTitulo() == null ? "" : t.getTitulo();
            if (mostrarResponsavel && t.getUsuario() != null && t.getUsuario().getNome() != null) {
                return primeiroNome(t.getUsuario().getNome()) + ": " + titulo;
            }
            return titulo;
        }

        // Dica que aparece ao passar o mouse no dia
        private String dicaDoDia(LocalDate dia, List<Tarefa> tarefas) {
            StringBuilder sb = new StringBuilder("<html><b>").append(nomeDoDia(dia)).append("</b>");
            for (Tarefa t : tarefas) {
                sb.append("<br>• ").append(escapar(textoCurto(t)))
                        .append(" <i>(").append(Tema.rotuloStatus(statusEfetivo(t, dia)).toLowerCase()).append(")</i>");
            }
            return sb.append("</html>").toString();
        }

        // Nome do dia por extenso, ex.: "Segunda, 18/11"
        private static String nomeDoDia(LocalDate dia) {
            DayOfWeek semana = dia.getDayOfWeek();
            String nome = NOMES_DIAS[semana.getValue() - 1];
            return Character.toUpperCase(nome.charAt(0)) + nome.substring(1) + ", " + dia.getDayOfMonth()
                    + " de " + MESES[dia.getMonthValue() - 1].toLowerCase() + " de " + dia.getYear();
        }

        // Primeiro nome de uma pessoa
        private static String primeiroNome(String nome) {
            String limpo = nome.trim();
            int espaco = limpo.indexOf(' ');
            return espaco > 0 ? limpo.substring(0, espaco) : limpo;
        }

        // Escapa caracteres especiais para usar em HTML
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

        // Não deixa o calendário esticar na vertical
        @Override
        public Dimension getMaximumSize() {
            return new Dimension(Integer.MAX_VALUE, getPreferredSize().height);
        }
    }
}
