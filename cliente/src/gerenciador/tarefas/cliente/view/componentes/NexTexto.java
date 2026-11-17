package gerenciador.tarefas.cliente.view.componentes;

import gerenciador.tarefas.cliente.view.tema.Tema;
import java.awt.Font;

/** Texto comum na fonte do tema. Ajuste tamanho e negrito pelas propriedades no Design. */
public class NexTexto extends NexLabel {

    public NexTexto() {
        this("Texto");
    }

    public NexTexto(String texto) {
        super(texto, Font.PLAIN, 13, Tema.TEXTO);
    }
}
