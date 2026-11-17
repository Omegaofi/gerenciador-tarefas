package gerenciador.tarefas.cliente.view.componentes;

/** Botão vermelho, para excluir. Arraste da paleta ou use como classe do componente no Design. */
public class BotaoPerigo extends BotaoNex {

    public BotaoPerigo() {
        this("Botão");
    }

    public BotaoPerigo(String texto) {
        super(texto, PERIGO);
    }
}
