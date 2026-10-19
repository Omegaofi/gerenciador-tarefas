package gerenciador.tarefas.cliente.view;

import gerenciador.tarefas.cliente.view.tema.Tema;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Janela de formulário (modal) no estilo NexProject: título, campos empilhados
 * e os botões CANCELAR / SALVAR no rodapé.
 */
public abstract class FormBase extends JDialog {

    protected final JPanel campos = new JPanel();
    protected final JButton botaoSalvar;
    private final JLabel textAlerta = new JLabel(" ");

    protected FormBase(Frame dono, String titulo, String subtitulo, String textoSalvar) {
        super(dono, titulo, true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel raiz = new JPanel(new BorderLayout(0, 16));
        raiz.setBackground(Tema.FUNDO);
        raiz.setBorder(BorderFactory.createEmptyBorder(24, 28, 22, 28));

        JPanel topo = new JPanel();
        topo.setOpaque(false);
        topo.setLayout(new BoxLayout(topo, BoxLayout.Y_AXIS));
        JLabel labelTitulo = new JLabel(titulo);
        labelTitulo.setFont(Tema.fonte(Font.BOLD, 18));
        topo.add(labelTitulo);
        if (subtitulo != null) {
            topo.add(Box.createVerticalStrut(4));
            topo.add(Tema.subtitulo(subtitulo));
        }
        topo.add(Box.createVerticalStrut(8));
        textAlerta.setFont(Tema.fonte(Font.BOLD, 12));
        textAlerta.setForeground(Tema.VERMELHO);
        topo.add(textAlerta);
        raiz.add(topo, BorderLayout.NORTH);

        campos.setOpaque(false);
        campos.setLayout(new BoxLayout(campos, BoxLayout.Y_AXIS));
        raiz.add(campos, BorderLayout.CENTER);

        JPanel rodape = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        rodape.setOpaque(false);
        JButton botaoCancelar = Tema.botaoSecundario("Cancelar");
        botaoCancelar.addActionListener(e -> dispose());
        botaoSalvar = Tema.botaoPrimario(textoSalvar);
        botaoSalvar.addActionListener(e -> salvar());
        rodape.add(botaoCancelar);
        rodape.add(Box.createHorizontalStrut(10));
        rodape.add(botaoSalvar);
        raiz.add(rodape, BorderLayout.SOUTH);

        setContentPane(raiz);
        getRootPane().setDefaultButton(botaoSalvar);
    }

    /** Item de combo: guarda o valor do banco e mostra o texto amigável. */
    protected record Opcao(String valor, String rotulo) {

        static Opcao[] deStatus(String... valores) {
            Opcao[] opcoes = new Opcao[valores.length];
            for (int i = 0; i < valores.length; i++) {
                opcoes[i] = new Opcao(valores[i], Tema.rotuloStatus(valores[i]));
            }
            return opcoes;
        }

        @Override
        public String toString() {
            return rotulo;
        }
    }

    protected static void selecionarValor(javax.swing.JComboBox<Opcao> combo, String valor) {
        for (int i = 0; i < combo.getItemCount(); i++) {
            if (combo.getItemAt(i).valor().equals(valor)) {
                combo.setSelectedIndex(i);
                return;
            }
        }
    }

    /** Ação do botão principal. */
    protected abstract void salvar();

    protected void adicionarCampo(String rotulo, JComponent campo) {
        JLabel label = Tema.rotuloCampo(rotulo);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        campos.add(label);
        campos.add(Box.createVerticalStrut(5));
        campo.setAlignmentX(Component.LEFT_ALIGNMENT);
        campo.setMaximumSize(new Dimension(Integer.MAX_VALUE, campo.getPreferredSize().height));
        campos.add(campo);
        campos.add(Box.createVerticalStrut(14));
    }

    protected void alerta(String mensagem) {
        textAlerta.setText(mensagem);
    }

    /** Ajusta o tamanho ao conteúdo (largura mínima de 460px) e centraliza na tela de origem. */
    protected void finalizarLayout() {
        pack();
        setSize(Math.max(460, getWidth()), getHeight());
        setLocationRelativeTo(getOwner());
    }
}
