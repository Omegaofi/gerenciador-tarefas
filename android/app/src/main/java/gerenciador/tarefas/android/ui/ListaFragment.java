package gerenciador.tarefas.android.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import gerenciador.tarefas.android.R;
import gerenciador.tarefas.android.rede.ConexaoController;
import gerenciador.tarefas.android.rede.Sessao;
import java.util.List;

/** Base das telas de lista: título, filtros, lista de cartões, puxar para atualizar e botão "+". */
public abstract class ListaFragment extends Fragment {

    protected TextView txtTitulo;
    protected TextView txtSubtitulo;
    protected TextView txtVazio;
    protected SwipeRefreshLayout atualizar;
    protected ChipGroup filtros;
    protected ExtendedFloatingActionButton fab;
    protected final ItemAdapter adapter = new ItemAdapter();
    protected View raiz;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        raiz = inflater.inflate(R.layout.fragment_lista, container, false);
        txtTitulo = raiz.findViewById(R.id.txtTitulo);
        txtSubtitulo = raiz.findViewById(R.id.txtSubtitulo);
        txtVazio = raiz.findViewById(R.id.txtVazio);
        atualizar = raiz.findViewById(R.id.atualizar);
        filtros = raiz.findViewById(R.id.filtros);
        fab = raiz.findViewById(R.id.fab);
        RecyclerView lista = raiz.findViewById(R.id.lista);
        lista.setLayoutManager(new LinearLayoutManager(requireContext()));
        lista.setAdapter(adapter);
        atualizar.setColorSchemeResources(R.color.preto);
        atualizar.setOnRefreshListener(this::carregar);
        configurar();
        return raiz;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (Sessao.logado()) {
            atualizar.setRefreshing(true);
            carregar();   // volta de um formulário: recarrega
        }
    }

    /** Monta títulos, filtros e ações. */
    protected abstract void configurar();

    /** Busca no servidor e chama {@link #mostrar}. */
    protected abstract void carregar();

    protected ConexaoController conexao() {
        return Sessao.conexao();
    }

    protected void mostrar(List<Item> itens, String textoVazio) {
        atualizar.setRefreshing(false);
        adapter.setItens(itens);
        txtVazio.setText(textoVazio);
        txtVazio.setVisibility(itens.isEmpty() ? View.VISIBLE : View.GONE);
    }

    /** Cria os chips de filtro; o primeiro começa marcado. */
    protected void criarFiltros(String[] rotulos, ChipGroup.OnCheckedStateChangeListener aoMudar) {
        raiz.findViewById(R.id.rolagemFiltros).setVisibility(View.VISIBLE);
        filtros.removeAllViews();
        for (int i = 0; i < rotulos.length; i++) {
            Chip chip = new Chip(requireContext(), null, com.google.android.material.R.attr.chipStyle);
            chip.setId(View.generateViewId());
            chip.setTag(i);
            chip.setText(rotulos[i]);
            chip.setCheckable(true);
            chip.setChecked(i == 0);
            chip.setCheckedIconVisible(false);
            chip.setChipBackgroundColorResource(R.color.campo);
            filtros.addView(chip);
        }
        pintarChips();
        filtros.setOnCheckedStateChangeListener((g, ids) -> {
            pintarChips();
            aoMudar.onCheckedChanged(g, ids);
        });
    }

    /** Índice do chip marcado. */
    protected int filtroMarcado() {
        View v = filtros.findViewById(filtros.getCheckedChipId());
        return v == null ? 0 : (int) v.getTag();
    }

    private void pintarChips() {
        for (int i = 0; i < filtros.getChildCount(); i++) {
            Chip c = (Chip) filtros.getChildAt(i);
            c.setChipBackgroundColorResource(c.isChecked() ? R.color.preto : R.color.campo);
            c.setTextColor(requireContext().getColor(c.isChecked() ? android.R.color.white : R.color.texto));
        }
    }

    protected void mostrarFab(String texto, View.OnClickListener aoTocar) {
        fab.setVisibility(View.VISIBLE);
        fab.setText(texto);
        fab.setOnClickListener(aoTocar);
    }
}
