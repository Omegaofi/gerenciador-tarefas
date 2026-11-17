package gerenciador.tarefas.cliente.view.componentes;

import gerenciador.tarefas.cliente.view.tema.Tema;
import java.awt.Color;
import javax.swing.JLabel;

/** Base dos textos do NexProject: fonte monoespaçada do tema, com tamanho editável no Design. */
public class NexLabel extends JLabel {

    private int estiloFonte;
    private int tamanho;

    protected NexLabel(String texto, int estiloFonte, int tamanho, Color cor) {
        super(texto);
        this.estiloFonte = estiloFonte;
        setForeground(cor);
        setTamanho(tamanho);
    }

    public int getTamanho() {
        return tamanho;
    }

    /** Tamanho da fonte em pontos (a família é a monoespaçada escolhida pelo tema). */
    public void setTamanho(int tamanho) {
        this.tamanho = tamanho;
        setFont(Tema.fonte(estiloFonte, tamanho));
    }

    public boolean isNegrito() {
        return estiloFonte == java.awt.Font.BOLD;
    }

    public void setNegrito(boolean negrito) {
        estiloFonte = negrito ? java.awt.Font.BOLD : java.awt.Font.PLAIN;
        setFont(Tema.fonte(estiloFonte, tamanho));
    }
}
