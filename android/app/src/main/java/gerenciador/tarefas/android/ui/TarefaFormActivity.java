package gerenciador.tarefas.android.ui;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.datepicker.MaterialDatePicker;
import gerenciador.tarefas.android.R;
import gerenciador.tarefas.android.rede.Rede;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import modelDominio.Projeto;
import modelDominio.Tarefa;
import modelDominio.Usuario;

/** FormTarefa1 do desktop: o criador cria ou edita uma tarefa e escolhe o responsável. */
public class TarefaFormActivity extends BaseActivity {

    public static final String EXTRA_TAREFA = "tarefa";

    private Tarefa tarefa;   // null = nova
    private Dropdown<Projeto> projetos;
    private Dropdown<Usuario> responsaveis;
    private Dropdown<String> status;
    private Date prazo;
    private TextView inputPrazo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (semSessao()) {
            return;
        }
        setContentView(R.layout.activity_form_tarefa);
        tarefa = (Tarefa) getIntent().getSerializableExtra(EXTRA_TAREFA);
        configurarToolbar(tarefa == null ? "Nova tarefa" : "Editar tarefa", true);
        ((TextView) findViewById(R.id.txtSubtitulo)).setText("Só quem foi aceito no projeto pode receber a tarefa.");

        projetos = new Dropdown<>(findViewById(R.id.listaProjeto), Projeto::getTitulo);
        responsaveis = new Dropdown<>(findViewById(R.id.listaResponsavel), u -> u.getNome() + "  (" + u.getEmail() + ")");
        status = new Dropdown<>(findViewById(R.id.listaStatus), Status::rotulo);
        status.setItens(Arrays.asList(Status.TAREFA));
        status.selecionarPrimeiro();
        projetos.setAoEscolher(p -> carregarResponsaveis(p, null));

        inputPrazo = findViewById(R.id.inputPrazo);
        inputPrazo.setOnClickListener(v -> escolherPrazo());
        findViewById(R.id.campo_inputPrazo).setOnClickListener(v -> escolherPrazo());
        definirPrazo(new Date());

        if (tarefa != null) {
            ((TextView) findViewById(R.id.inputTitulo)).setText(tarefa.getTitulo());
            if (tarefa.getPrazo() != null) {
                definirPrazo(tarefa.getPrazo());
            }
            if (tarefa.getStatus() != null) {
                status.selecionar(tarefa.getStatus());
            }
        }
        findViewById(R.id.botaoSalvar).setOnClickListener(v -> salvar());
        carregarProjetos();
    }

    private void carregarProjetos() {
        ocupado(true);
        Rede.rodar(this, () -> conexao().projetoListaPorCriador(conexao().getUsuarioLogado().getId()), lista -> {
            ocupado(false);
            projetos.setItens(lista);
            if (lista.isEmpty()) {
                alerta("Crie um projeto antes de cadastrar tarefas.");
                return;
            }
            Projeto inicial = lista.get(0);
            if (tarefa != null && tarefa.getProjeto() != null) {
                for (Projeto p : lista) {
                    if (p.getId() == tarefa.getProjeto().getId()) {
                        inicial = p;
                    }
                }
            }
            projetos.selecionar(inicial);
            carregarResponsaveis(inicial, tarefa != null ? tarefa.getUsuario() : null);
        });
    }

    /** Os responsáveis dependem do projeto: só quem foi aceito nele pode receber tarefa. */
    private void carregarResponsaveis(Projeto projeto, Usuario atual) {
        responsaveis.setItens(new ArrayList<>());
        Rede.rodar(this, () -> conexao().projetoParticipantes(projeto.getId()), lista -> {
            responsaveis.setItens(lista);
            Usuario escolhido = lista.isEmpty() ? null : lista.get(0);
            if (atual != null) {
                for (Usuario u : lista) {
                    if (u.getId() == atual.getId()) {
                        escolhido = u;
                    }
                }
            }
            responsaveis.selecionar(escolhido);
            alerta(lista.isEmpty()
                    ? "Ninguém foi aceito neste projeto ainda. Aprove inscrições no menu Inscrições."
                    : "");
        });
    }

    private void escolherPrazo() {
        // O MaterialDatePicker trabalha em UTC; convertemos de/para a data local
        Calendar local = Calendar.getInstance();
        local.setTime(prazo);
        Calendar utc = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        utc.clear();
        utc.set(local.get(Calendar.YEAR), local.get(Calendar.MONTH), local.get(Calendar.DAY_OF_MONTH));

        MaterialDatePicker<Long> picker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Prazo da tarefa")
                .setSelection(utc.getTimeInMillis())
                .build();
        picker.addOnPositiveButtonClickListener(millis -> {
            Calendar escolhido = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
            escolhido.setTimeInMillis(millis);
            Calendar c = Calendar.getInstance();
            c.clear();
            c.set(escolhido.get(Calendar.YEAR), escolhido.get(Calendar.MONTH), escolhido.get(Calendar.DAY_OF_MONTH));
            definirPrazo(c.getTime());
        });
        picker.show(getSupportFragmentManager(), "prazo");
    }

    private void definirPrazo(Date d) {
        prazo = d;
        inputPrazo.setText(Status.data(d));
    }

    private void salvar() {
        String titulo = texto(findViewById(R.id.inputTitulo));
        Projeto projeto = projetos.getSelecionado();
        Usuario responsavel = responsaveis.getSelecionado();
        if (titulo.isEmpty()) {
            alerta("Informe o título da tarefa.");
            return;
        }
        if (projeto == null) {
            alerta("Selecione um projeto.");
            return;
        }
        if (responsavel == null) {
            alerta("Selecione o colaborador responsável.");
            return;
        }
        String st = status.getSelecionado() != null ? status.getSelecionado() : "pendente";
        esconderTeclado();
        ocupado(true);
        Rede.rodar(this, () -> tarefa != null
                ? conexao().tarefaAtualizar(new Tarefa(tarefa.getId(), titulo, prazo, st, projeto, responsavel, tarefa.getCriadoEm()))
                : conexao().tarefaInserir(new Tarefa(titulo, prazo, st, projeto, responsavel)), ok -> {
            ocupado(false);
            if (ok) {
                Toast.makeText(this, tarefa != null ? "Tarefa atualizada com sucesso!" : "Tarefa criada com sucesso!",
                        Toast.LENGTH_SHORT).show();
                finish();
            } else {
                alerta("Erro ao salvar a tarefa. Tente novamente.");
            }
        });
    }
}
