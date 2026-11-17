package gerenciador.tarefas.android.ui;

import gerenciador.tarefas.android.rede.Rede;
import java.util.ArrayList;
import java.util.List;
import modelDominio.Inscricao;

/** Lista das inscrições pendentes, cada uma com Aprovar e Recusar. */
public class InscricoesFragment extends ListaFragment {

    @Override
    protected void configurar() {
        txtTitulo.setText("Inscrições pendentes");
        adapter.setAoTocarBotao(item -> Inscricoes.responder(this, (Inscricao) item.dado, true, this::carregar));
        adapter.setAoTocarBotao2(item -> Inscricoes.responder(this, (Inscricao) item.dado, false, this::carregar));
    }

    @Override
    protected void carregar() {
        Rede.rodar(this, () -> conexao().inscricaoPendentesCriador(), lista -> {
            int total = lista.size();
            txtSubtitulo.setText(total == 0 ? "Ninguém aguardando resposta."
                    : total + (total == 1 ? " pessoa quer" : " pessoas querem") + " participar dos seus projetos.");
            List<Item> itens = new ArrayList<>();
            for (Inscricao i : lista) {
                itens.add(Inscricoes.card(i));
            }
            mostrar(itens, "Nenhuma inscrição pendente.\nQuando alguém pedir para entrar num projeto seu, aparece aqui.");
        });
    }
}
