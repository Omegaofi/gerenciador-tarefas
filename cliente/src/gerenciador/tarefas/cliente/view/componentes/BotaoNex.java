package gerenciador.tarefas.cliente.view.componentes;

import gerenciador.tarefas.cliente.view.tema.Tema;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.BorderFactory;
import javax.swing.JButton;

/**
 * Botão retangular e plano do NexProject, pintado à mão para ficar igual em qualquer sistema.
 * É um JavaBean (construtor sem parâmetros), então aparece com o visual real no Design do NetBeans.
 * A propriedade {@code estilo} aceita: primario, destaque, secundario, perigo.
 */
public class BotaoNex extends JButton {

    public static final String PRIMARIO = "primario";
    public static final String DESTAQUE = "destaque";
    public static final String SECUNDARIO = "secundario";
    public static final String PERIGO = "perigo";

    private String estilo = PRIMARIO;
    private Color fundo;
    private Color hover;
    private Color borda;

    public BotaoNex() {
        this("Botão", PRIMARIO);
    }

    public BotaoNex(String texto, String estilo) {
        super(texto);
        setFont(Tema.fonte(Font.BOLD, 12));
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 18));
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setEstilo(estilo);
    }

    public String getEstilo() {
        return estilo;
    }

    public void setEstilo(String estilo) {
        this.estilo = estilo == null ? PRIMARIO : estilo.toLowerCase();
        switch (this.estilo) {
            case DESTAQUE:
                fundo = Tema.LIMAO; hover = Tema.LIMAO_HOVER; borda = null; setForeground(Tema.TEXTO);
                break;
            case SECUNDARIO:
                fundo = Tema.CARD; hover = Tema.CAMPO; borda = Tema.BORDA; setForeground(Tema.TEXTO);
                break;
            case PERIGO:
                fundo = Tema.CARD; hover = new Color(0xFB, 0xE9, 0xE9); borda = Tema.VERMELHO; setForeground(Tema.VERMELHO);
                break;
            default:
                this.estilo = PRIMARIO;
                fundo = Tema.PRETO; hover = Tema.PRETO_HOVER; borda = null; setForeground(Color.WHITE);
        }
        repaint();
    }

    /** O texto dos botões é sempre em caixa alta, como nos wireframes. */
    @Override
    public void setText(String texto) {
        super.setText(texto == null ? null : texto.toUpperCase());
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setColor(getModel().isRollover() || getModel().isPressed() ? hover : fundo);
        g2.fillRect(0, 0, getWidth(), getHeight());
        if (borda != null) {
            g2.setColor(borda);
            g2.setStroke(new BasicStroke(1));
            g2.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
        }
        g2.dispose();
        super.paintComponent(g);
    }
}
