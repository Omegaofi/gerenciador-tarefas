package gerenciador.tarefas.cliente.view.componentes;

import gerenciador.tarefas.cliente.view.tema.Tema;
import java.awt.Font;
import javax.swing.JLabel;

/** Logotipo "NEX" + "PROJECT" (PROJECT em verde-limão). Propriedades: tamanho e fundoEscuro. */
public class NexLogo extends JLabel {

    private int tamanho = 18;
    private boolean fundoEscuro;

    public NexLogo() {
        atualizar();
    }

    public NexLogo(int tamanho, boolean fundoEscuro) {
        this.tamanho = tamanho;
        this.fundoEscuro = fundoEscuro;
        atualizar();
    }

    public int getTamanho() {
        return tamanho;
    }

    public void setTamanho(int tamanho) {
        this.tamanho = tamanho;
        atualizar();
    }

    /** true quando o logo fica sobre fundo preto ("NEX" em branco). */
    public boolean isFundoEscuro() {
        return fundoEscuro;
    }

    public void setFundoEscuro(boolean fundoEscuro) {
        this.fundoEscuro = fundoEscuro;
        atualizar();
    }

    private void atualizar() {
        String corNex = fundoEscuro ? "#FFFFFF" : "#0B0B0B";
        setText("<html><span style='color:" + corNex + "'>NEX</span>"
                + "<span style='color:#C6F033'>PROJECT</span></html>");
        setFont(Tema.fonte(Font.BOLD, tamanho));
    }
}
