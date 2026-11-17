package gerenciador.tarefas.cliente.view.componentes;

import gerenciador.tarefas.cliente.view.tema.Tema;
import java.awt.Font;

/** Título da tela (negrito, 22pt). */
public class NexTitulo extends NexLabel {

    public NexTitulo() {
        this("Título");
    }

    public NexTitulo(String texto) {
        super(texto, Font.BOLD, 22, Tema.TEXTO);
    }
}
