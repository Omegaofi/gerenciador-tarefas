package gerenciador.tarefas.android.ui;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import gerenciador.tarefas.android.R;
import gerenciador.tarefas.android.rede.Rede;
import java.util.Arrays;
import modelDominio.Projeto;
import modelDominio.Usuario;

/** FormProjeto1 do desktop: cria ou edita um projeto. */
public class ProjetoFormActivity extends BaseActivity {

    public static final String EXTRA_PROJETO = "projeto";

    private Projeto projeto;   // null = novo
    private Dropdown<String> status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (semSessao()) {
            return;
        }
        setContentView(R.layout.activity_form_projeto);
        projeto = (Projeto) getIntent().getSerializableExtra(EXTRA_PROJETO);
        configurarToolbar(projeto == null ? "Novo projeto" : "Editar projeto", true);
        ((TextView) findViewById(R.id.txtSubtitulo)).setText("Responsável: " + conexao().getUsuarioLogado().getNome());

        status = new Dropdown<>(findViewById(R.id.listaStatus), Status::rotulo);
        status.setItens(Arrays.asList(Status.PROJETO));
        status.selecionarPrimeiro();

        if (projeto != null) {
            ((TextView) findViewById(R.id.inputTitulo)).setText(projeto.getTitulo());
            ((TextView) findViewById(R.id.inputDescricao)).setText(projeto.getDescricao());
            ((TextView) findViewById(R.id.inputArea)).setText(projeto.getAreaAtuacao());
            if (projeto.getStatus() != null) {
                status.selecionar(projeto.getStatus());
            }
        }
        findViewById(R.id.botaoSalvar).setOnClickListener(v -> salvar());
    }

    private void salvar() {
        String titulo = texto(findViewById(R.id.inputTitulo));
        String descricao = texto(findViewById(R.id.inputDescricao));
        String area = texto(findViewById(R.id.inputArea));
        if (titulo.isEmpty()) {
            alerta("Informe o título do projeto.");
            return;
        }
        String st = status.getSelecionado() != null ? status.getSelecionado() : "planejado";
        Usuario logado = conexao().getUsuarioLogado();
        esconderTeclado();
        ocupado(true);
        Rede.rodar(this, () -> {
            if (projeto != null) {
                return conexao().projetoAtualizar(new Projeto(projeto.getId(), titulo, descricao, area, st,
                        logado, projeto.getCriadoEm()));
            }
            return conexao().projetoInserir(new Projeto(titulo, descricao, area, st, logado));
        }, ok -> {
            ocupado(false);
            if (ok) {
                Toast.makeText(this, projeto != null ? "Projeto atualizado com sucesso!" : "Projeto criado com sucesso!",
                        Toast.LENGTH_SHORT).show();
                finish();
            } else {
                alerta("Erro ao salvar o projeto. Tente novamente.");
            }
        });
    }
}
