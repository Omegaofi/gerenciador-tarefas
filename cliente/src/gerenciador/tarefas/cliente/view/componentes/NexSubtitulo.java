package gerenciador.tarefas.cliente.view.componentes;

import gerenciador.tarefas.cliente.view.tema.Tema;
import java.awt.Font;

/** Texto de apoio, cinza (12pt). */
public class NexSubtitulo extends NexLabel {

    public NexSubtitulo() {
        this("Subtítulo");
    }

    public NexSubtitulo(String texto) {
        super(texto, Font.PLAIN, 12, Tema.TEXTO_CLARO);
    }
}
