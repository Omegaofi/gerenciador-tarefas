package gerenciador.tarefas.android.ui;

import android.content.Intent;
import gerenciador.tarefas.android.R;
import gerenciador.tarefas.android.rede.ConexaoController;
import gerenciador.tarefas.android.rede.Rede;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import modelDominio.EntregaTarefa;
import modelDominio.Inscricao;
import modelDominio.Projeto;
import modelDominio.Tarefa;
import modelDominio.Usuario;

/** Dashboard (TelaPrincipal1 do desktop): projetos, próximos prazos e atividade recente. */
public class InicioFragment extends ListaFragment {

    private static final class Dados {
        ArrayList<Projeto> projetos;
        ArrayList<Tarefa> tarefas;
        ArrayList<EntregaTarefa> entregas;
        ArrayList<Inscricao> pendentes = new ArrayList<>();
    }

    private boolean criador;

    @Override
    protected void configurar() {
        criador = conexao().isCriador();
        Usuario logado = conexao().getUsuarioLogado();
        txtTitulo.setText("Olá, " + primeiroNome(logado.getNome()));
        if (criador) {
            mostrarFab("Novo projeto", v -> startActivity(new Intent(requireContext(), ProjetoFormActivity.class)));
        }
        adapter.setAoTocarBotao(item -> Inscricoes.responder(this, (Inscricao) item.dado, true, this::carregar));
        adapter.setAoTocarBotao2(item -> Inscricoes.responder(this, (Inscricao) item.dado, false, this::carregar));
        adapter.setAoTocar(item -> {
            if (item.dado instanceof Projeto || item.dado instanceof Tarefa) {
                ((MainActivity) requireActivity()).irPara(R.id.nav_tarefas);
            } else if (item.dado instanceof EntregaTarefa) {
                ((MainActivity) requireActivity()).irPara(R.id.nav_entregas);
            }
        });
    }

    @Override
    protected void carregar() {
        Rede.rodar(this, () -> {
            ConexaoController c = conexao();
            int id = c.getUsuarioLogado().getId();
            Dados d = new Dados();
            if (criador) {
                d.projetos = c.projetoListaPorCriador(id);
                d.tarefas = c.tarefaListaPorCriador(id);
                d.entregas = c.entregaTarefaListaPorCriador(id);
                d.pendentes = c.inscricaoPendentesCriador();
            } else {
                d.tarefas = c.tarefaListaPorResponsavel(id);
                d.projetos = projetosDoParticipante(c, d.tarefas);
                d.entregas = c.entregaTarefaListaPorResponsavel(id);
            }
            return d;
        }, this::montar);
    }

    /** Projetos em que foi aceito + projetos das tarefas atribuídas a ele. */
    private static ArrayList<Projeto> projetosDoParticipante(ConexaoController c, List<Tarefa> tarefas) {
        Map<Integer, Projeto> porId = new LinkedHashMap<>();
        for (Projeto p : c.projetoListaParticipando()) {
            porId.put(p.getId(), p);
        }
        for (Tarefa t : tarefas) {
            if (t.getProjeto() != null) {
                porId.putIfAbsent(t.getProjeto().getId(), t.getProjeto());
            }
        }
        return new ArrayList<>(porId.values());
    }

    private void montar(Dados d) {
        if (criador) {
            long ativos = d.projetos.stream()
                    .filter(p -> !"concluido".equals(p.getStatus()) && !"cancelado".equals(p.getStatus()))
                    .count();
            txtSubtitulo.setText(ativos + (ativos == 1 ? " projeto ativo" : " projetos ativos"));
        } else {
            int n = d.projetos.size();
            txtSubtitulo.setText(n + (n == 1 ? " projeto em que você participa" : " projetos em que você participa"));
        }

        List<Item> itens = new ArrayList<>();

        // Criador: quem pediu para entrar, com Aprovar/Recusar direto no cartão
        if (!d.pendentes.isEmpty()) {
            itens.add(Item.cabecalho("Inscrições pendentes (" + d.pendentes.size() + ")"));
            for (Inscricao insc : d.pendentes) {
                itens.add(Inscricoes.card(insc));
            }
        }

        itens.add(Item.cabecalho("Projetos"));
        if (d.projetos.isEmpty()) {
            Item vazio = Item.card(null, criador ? "Nenhum projeto ainda" : "Você ainda não participa de projetos");
            vazio.sub = criador
                    ? "Toque em \"Novo projeto\" para começar."
                    : "Use a aba Explorar para encontrar um projeto e pedir para entrar.";
            vazio.corMarcador = R.color.borda;
            itens.add(vazio);
        }
        for (Projeto p : d.projetos) {
            itens.add(cardProjeto(p, d.tarefas));
        }

        // Próximos prazos: tarefas abertas, as mais urgentes primeiro (o calendário do desktop)
        List<Tarefa> abertas = new ArrayList<>();
        for (Tarefa t : d.tarefas) {
            if (!"concluida".equals(t.getStatus())) {
                abertas.add(t);
            }
        }
        abertas.sort((a, b) -> {
            if (a.getPrazo() == null) return b.getPrazo() == null ? 0 : 1;
            if (b.getPrazo() == null) return -1;
            return a.getPrazo().compareTo(b.getPrazo());
        });
        itens.add(Item.cabecalho("Próximos prazos"));
        if (abertas.isEmpty()) {
            Item vazio = Item.card(null, "Nenhuma tarefa em aberto");
            vazio.corMarcador = R.color.borda;
            itens.add(vazio);
        }
        for (int i = 0; i < Math.min(5, abertas.size()); i++) {
            Tarefa t = abertas.get(i);
            Item it = Item.card(t, t.getTitulo());
            it.topo = t.getProjeto() != null ? t.getProjeto().getTitulo() : null;
            it.status = Status.statusExibido(t);
            it.rodape = "Prazo: " + Status.data(t.getPrazo())
                    + (criador && t.getUsuario() != null ? "  ·  " + t.getUsuario().getNome() : "");
            it.corMarcador = "atrasada".equals(it.status) ? R.color.vermelho : R.color.limao;
            itens.add(it);
        }

        itens.add(Item.cabecalho("Atividade recente"));
        if (d.entregas.isEmpty()) {
            Item vazio = Item.card(null, "Nenhuma entrega registrada ainda");
            vazio.corMarcador = R.color.borda;
            itens.add(vazio);
        }
        for (int i = 0; i < Math.min(5, d.entregas.size()); i++) {
            EntregaTarefa e = d.entregas.get(i);
            String quem = criador && e.getTarefa() != null && e.getTarefa().getUsuario() != null
                    ? e.getTarefa().getUsuario().getNome() : "Você";
            Item it = Item.card(e, quem + " enviou «" + (e.getTarefa() != null ? e.getTarefa().getTitulo() : "?") + "»");
            it.status = e.getStatus();
            it.rodape = Status.dataHora(e.getDataEntrega());
            itens.add(it);
        }
        mostrar(itens, "");
    }

    private Item cardProjeto(Projeto projeto, List<Tarefa> tarefas) {
        int total = 0;
        int concluidas = 0;
        Set<Integer> membros = new HashSet<>();
        for (Tarefa t : tarefas) {
            if (t.getProjeto() != null && t.getProjeto().getId() == projeto.getId()) {
                total++;
                if ("concluida".equals(t.getStatus())) {
                    concluidas++;
                }
                if (t.getUsuario() != null) {
                    membros.add(t.getUsuario().getId());
                }
            }
        }
        Item it = Item.card(projeto, projeto.getTitulo());
        it.topo = Status.vazio(projeto.getAreaAtuacao()) ? null : projeto.getAreaAtuacao();
        it.sub = Status.vazio(projeto.getDescricao()) ? null : projeto.getDescricao();
        it.status = projeto.getStatus();
        it.corMarcador = Status.corProjeto(projeto.getStatus());
        it.progresso = total == 0 ? 0 : concluidas * 100 / total;
        it.rodape = concluidas + "/" + total + " tarefas  ·  " + membros.size()
                + (membros.size() == 1 ? " membro" : " membros");
        return it;
    }

    private static String primeiroNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            return "";
        }
        return nome.trim().split("\\s+")[0];
    }
}
