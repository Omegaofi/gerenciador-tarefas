package gerenciador.tarefas.android.ui;

import android.widget.ArrayAdapter;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import java.util.ArrayList;
import java.util.List;

/** Lista suspensa do Material (o JComboBox do desktop), guardando o objeto escolhido. */
public class Dropdown<T> {

    public interface Rotulo<T> {
        String de(T item);
    }

    public interface AoEscolher<T> {
        void escolheu(T item);
    }

    private final MaterialAutoCompleteTextView campo;
    private final Rotulo<T> rotulo;
    private final List<T> itens = new ArrayList<>();
    private T selecionado;
    private AoEscolher<T> aoEscolher;

    public Dropdown(MaterialAutoCompleteTextView campo, Rotulo<T> rotulo) {
        this.campo = campo;
        this.rotulo = rotulo;
        campo.setOnItemClickListener((parent, view, position, id) -> {
            selecionado = itens.get(position);
            if (aoEscolher != null) {
                aoEscolher.escolheu(selecionado);
            }
        });
    }

    public void setAoEscolher(AoEscolher<T> aoEscolher) {
        this.aoEscolher = aoEscolher;
    }

    public void setItens(List<T> novos) {
        itens.clear();
        itens.addAll(novos);
        List<String> textos = new ArrayList<>();
        for (T t : itens) {
            textos.add(rotulo.de(t));
        }
        campo.setAdapter(new ArrayAdapter<>(campo.getContext(), android.R.layout.simple_list_item_1, textos));
        selecionar(null);
    }

    public List<T> getItens() {
        return itens;
    }

    public T getSelecionado() {
        return selecionado;
    }

    /** Seleciona pelo critério (null limpa). */
    public void selecionar(T item) {
        selecionado = item;
        campo.setText(item == null ? "" : rotulo.de(item), false);
    }

    public void selecionarPrimeiro() {
        selecionar(itens.isEmpty() ? null : itens.get(0));
    }
}
