package gerenciador.tarefas.cliente.view.componentes;

import gerenciador.tarefas.cliente.view.tema.Tema;
import javax.swing.JTextArea;

/** Área de texto com quebra de linha automática. Use a propriedade rows para a altura. */
public class NexAreaTexto extends JTextArea {

    public NexAreaTexto() {
        super(4, 20);
        setLineWrap(true);
        setWrapStyleWord(true);
        Tema.estilizarCampo(this);
    }
}
