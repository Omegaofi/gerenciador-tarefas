package gerenciador.tarefas.android.ui;

import gerenciador.tarefas.android.rede.ConexaoController;
import gerenciador.tarefas.android.rede.Rede;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import modelDominio.Projeto;
import modelDominio.Tarefa;

/** Lista de conversas (a lista lateral do TelaChat1): cada projeto tem o seu chat cifrado. */
public class ConversasFragment extends ListaFragment {

    @Override
    protected void configurar() {
        txtTitulo.setText("Chat");
        txtSubtitulo.setText("Mensagens cifradas de ponta a ponta. Só os membros do projeto conseguem ler.");
        adapter.setAoTocar(item -> ChatActivity.abrir(requireContext(), (Projeto) item.dado));
    }

    @Override
    protected void carregar() {
        Rede.rodar(this, () -> {
            ConexaoController c = conexao();
            int id = c.getUsuarioLogado().getId();
            if (c.isCriador()) {
                return c.projetoListaPorCriador(id);
            }
            // Participante: projetos em que foi aceito + projetos das tarefas dele
            Map<Integer, Projeto> porId = new LinkedHashMap<>();
            for (Projeto p : c.projetoListaParticipando()) {
                porId.put(p.getId(), p);
            }
            for (Tarefa t : c.tarefaListaPorResponsavel(id)) {
                if (t.getProjeto() != null) {
                    porId.putIfAbsent(t.getProjeto().getId(), t.getProjeto());
                }
            }
            return new ArrayList<Projeto>(porId.values());
        }, projetos -> {
            List<Item> itens = new ArrayList<>();
            for (Projeto p : projetos) {
                Item it = Item.card(p, p.getTitulo());
                it.topo = Status.vazio(p.getAreaAtuacao()) ? null : p.getAreaAtuacao();
                it.sub = "Toque para abrir a conversa";
                it.corMarcador = Status.corProjeto(p.getStatus());
                it.status = p.getStatus();
                itens.add(it);
            }
            mostrar(itens, "Você ainda não participa de nenhum projeto para conversar.");
        });
    }
}
