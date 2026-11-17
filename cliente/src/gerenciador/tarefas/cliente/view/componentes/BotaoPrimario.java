package gerenciador.tarefas.cliente.view.componentes;

/** Botão preto. Arraste da paleta ou use como classe do componente no Design. */
public class BotaoPrimario extends BotaoNex {

    public BotaoPrimario() {
        this("Botão");
    }

    public BotaoPrimario(String texto) {
        super(texto, PRIMARIO);
    }
}
