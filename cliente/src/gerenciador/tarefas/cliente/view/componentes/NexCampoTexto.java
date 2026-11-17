package gerenciador.tarefas.cliente.view.componentes;

import gerenciador.tarefas.cliente.view.tema.Tema;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import javax.swing.JTextField;

/**
 * Campo de texto do NexProject (fundo cinza-claro, borda fina).
 * A propriedade {@code dica} mostra um texto cinza enquanto o campo está vazio (placeholder).
 */
public class NexCampoTexto extends JTextField {

    private String dica = "";

    public NexCampoTexto() {
        Tema.estilizarCampo(this);
        setCaretColor(Tema.TEXTO);
    }

    public String getDica() {
        return dica;
    }

    public void setDica(String dica) {
        this.dica = dica == null ? "" : dica;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (getText().isEmpty() && !dica.isEmpty()) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2.setColor(Tema.TEXTO_CLARO);
            g2.setFont(getFont());
            Insets margem = getInsets();
            int y = (getHeight() + g2.getFontMetrics().getAscent() - g2.getFontMetrics().getDescent()) / 2;
            g2.drawString(dica, margem.left, y);
            g2.dispose();
        }
    }
}
