package gerenciador.tarefas.android.ui;

import android.content.Intent;
import android.widget.Toast;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import gerenciador.tarefas.android.R;
import gerenciador.tarefas.android.rede.Rede;
import java.util.ArrayList;
import java.util.List;
import modelDominio.EntregaTarefa;
import modelDominio.Tarefa;

/** TabelaTarefa1 do desktop. Criador: todas as tarefas dos seus projetos. Participante: as dele. */
public class TarefasFragment extends ListaFragment {

    private static final String[] ROTULOS = {"Todas", "Pendente", "Em andamento", "Concluída", "Atrasada"};
    private static final String[] FILTROS = {null, "pendente", "em_andamento", "concluida", "atrasada"};

    private boolean criador;
    private List<Tarefa> tarefas = new ArrayList<>();

    @Override
    protected void configurar() {
        criador = conexao().isCriador();
        txtTitulo.setText(criador ? "Tarefas" : "Minhas tarefas");
        criarFiltros(ROTULOS, (g, ids) -> filtrar());
        if (criador) {
            mostrarFab("Nova tarefa", v -> startActivity(new Intent(requireContext(), TarefaFormActivity.class)));
            adapter.setAoTocar(item -> acoesCriador((Tarefa) item.dado));
        } else {
            adapter.setAoTocarBotao(item -> entregar((Tarefa) item.dado));
        }
    }

    @Override
    protected void carregar() {
        Rede.rodar(this, () -> {
            int id = conexao().getUsuarioLogado().getId();
            return criador ? conexao().tarefaListaPorCriador(id) : conexao().tarefaListaPorResponsavel(id);
        }, lista -> {
            tarefas = lista;
            filtrar();
        });
    }

    private void filtrar() {
        String filtro = FILTROS[filtroMarcado()];
        List<Item> itens = new ArrayList<>();
        for (Tarefa t : tarefas) {
            String status = Status.statusExibido(t);
            if (filtro != null && !filtro.equals(status)) {
                continue;
            }
            Item it = Item.card(t, t.getTitulo());
            it.topo = t.getProjeto() != null ? t.getProjeto().getTitulo() : null;
            it.status = status;
            it.corMarcador = "atrasada".equals(status) ? R.color.vermelho
                    : "concluida".equals(status) ? R.color.azul : R.color.limao;
            it.sub = criador && t.getUsuario() != null ? "Responsável: " + t.getUsuario().getNome() : null;
            it.rodape = "Prazo: " + Status.data(t.getPrazo());
            if (!criador && !"concluida".equals(t.getStatus())) {
                it.textoBotao = "Entregar";
            }
            itens.add(it);
        }
        int n = itens.size();
        txtSubtitulo.setText(n + (n == 1 ? " tarefa" : " tarefas"));
        mostrar(itens, tarefas.isEmpty()
                ? (criador ? "Nenhuma tarefa cadastrada.\nToque em \"Nova tarefa\"." : "Nenhuma tarefa atribuída a você ainda.")
                : "Nenhuma tarefa com este status.");
    }

    private void acoesCriador(Tarefa tarefa) {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(tarefa.getTitulo())
                .setItems(new String[]{"Editar", "Excluir"}, (d, qual) -> {
                    if (qual == 0) {
                        Intent i = new Intent(requireContext(), TarefaFormActivity.class);
                        i.putExtra(TarefaFormActivity.EXTRA_TAREFA, tarefa);
                        startActivity(i);
                    } else {
                        new MaterialAlertDialogBuilder(requireContext())
                                .setTitle("Excluir tarefa")
                                .setMessage("Excluir \"" + tarefa.getTitulo() + "\"?")
                                .setPositiveButton("Excluir", (d2, w) -> Rede.rodar(this,
                                        () -> conexao().tarefaExcluir(tarefa), ok -> {
                                            Toast.makeText(requireContext(), ok ? "Tarefa excluída com sucesso!"
                                                    : "Erro ao excluir tarefa!", Toast.LENGTH_SHORT).show();
                                            carregar();
                                        }))
                                .setNegativeButton("Cancelar", null)
                                .show();
                    }
                })
                .show();
    }

    /** Participante: só deixa entregar se não houver outra entrega aguardando revisão. */
    private void entregar(Tarefa tarefa) {
        if ("concluida".equals(tarefa.getStatus())) {
            Toast.makeText(requireContext(), "Esta tarefa já foi concluída.", Toast.LENGTH_SHORT).show();
            return;
        }
        Rede.rodar(this, () -> {
            for (EntregaTarefa e : conexao().entregaTarefaListaPorResponsavel(conexao().getUsuarioLogado().getId())) {
                boolean aguardando = "enviada".equals(e.getStatus()) || "em_revisao".equals(e.getStatus());
                if (aguardando && e.getTarefa() != null && e.getTarefa().getId() == tarefa.getId()) {
                    return true;
                }
            }
            return false;
        }, jaTem -> {
            if (jaTem) {
                new MaterialAlertDialogBuilder(requireContext())
                        .setMessage("Já existe uma entrega desta tarefa aguardando revisão do criador.")
                        .setPositiveButton("OK", null)
                        .show();
            } else {
                Intent i = new Intent(requireContext(), EntregaFormActivity.class);
                i.putExtra(EntregaFormActivity.EXTRA_TAREFA, tarefa);
                startActivity(i);
            }
        });
    }
}
