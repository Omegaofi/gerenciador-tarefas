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
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import modelDominio.EntregaTarefa;
import modelDominio.Projeto;
import modelDominio.Tarefa;
import modelDominio.Usuario;

/** Dashboard: cards dos projetos com progresso das tarefas e atividade recente. */
public class TelaPrincipal extends TelaBase {

    private final ConexaoController conexao = GerenciadorTarefasCliente.conexaoCliente;
    private final SimpleDateFormat formatoData = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    public TelaPrincipal() {
        super("Dashboard", DASHBOARD);
        montar();
    }

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
            projetos = projetosDasTarefas(tarefas);
            entregas = conexao.entregaTarefaListaPorResponsavel(logado.getId());
        }

        String subtitulo;
        JButton acao = null;
        if (criador) {
            long ativos = projetos.stream()
                    .filter(p -> !"concluido".equals(p.getStatus()) && !"cancelado".equals(p.getStatus()))
                    .count();
            subtitulo = ativos + (ativos == 1 ? " projeto ativo" : " projetos ativos");
            acao = Tema.botaoPrimario("+ Novo projeto");
            acao.addActionListener(e -> {
                FormProjeto form = new FormProjeto(this, null, () -> abrir(new TelaPrincipal()));
                form.setVisible(true);
            });
        } else {
            subtitulo = projetos.size() + (projetos.size() == 1
                    ? " projeto com tarefas para você" : " projetos com tarefas para você");
        }
        conteudo.add(cabecalho("Meus Projetos", subtitulo, acao), BorderLayout.NORTH);

        JPanel corpo = new JPanel();
        corpo.setOpaque(false);
        corpo.setLayout(new BoxLayout(corpo, BoxLayout.Y_AXIS));
        corpo.add(gradeProjetos(projetos, tarefas, criador));
        corpo.add(Box.createVerticalStrut(18));
        corpo.add(atividadeRecente(entregas, criador));

        JScrollPane rolagem = new JScrollPane(corpo);
        rolagem.setBorder(null);
        rolagem.setOpaque(false);
        rolagem.getViewport().setOpaque(false);
        rolagem.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        rolagem.getVerticalScrollBar().setUnitIncrement(16);
        conteudo.add(rolagem, BorderLayout.CENTER);
    }

    /** Para o Participante, os "projetos" são os projetos das tarefas atribuídas a ele. */
    private ArrayList<Projeto> projetosDasTarefas(ArrayList<Tarefa> tarefas) {
        Map<Integer, Projeto> porId = new LinkedHashMap<>();
        for (Tarefa t : tarefas) {
            if (t.getProjeto() != null) {
                porId.putIfAbsent(t.getProjeto().getId(), t.getProjeto());
            }
        }
        return new ArrayList<>(porId.values());
    }

    private JPanel gradeProjetos(ArrayList<Projeto> projetos, ArrayList<Tarefa> tarefas, boolean criador) {
        JPanel grade = new JPanel(new GridLayout(0, 2, 14, 14));
        grade.setOpaque(false);
        grade.setAlignmentX(LEFT_ALIGNMENT);

        if (projetos.isEmpty()) {
            JPanel vazio = Tema.card();
            vazio.setLayout(new BorderLayout());
            vazio.add(Tema.subtitulo(criador
                    ? "Você ainda não criou projetos. Clique em \"+ NOVO PROJETO\" para começar."
                    : "Nenhuma tarefa foi atribuída a você ainda."), BorderLayout.CENTER);
            grade.add(vazio);
        }
        for (Projeto projeto : projetos) {
            grade.add(cardProjeto(projeto, tarefas));
        }
        grade.setMaximumSize(new Dimension(Integer.MAX_VALUE, grade.getPreferredSize().height));
        return grade;
    }

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
                abrir(new TabelaTarefa());
            }
        });
        return card;
    }

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
}
