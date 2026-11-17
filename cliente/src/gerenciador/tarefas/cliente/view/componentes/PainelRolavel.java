package gerenciador.tarefas.cliente.view.componentes;

import java.awt.Dimension;
import java.awt.Rectangle;
import javax.swing.JPanel;
import javax.swing.Scrollable;

/**
 * Painel para colocar dentro de um JScrollPane: acompanha a largura da área visível
 * (os cards esticam junto com a janela) e rola só na vertical.
 */
public class PainelRolavel extends JPanel implements Scrollable {

    public PainelRolavel() {
        setOpaque(false);
    }

    @Override
    public Dimension getPreferredScrollableViewportSize() {
        return getPreferredSize();
    }

    @Override
    public int getScrollableUnitIncrement(Rectangle visivel, int orientacao, int direcao) {
        return 16;
    }

    @Override
    public int getScrollableBlockIncrement(Rectangle visivel, int orientacao, int direcao) {
        return Math.max(visivel.height - 32, 16);
    }

    @Override
    public boolean getScrollableTracksViewportWidth() {
        return true;
    }

    @Override
    public boolean getScrollableTracksViewportHeight() {
        return false;
    }
}
