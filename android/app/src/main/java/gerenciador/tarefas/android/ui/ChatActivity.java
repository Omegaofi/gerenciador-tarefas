package gerenciador.tarefas.android.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import gerenciador.tarefas.android.R;
import gerenciador.tarefas.android.cripto.ChatCifrado;
import gerenciador.tarefas.android.cripto.MensagemAberta;
import gerenciador.tarefas.android.rede.Rede;
import gerenciador.tarefas.android.rede.Sessao;
import java.util.ArrayList;
import java.util.List;
import modelDominio.Projeto;

/**
 * TelaChat1 do desktop: chat cifrado de ponta a ponta de um projeto.
 * A cada 3 segundos busca só as mensagens novas e decifra no próprio celular.
 */
public class ChatActivity extends BaseActivity {

    private static final String EXTRA_PROJETO = "projeto";
    private static final long INTERVALO_ATUALIZACAO = 3000;

    public static void abrir(Context contexto, Projeto projeto) {
        Intent i = new Intent(contexto, ChatActivity.class);
        i.putExtra(EXTRA_PROJETO, projeto);
        contexto.startActivity(i);
    }

    private Projeto projeto;
    private int euId;
    private int ultimaMensagemId;
    private boolean buscando;
    private final List<MensagemAberta> conversa = new ArrayList<>();
    private final MensagensAdapter adapter = new MensagensAdapter();
    private final Handler relogio = new Handler(Looper.getMainLooper());
    private final Runnable tique = new Runnable() {
        @Override
        public void run() {
            atualizar();
            relogio.postDelayed(this, INTERVALO_ATUALIZACAO);
        }
    };

    private RecyclerView lista;
    private TextView inputMensagem;
    private View botaoEnviar;
    private TextView txtAviso;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (semSessao()) {
            return;
        }
        setContentView(R.layout.activity_chat);
        projeto = (Projeto) getIntent().getSerializableExtra(EXTRA_PROJETO);
        euId = conexao().getUsuarioLogado().getId();
        configurarToolbar(projeto.getTitulo(), true).setSubtitle("Chat cifrado de ponta a ponta");

        lista = findViewById(R.id.listaMensagens);
        LinearLayoutManager lm = new LinearLayoutManager(this);
        lm.setStackFromEnd(true);
        lista.setLayoutManager(lm);
        lista.setAdapter(adapter);
        inputMensagem = findViewById(R.id.inputMensagem);
        botaoEnviar = findViewById(R.id.botaoEnviar);
        txtAviso = findViewById(R.id.txtAviso);

        botaoEnviar.setOnClickListener(v -> enviar());
        inputMensagem.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                enviar();
                return true;
            }
            return false;
        });

        if (!chat().isCofreAberto()) {
            inputMensagem.setEnabled(false);
            botaoEnviar.setEnabled(false);
            aviso("As chaves do chat não estão abertas. Saia e entre de novo para ler as mensagens.");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (projeto != null && chat().isCofreAberto()) {
            relogio.post(tique);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        relogio.removeCallbacks(tique);
    }

    private ChatCifrado chat() {
        return conexao().getChatCifrado();
    }

    private void aviso(String texto) {
        txtAviso.setVisibility(texto == null ? View.GONE : View.VISIBLE);
        txtAviso.setText(texto);
    }

    /** Busca só as mensagens novas (id maior que a última exibida) e decifra no app. */
    private void atualizar() {
        if (buscando || !Sessao.logado()) {
            return;
        }
        buscando = true;
        int depoisDe = ultimaMensagemId;
        Rede.rodar(this, () -> chat().carregar(projeto, depoisDe), novas -> {
            buscando = false;
            if (novas.isEmpty()) {
                if (conversa.isEmpty()) {
                    aviso("Nenhuma mensagem ainda. Diga olá! As mensagens são cifradas no seu celular.");
                }
                return;
            }
            aviso(null);
            int inicio = conversa.size();
            for (MensagemAberta m : novas) {
                if (m.id() > ultimaMensagemId) {
                    conversa.add(m);
                    ultimaMensagemId = m.id();
                }
            }
            adapter.notifyItemRangeInserted(inicio, conversa.size() - inicio);
            lista.scrollToPosition(conversa.size() - 1);
        }, e -> {
            buscando = false;
            aviso("Erro ao carregar mensagens: " + e.getMessage());
        });
    }

    private void enviar() {
        String texto = inputMensagem.getText().toString().trim();
        if (texto.isEmpty()) {
            return;
        }
        botaoEnviar.setEnabled(false);
        Rede.rodar(this, () -> chat().enviar(projeto, texto), semChat -> {
            botaoEnviar.setEnabled(true);
            inputMensagem.setText("");
            if (!semChat.isEmpty()) {
                new MaterialAlertDialogBuilder(this)
                        .setTitle("Chat cifrado")
                        .setMessage("Mensagem enviada, mas estes membros ainda não ativaram o chat e não vão conseguir lê-la:\n\n"
                                + String.join(", ", semChat)
                                + "\n\nEles ativam o chat ao entrar no app pela primeira vez.")
                        .setPositiveButton("OK", null)
                        .show();
            }
            atualizar();
        }, e -> {
            botaoEnviar.setEnabled(true);
            e.printStackTrace();
            new MaterialAlertDialogBuilder(this)
                    .setMessage("Erro ao enviar mensagem: " + e.getMessage())
                    .setPositiveButton("OK", null)
                    .show();
        });
    }

    private class MensagensAdapter extends RecyclerView.Adapter<MensagensAdapter.Holder> {

        @NonNull
        @Override
        public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mensagem, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull Holder h, int position) {
            MensagemAberta m = conversa.get(position);
            boolean minha = m.autorId() == euId;
            boolean problema = m.estado() != MensagemAberta.Estado.CIFRADA_OK;

            ((LinearLayout) h.itemView).setGravity(minha ? Gravity.END : Gravity.START);
            h.bolha.setBackgroundResource(minha ? R.drawable.bolha_minha : R.drawable.bolha_outro);
            int corTexto = getColor(minha ? android.R.color.white : R.color.texto);
            int corClara = getColor(minha ? R.color.texto_menu : R.color.texto_claro);

            h.autor.setVisibility(minha ? View.GONE : View.VISIBLE);
            h.autor.setText(m.autorNome());
            h.autor.setTextColor(getColor(minha ? R.color.limao : R.color.texto));
            h.texto.setText(problema ? "⚠ " + m.texto() : m.texto());
            h.texto.setTextColor(problema ? corClara : corTexto);
            h.texto.setTypeface(null, problema ? android.graphics.Typeface.ITALIC : android.graphics.Typeface.NORMAL);
            h.hora.setText(Status.dataHora(m.dataEnvio()));
            h.hora.setTextColor(corClara);
        }

        @Override
        public int getItemCount() {
            return conversa.size();
        }

        class Holder extends RecyclerView.ViewHolder {
            final View bolha;
            final TextView autor;
            final TextView texto;
            final TextView hora;

            Holder(View v) {
                super(v);
                bolha = v.findViewById(R.id.bolha);
                autor = v.findViewById(R.id.txtAutor);
                texto = v.findViewById(R.id.txtTexto);
                hora = v.findViewById(R.id.txtHora);
            }
        }
    }
}
