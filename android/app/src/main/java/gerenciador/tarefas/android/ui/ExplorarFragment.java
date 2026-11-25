package gerenciador.tarefas.android.ui;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import gerenciador.tarefas.android.R;
import gerenciador.tarefas.android.rede.Rede;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.TreeSet;
import modelDominio.Inscricao;
import modelDominio.Projeto;

/** FormProcura do desktop: o participante encontra projetos e pede para entrar. */
public class ExplorarFragment extends ListaFragment {

    private static final class Dados {
        ArrayList<Inscricao> disponiveis;
        boolean semProjeto;
    }

    private List<Inscricao> disponiveis = new ArrayList<>();
    private final List<String> areas = new ArrayList<>();
    private EditText inputBusca;

    @Override
    protected void configurar() {
        txtTitulo.setText("Explorar projetos");
        raiz.findViewById(R.id.campoBusca).setVisibility(View.VISIBLE);
        inputBusca = raiz.findViewById(R.id.inputBusca);
        inputBusca.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int a, int b, int c) { }
            @Override public void onTextChanged(CharSequence s, int a, int b, int c) { }
            @Override public void afterTextChanged(Editable s) { filtrar(); }
        });
        adapter.setAoTocarBotao(item -> inscrever(((Inscricao) item.dado).getProjeto()));
    }

    @Override
    protected void carregar() {
        Rede.rodar(this, () -> {
            Dados d = new Dados();
            d.disponiveis = conexao().inscricaoProjetosDisponiveis();
            d.semProjeto = !conexao().isCriador() && !conexao().usuarioParticipaDeProjeto();
            return d;
        }, d -> {
            disponiveis = d.disponiveis;
            txtSubtitulo.setText(d.semProjeto
                    ? "Você ainda não participa de nenhum projeto. Escolha um abaixo e peça para entrar."
                    : "Encontre novos projetos e peça para participar.");
            montarFiltrosDeArea();
            filtrar();
        });
    }

    /** Chips com as áreas que existem nos projetos disponíveis, em ordem alfabética. */
    private void montarFiltrosDeArea() {
        TreeSet<String> novas = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        for (Inscricao i : disponiveis) {
            String area = i.getProjeto().getAreaAtuacao();
            if (!Status.vazio(area)) {
                novas.add(area.trim());
            }
        }
        if (new ArrayList<>(novas).equals(areas)) {
            return;   // nada mudou: mantém o chip escolhido
        }
        areas.clear();
        areas.addAll(novas);
        String[] rotulos = new String[areas.size() + 1];
        rotulos[0] = "Todas as áreas";
        for (int i = 0; i < areas.size(); i++) {
            rotulos[i + 1] = areas.get(i);
        }
        criarFiltros(rotulos, (g, ids) -> filtrar());
    }

    private void filtrar() {
        String busca = normalizar(inputBusca.getText().toString());
        int marcado = filtros.getChildCount() == 0 ? 0 : filtroMarcado();
        String area = marcado == 0 || marcado > areas.size() ? null : areas.get(marcado - 1);

        List<Item> itens = new ArrayList<>();
        for (Inscricao i : disponiveis) {
            Projeto p = i.getProjeto();
            String areaProjeto = p.getAreaAtuacao() == null ? "" : p.getAreaAtuacao().trim();
            boolean areaOk = area == null || area.equalsIgnoreCase(areaProjeto);
            String texto = normalizar(p.getTitulo() + " " + p.getDescricao());
            if (!areaOk || (!busca.isEmpty() && !texto.contains(busca))) {
                continue;
            }
            Item it = Item.card(i, p.getTitulo());
            it.topo = areaProjeto.isEmpty() ? "Área não informada" : areaProjeto;
            it.sub = Status.vazio(p.getDescricao()) ? "Sem descrição." : p.getDescricao();
            it.rodape = p.getUsuario() != null ? "Criado por " + p.getUsuario().getNome() : null;
            // Sem inscrição: "Inscrever-se". Pendente: aguardando. Recusado: pode tentar de novo.
            if (i.isPendente()) {
                it.textoBotao = "Aguardando aprovação";
                it.botaoAtivo = false;
                it.status = Inscricao.PENDENTE;
            } else if (i.isRecusado()) {
                it.textoBotao = "Tentar de novo";
                it.status = Inscricao.RECUSADO;
            } else {
                it.textoBotao = "Inscrever-se";
            }
            itens.add(it);
        }
        mostrar(itens, disponiveis.isEmpty()
                ? "Nenhum projeto aberto para inscrições no momento."
                : "Nenhum projeto encontrado.");
    }

    private void inscrever(Projeto projeto) {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Inscrever-se")
                .setMessage("Pedir para participar de \"" + projeto.getTitulo() + "\"?\n"
                        + "O criador do projeto vai aprovar ou recusar sua inscrição.")
                .setPositiveButton("Pedir para entrar", (d, w) -> Rede.rodar(this,
                        () -> conexao().inscricaoInscrever(projeto.getId()), ok -> {
                            new MaterialAlertDialogBuilder(requireContext())
                                    .setTitle("Inscrever-se")
                                    .setMessage(ok
                                            ? "Inscrição enviada!\nAssim que o criador aprovar, o projeto aparecerá no seu Início."
                                            : "Não foi possível fazer a inscrição.\nO projeto pode ter sido fechado ou você já está inscrito.")
                                    .setPositiveButton("OK", null)
                                    .show();
                            carregar();
                        }))
                .setNegativeButton("Cancelar", null)
                .show();
    }

    /** Minúsculas e sem acento, para "Inteligência" achar "inteligencia". */
    private static String normalizar(String s) {
        if (s == null) {
            return "";
        }
        String semAcento = Normalizer.normalize(s, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
        return semAcento.toLowerCase(Locale.ROOT).trim();
    }
}
