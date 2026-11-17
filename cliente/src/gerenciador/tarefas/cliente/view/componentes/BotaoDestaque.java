package gerenciador.tarefas.cliente.view.componentes;

/** Botão verde-limão. Arraste da paleta ou use como classe do componente no Design. */
public class BotaoDestaque extends BotaoNex {

    public BotaoDestaque() {
        this("Botão");
    }

    public BotaoDestaque(String texto) {
        super(texto, DESTAQUE);
    }
}
