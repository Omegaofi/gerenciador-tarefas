package gerenciador.tarefas.android.ui;

import gerenciador.tarefas.android.R;

/** Um cartão (ou um cabeçalho de seção) das listas. Substitui as JTables do desktop. */
public class Item {

    public final boolean cabecalho;
    public String topo;
    public String titulo;
    public String sub;
    public String rodape;
    public String status;          // vira o selo colorido
    public int corMarcador = R.color.limao;
    public int progresso = -1;     // 0..100, ou -1 para esconder
    public String textoBotao;      // null esconde o botão
    public String textoBotao2;     // botão vermelho à esquerda (ex.: "Recusar"); null esconde
    public boolean botaoAtivo = true;
    public Object dado;            // o objeto do modelDominio por trás do cartão

    private Item(boolean cabecalho) {
        this.cabecalho = cabecalho;
    }

    public static Item cabecalho(String texto) {
        Item i = new Item(true);
        i.titulo = texto;
        return i;
    }

    public static Item card(Object dado, String titulo) {
        Item i = new Item(false);
        i.dado = dado;
        i.titulo = titulo;
        return i;
    }
}
