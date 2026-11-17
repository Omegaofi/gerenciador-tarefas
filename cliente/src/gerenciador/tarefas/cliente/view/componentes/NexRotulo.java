package gerenciador.tarefas.cliente.view.componentes;

import gerenciador.tarefas.cliente.view.tema.Tema;
import java.awt.Font;

/** Rótulo pequeno em caixa alta, usado acima dos campos (ex.: E-MAIL). */
public class NexRotulo extends NexLabel {

    public NexRotulo() {
        this("Rótulo");
    }

    public NexRotulo(String texto) {
        super(texto, Font.BOLD, 11, Tema.TEXTO_CLARO);
    }

    @Override
    public void setText(String texto) {
        super.setText(texto == null ? null : texto.toUpperCase());
    }
}
