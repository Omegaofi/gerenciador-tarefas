package gerenciador.tarefas.cliente.view.componentes;

import gerenciador.tarefas.cliente.view.tema.Tema;
import javax.swing.JPasswordField;

/** Campo de senha do NexProject. */
public class NexCampoSenha extends JPasswordField {

    public NexCampoSenha() {
        Tema.estilizarCampo(this);
        setCaretColor(Tema.TEXTO);
    }
}
