package gerenciador.tarefas.android.ui;

import android.widget.Toast;
import androidx.fragment.app.Fragment;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import gerenciador.tarefas.android.R;
import gerenciador.tarefas.android.rede.Rede;
import gerenciador.tarefas.android.rede.Sessao;
import modelDominio.Inscricao;

/** Aprovar/recusar inscrições, usado no Início e na tela Inscrições. */
final class Inscricoes {

    private Inscricoes() {
    }

    /** Cartão de uma inscrição pendente com os botões Recusar e Aprovar. */
    static Item card(Inscricao i) {
        Item it = Item.card(i, i.getUsuario().getNome());
        it.topo = "Quer entrar em: " + i.getProjeto().getTitulo();
        it.sub = i.getUsuario().getEmail()
                + (Status.vazio(i.getUsuario().getBiografia()) ? "" : "\n" + i.getUsuario().getBiografia());
        it.rodape = "Pediu em " + Status.dataHora(i.getDataInscricao());
        it.status = i.getStatus();
        it.corMarcador = R.color.laranja;
        it.textoBotao = "Aprovar";
        it.textoBotao2 = "Recusar";
        return it;
    }

    /** Aprova direto; recusar pede confirmação. Depois chama {@code depois} para recarregar a lista. */
    static void responder(Fragment tela, Inscricao inscricao, boolean aprovar, Runnable depois) {
        if (aprovar) {
            enviar(tela, inscricao, true, depois);
            return;
        }
        new MaterialAlertDialogBuilder(tela.requireContext())
                .setTitle("Recusar inscrição")
                .setMessage("Recusar a inscrição de " + inscricao.getUsuario().getNome()
                        + " em \"" + inscricao.getProjeto().getTitulo() + "\"?")
                .setPositiveButton("Recusar", (d, w) -> enviar(tela, inscricao, false, depois))
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private static void enviar(Fragment tela, Inscricao inscricao, boolean aprovar, Runnable depois) {
        String nome = inscricao.getUsuario().getNome();
        String projeto = inscricao.getProjeto().getTitulo();
        Rede.rodar(tela, () -> Sessao.conexao().inscricaoResponder(
                inscricao.getProjeto().getId(), inscricao.getUsuario().getId(), aprovar), ok -> {
            Toast.makeText(tela.requireContext(), !ok
                    ? "Não foi possível responder. A inscrição pode já ter sido respondida."
                    : aprovar ? nome + " agora faz parte de \"" + projeto + "\". Já pode receber tarefas."
                    : "Inscrição recusada.", Toast.LENGTH_LONG).show();
            depois.run();
        });
    }
}
