package gerenciador.tarefas.cliente.view.componentes;

/** Botão branco com borda. Arraste da paleta ou use como classe do componente no Design. */
public class BotaoSecundario extends BotaoNex {

    public BotaoSecundario() {
        this("Botão");
    }

    public BotaoSecundario(String texto) {
        super(texto, SECUNDARIO);
    }
}
