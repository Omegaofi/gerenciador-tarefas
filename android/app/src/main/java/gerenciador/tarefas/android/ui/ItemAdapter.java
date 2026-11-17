package gerenciador.tarefas.android.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import gerenciador.tarefas.android.R;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public interface AoTocar {
        void tocou(Item item);
    }

    private static final int CABECALHO = 0;
    private static final int CARD = 1;

    private final List<Item> itens = new ArrayList<>();
    private AoTocar aoTocar;
    private AoTocar aoTocarBotao;
    private AoTocar aoTocarBotao2;

    public void setItens(List<Item> novos) {
        itens.clear();
        itens.addAll(novos);
        notifyDataSetChanged();
    }

    public void setAoTocar(AoTocar aoTocar) {
        this.aoTocar = aoTocar;
    }

    public void setAoTocarBotao(AoTocar aoTocarBotao) {
        this.aoTocarBotao = aoTocarBotao;
    }

    public void setAoTocarBotao2(AoTocar aoTocarBotao2) {
        this.aoTocarBotao2 = aoTocarBotao2;
    }

    @Override
    public int getItemViewType(int position) {
        return itens.get(position).cabecalho ? CABECALHO : CARD;
    }

    @Override
    public int getItemCount() {
        return itens.size();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inf = LayoutInflater.from(parent.getContext());
        if (viewType == CABECALHO) {
            return new RecyclerView.ViewHolder(inf.inflate(R.layout.item_cabecalho, parent, false)) { };
        }
        return new CardHolder(inf.inflate(R.layout.item_card, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Item item = itens.get(position);
        if (item.cabecalho) {
            ((TextView) holder.itemView).setText(item.titulo.toUpperCase(new Locale("pt", "BR")));
            return;
        }
        CardHolder h = (CardHolder) holder;
        mostrar(h.topo, item.topo == null ? null : item.topo.toUpperCase(new Locale("pt", "BR")));
        mostrar(h.titulo, item.titulo);
        mostrar(h.sub, item.sub);
        mostrar(h.rodape, item.rodape);
        h.marcador.setBackgroundColor(h.itemView.getContext().getColor(item.corMarcador));
        if (item.status != null) {
            h.badge.setVisibility(View.VISIBLE);
            Status.pintarBadge(h.badge, item.status);
        } else {
            h.badge.setVisibility(View.GONE);
        }
        if (item.progresso >= 0) {
            h.progresso.setVisibility(View.VISIBLE);
            h.progresso.setProgress(item.progresso);
        } else {
            h.progresso.setVisibility(View.GONE);
        }
        if (item.textoBotao != null) {
            h.botao.setVisibility(View.VISIBLE);
            h.botao.setText(item.textoBotao);
            h.botao.setEnabled(item.botaoAtivo);
            h.botao.setOnClickListener(v -> {
                if (aoTocarBotao != null) {
                    aoTocarBotao.tocou(item);
                }
            });
        } else {
            h.botao.setVisibility(View.GONE);
        }
        if (item.textoBotao2 != null) {
            h.botao2.setVisibility(View.VISIBLE);
            h.botao2.setText(item.textoBotao2);
            h.botao2.setOnClickListener(v -> {
                if (aoTocarBotao2 != null) {
                    aoTocarBotao2.tocou(item);
                }
            });
        } else {
            h.botao2.setVisibility(View.GONE);
        }
        h.itemView.setOnClickListener(aoTocar == null ? null : v -> aoTocar.tocou(item));
        h.itemView.setClickable(aoTocar != null);
    }

    private static void mostrar(TextView v, String texto) {
        v.setVisibility(texto == null || texto.isEmpty() ? View.GONE : View.VISIBLE);
        v.setText(texto);
    }

    static class CardHolder extends RecyclerView.ViewHolder {
        final View marcador;
        final TextView topo;
        final TextView badge;
        final TextView titulo;
        final TextView sub;
        final TextView rodape;
        final LinearProgressIndicator progresso;
        final MaterialButton botao;
        final MaterialButton botao2;

        CardHolder(View v) {
            super(v);
            marcador = v.findViewById(R.id.marcador);
            topo = v.findViewById(R.id.txtTopo);
            badge = v.findViewById(R.id.txtBadge);
            titulo = v.findViewById(R.id.txtTitulo);
            sub = v.findViewById(R.id.txtSub);
            rodape = v.findViewById(R.id.txtRodape);
            progresso = v.findViewById(R.id.progresso);
            botao = v.findViewById(R.id.botao);
            botao2 = v.findViewById(R.id.botao2);
        }
    }
}
