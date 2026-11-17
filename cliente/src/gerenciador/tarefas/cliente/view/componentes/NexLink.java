package gerenciador.tarefas.cliente.view.componentes;

import gerenciador.tarefas.cliente.view.tema.Tema;
import java.awt.Font;

/** Texto clicável e sublinhado, como "Cadastre-se". */
public class NexLink extends NexLabel {

    public NexLink() {
        this("Link");
    }

    public NexLink(String texto) {
        super(texto, Font.BOLD, 12, Tema.TEXTO);
    }

    {
        setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.HAND_CURSOR));
    }

    @Override
    public void setFont(Font fonte) {
        if (fonte != null) {
            java.util.Map<java.awt.font.TextAttribute, Object> atributos = new java.util.HashMap<>(fonte.getAttributes());
            atributos.put(java.awt.font.TextAttribute.UNDERLINE, java.awt.font.TextAttribute.UNDERLINE_ON);
            fonte = fonte.deriveFont(atributos);
        }
        super.setFont(fonte);
    }
}
