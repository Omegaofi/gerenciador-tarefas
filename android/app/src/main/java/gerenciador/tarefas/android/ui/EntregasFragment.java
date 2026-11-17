package gerenciador.tarefas.android.ui;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import gerenciador.tarefas.android.R;
import gerenciador.tarefas.android.rede.Rede;
import java.util.ArrayList;
import java.util.List;
import modelDominio.EntregaTarefa;

/** TabelaEntrega1 do desktop. Criador revisa (aprova/rejeita); participante acompanha. */
public class EntregasFragment extends ListaFragment {

    private boolean criador;

    @Override
    protected void configurar() {
        criador = conexao().isCriador();
        txtTitulo.setText(criador ? "Entregas para revisar" : "Minhas entregas");
        adapter.setAoTocar(item -> detalhes((EntregaTarefa) item.dado));
        adapter.setAoTocarBotao(item -> detalhes((EntregaTarefa) item.dado));
    }

    @Override
    protected void carregar() {
        Rede.rodar(this, () -> {
            int id = conexao().getUsuarioLogado().getId();
            return criador ? conexao().entregaTarefaListaPorCriador(id) : conexao().entregaTarefaListaPorResponsavel(id);
        }, lista -> {
            int aguardando = 0;
            List<Item> itens = new ArrayList<>();
            for (EntregaTarefa e : lista) {
                boolean revisar = aguardandoRevisao(e);
                if (revisar) {
                    aguardando++;
                }
                Item it = Item.card(e, e.getTarefa() != null ? e.getTarefa().getTitulo() : "Tarefa");
                it.topo = e.getTarefa() != null && e.getTarefa().getProjeto() != null
                        ? e.getTarefa().getProjeto().getTitulo() : null;
                it.status = e.getStatus();
                it.sub = e.getArquivo() != null ? e.getArquivo().getNomeArquivo() : null;
                String quem = e.getTarefa() != null && e.getTarefa().getUsuario() != null
                        ? e.getTarefa().getUsuario().getNome() : "";
                it.rodape = (criador && !quem.isEmpty() ? quem + "  ·  " : "") + "Enviada em " + Status.dataHora(e.getDataEntrega());
                it.corMarcador = revisar ? R.color.preto : "rejeitada".equals(e.getStatus()) ? R.color.vermelho : R.color.limao;
                if (criador && revisar) {
                    it.textoBotao = "Revisar";
                }
                itens.add(it);
            }
            txtSubtitulo.setText(criador
                    ? aguardando + (aguardando == 1 ? " entrega aguardando revisão" : " entregas aguardando revisão")
                    : lista.size() + (lista.size() == 1 ? " entrega enviada" : " entregas enviadas"));
            mostrar(itens, criador ? "Nenhuma entrega recebida ainda."
                    : "Você ainda não enviou entregas.\nUse \"Entregar\" na aba Tarefas.");
        });
    }

    private static boolean aguardandoRevisao(EntregaTarefa e) {
        return "enviada".equals(e.getStatus()) || "em_revisao".equals(e.getStatus());
    }

    private void detalhes(EntregaTarefa e) {
        String link = e.getArquivo() != null ? e.getArquivo().getArquivoUrl() : null;
        StringBuilder msg = new StringBuilder();
        if (e.getArquivo() != null && !Status.vazio(e.getArquivo().getNomeArquivo())) {
            msg.append(e.getArquivo().getNomeArquivo()).append("\n\n");
        }
        msg.append("Status: ").append(Status.rotulo(e.getStatus()));
        msg.append("\nEnviada em: ").append(Status.dataHora(e.getDataEntrega()));
        if (link != null) {
            msg.append("\nLink: ").append(link);
        }

        MaterialAlertDialogBuilder b = new MaterialAlertDialogBuilder(requireContext())
                .setTitle(e.getTarefa() != null ? e.getTarefa().getTitulo() : "Entrega")
                .setMessage(msg);
        if (link != null) {
            b.setNeutralButton("Abrir link", (d, w) -> abrirLink(link));
        }
        if (criador && aguardandoRevisao(e)) {
            b.setPositiveButton("Aprovar", (d, w) -> avaliar(e, "aprovada"));
            b.setNegativeButton("Rejeitar", (d, w) -> avaliar(e, "rejeitada"));
        } else {
            b.setPositiveButton("Fechar", null);
        }
        AlertDialog dialogo = b.show();
        if (criador && aguardandoRevisao(e)) {
            dialogo.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(requireContext().getColor(R.color.vermelho));
        }
    }

    private void avaliar(EntregaTarefa entrega, String novoStatus) {
        String acao = "aprovada".equals(novoStatus)
                ? "Aprovar a entrega? A tarefa será marcada como concluída."
                : "Rejeitar a entrega? A tarefa volta para \"em andamento\" para o colaborador refazer.";
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Revisar entrega")
                .setMessage(acao)
                .setPositiveButton("Sim", (d, w) -> {
                    entrega.setStatus(novoStatus);
                    Rede.rodar(this, () -> conexao().entregaTarefaAvaliar(entrega), ok -> {
                        if (!ok) {
                            Toast.makeText(requireContext(), "Erro ao salvar a revisão!", Toast.LENGTH_LONG).show();
                        }
                        carregar();
                    });
                })
                .setNegativeButton("Não", null)
                .show();
    }

    private void abrirLink(String url) {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
        } catch (ActivityNotFoundException ex) {
            Toast.makeText(requireContext(), "Nenhum app para abrir: " + url, Toast.LENGTH_LONG).show();
        }
    }
}
