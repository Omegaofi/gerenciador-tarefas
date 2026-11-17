package gerenciador.tarefas.android.ui;

import android.content.Intent;
import android.widget.Toast;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import gerenciador.tarefas.android.rede.Rede;
import java.util.ArrayList;
import java.util.List;
import modelDominio.Projeto;

/** TabelaProjeto1 do desktop (só para o Criador). */
public class ProjetosFragment extends ListaFragment {

    private static final class Dados {
        ArrayList<Projeto> projetos;
        int pendentes;
    }

    @Override
    protected void configurar() {
        txtTitulo.setText("Meus projetos");
        mostrarFab("Novo projeto", v -> startActivity(new Intent(requireContext(), ProjetoFormActivity.class)));
        adapter.setAoTocar(item -> {
            if (item.dado instanceof Projeto) {
                acoes((Projeto) item.dado);
            }
        });
        adapter.setAoTocarBotao(item -> startActivity(new Intent(requireContext(), InscricoesActivity.class)));
    }

    @Override
    protected void carregar() {
        Rede.rodar(this, () -> {
            Dados d = new Dados();
            d.projetos = conexao().projetoListaPorCriador(conexao().getUsuarioLogado().getId());
            d.pendentes = conexao().inscricaoPendentesCriador().size();
            return d;
        }, d -> {
            int n = d.projetos.size();
            txtSubtitulo.setText(n + (n == 1 ? " projeto" : " projetos"));
            List<Item> itens = new ArrayList<>();
            if (d.pendentes > 0) {
                Item aviso = Item.card(null, d.pendentes + (d.pendentes == 1
                        ? " pessoa quer participar" : " pessoas querem participar"));
                aviso.sub = "Aprove as inscrições para poder atribuir tarefas.";
                aviso.textoBotao = "Ver inscrições";
                itens.add(aviso);
            }
            for (Projeto p : d.projetos) {
                Item it = Item.card(p, p.getTitulo());
                it.topo = Status.vazio(p.getAreaAtuacao()) ? "Área não informada" : p.getAreaAtuacao();
                it.sub = Status.vazio(p.getDescricao()) ? "Sem descrição." : p.getDescricao();
                it.status = p.getStatus();
                it.corMarcador = Status.corProjeto(p.getStatus());
                it.rodape = "Criado em " + Status.data(p.getCriadoEm());
                itens.add(it);
            }
            mostrar(itens, "Você ainda não criou projetos.\nToque em \"Novo projeto\" para começar.");
        });
    }

    private void acoes(Projeto projeto) {
        String[] opcoes = {"Editar", "Abrir chat", "Excluir"};
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(projeto.getTitulo())
                .setItems(opcoes, (d, qual) -> {
                    if (qual == 0) {
                        Intent i = new Intent(requireContext(), ProjetoFormActivity.class);
                        i.putExtra(ProjetoFormActivity.EXTRA_PROJETO, projeto);
                        startActivity(i);
                    } else if (qual == 1) {
                        ChatActivity.abrir(requireContext(), projeto);
                    } else {
                        confirmarExclusao(projeto);
                    }
                })
                .show();
    }

    private void confirmarExclusao(Projeto projeto) {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Excluir projeto")
                .setMessage("Excluir \"" + projeto.getTitulo() + "\"? Esta ação não pode ser desfeita.")
                .setPositiveButton("Excluir", (d, w) -> Rede.rodar(this, () -> conexao().projetoExcluir(projeto), ok -> {
                    Toast.makeText(requireContext(), ok ? "Projeto excluído com sucesso!"
                            : "Erro ao excluir. O projeto pode ter tarefas vinculadas.", Toast.LENGTH_LONG).show();
                    carregar();
                }))
                .setNegativeButton("Cancelar", null)
                .show();
    }
}
