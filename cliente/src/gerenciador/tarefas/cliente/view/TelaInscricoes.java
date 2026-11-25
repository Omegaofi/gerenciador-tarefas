package gerenciador.tarefas.cliente.view;

import gerenciador.tarefas.cliente.GerenciadorTarefasCliente;
import gerenciador.tarefas.cliente.view.tablemodel.InscricaoTableModel;
import gerenciador.tarefas.cliente.view.tema.Tema;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import modelDominio.Inscricao;

/**
 * O criador vê quem pediu para entrar nos projetos dele e aprova ou recusa (RF008).
 * Aberta pelo botão "Inscrições" da tela de Projetos.
 */
public class TelaInscricoes extends JDialog {

    private final JTable tabela = new JTable();
    private final JScrollPane rolagem = new JScrollPane(tabela);
    private final JLabel lblResumo = Tema.subtitulo(" ");
    private final Runnable aoMudar;
    private InscricaoTableModel modelo;

    /** @param aoMudar chamado depois de cada aprovação/recusa (ex.: atualizar o contador do botão) */
    public TelaInscricoes(Window dono, Runnable aoMudar) {
        super(dono, "Inscrições nos meus projetos", ModalityType.APPLICATION_MODAL);
        this.aoMudar = aoMudar;
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        montar();
        carregar();
        setSize(940, 460);
        setLocationRelativeTo(dono);
    }

    // Monta a janela: título, tabela de pedidos e botões aprovar/recusar
    private void montar() {
        JPanel fundo = new JPanel(new BorderLayout(0, 16));
        fundo.setBackground(Tema.FUNDO);
        fundo.setBorder(BorderFactory.createEmptyBorder(24, 28, 22, 28));

        JPanel topo = new JPanel();
        topo.setOpaque(false);
        topo.setLayout(new BoxLayout(topo, BoxLayout.Y_AXIS));
        topo.add(Tema.titulo("Inscrições pendentes"));
        topo.add(lblResumo);
        fundo.add(topo, BorderLayout.NORTH);

        Tema.estilizarTabela(tabela, rolagem);
        rolagem.setPreferredSize(new Dimension(700, 260));
        tabela.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    verPerfil();
                }
            }
        });
        fundo.add(rolagem, BorderLayout.CENTER);

        JButton botaoAprovar = Tema.botaoPrimario("Aprovar");
        JButton botaoRecusar = Tema.botaoPerigo("Recusar");
        JButton botaoFechar = Tema.botaoSecundario("Fechar");
        botaoAprovar.addActionListener(e -> responder(true));
        botaoRecusar.addActionListener(e -> responder(false));
        botaoFechar.addActionListener(e -> dispose());

        JPanel acoes = new JPanel(new BorderLayout());
        acoes.setOpaque(false);
        JPanel esquerda = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        esquerda.setOpaque(false);
        esquerda.add(botaoAprovar);
        esquerda.add(botaoRecusar);
        JPanel direita = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        direita.setOpaque(false);
        direita.add(botaoFechar);
        acoes.add(esquerda, BorderLayout.WEST);
        acoes.add(direita, BorderLayout.EAST);
        fundo.add(acoes, BorderLayout.SOUTH);

        setContentPane(fundo);
    }

    // Busca no servidor os pedidos pendentes e preenche a tabela
    private void carregar() {
        modelo = new InscricaoTableModel(GerenciadorTarefasCliente.conexaoCliente.inscricaoPendentesCriador());
        tabela.setModel(modelo);
        int[] larguras = {240, 190, 290, 150};
        for (int i = 0; i < larguras.length; i++) {
            tabela.getColumnModel().getColumn(i).setPreferredWidth(larguras[i]);
        }
        int total = modelo.getRowCount();
        lblResumo.setText(total == 0 ? "Nenhuma inscrição aguardando sua resposta."
                : total + (total == 1 ? " pessoa quer" : " pessoas querem")
                + " participar dos seus projetos. Clique duas vezes para ver o perfil.");
    }

    // Inscrição selecionada na tabela (avisa se nenhuma estiver)
    private Inscricao selecionada() {
        int linha = tabela.getSelectedRow();
        if (linha < 0) {
            JOptionPane.showMessageDialog(this, "Selecione uma inscrição.");
            return null;
        }
        return modelo.getInscricao(tabela.convertRowIndexToModel(linha));
    }

    // Mostra o perfil de quem pediu para entrar
    private void verPerfil() {
        Inscricao inscricao = selecionada();
        if (inscricao == null) {
            return;
        }
        String bio = inscricao.getUsuario().getBiografia();
        JOptionPane.showMessageDialog(this,
                inscricao.getUsuario().getNome() + "\n" + inscricao.getUsuario().getEmail() + "\n\n"
                + (bio == null || bio.isBlank() ? "Sem biografia." : bio),
                "Perfil do participante", JOptionPane.INFORMATION_MESSAGE);
    }

    // Aprova ou recusa o pedido selecionado
    private void responder(boolean aprovar) {
        Inscricao inscricao = selecionada();
        if (inscricao == null) {
            return;
        }
        String nome = inscricao.getUsuario().getNome();
        String projeto = inscricao.getProjeto().getTitulo();
        if (!aprovar) {
            int resposta = JOptionPane.showConfirmDialog(this,
                    "Recusar a inscrição de " + nome + " em \"" + projeto + "\"?",
                    "Recusar inscrição", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (resposta != JOptionPane.YES_OPTION) {
                return;
            }
        }
        if (GerenciadorTarefasCliente.conexaoCliente.inscricaoResponder(
                inscricao.getProjeto().getId(), inscricao.getUsuario().getId(), aprovar)) {
            JOptionPane.showMessageDialog(this, aprovar
                    ? nome + " agora faz parte de \"" + projeto + "\".\nVocê já pode atribuir tarefas a ele(a)."
                    : "Inscrição recusada.");
        } else {
            JOptionPane.showMessageDialog(this, "Não foi possível responder. A inscrição pode já ter sido respondida.");
        }
        carregar();
        if (aoMudar != null) {
            aoMudar.run();
        }
    }
}
