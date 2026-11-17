package gerenciador.tarefas.android.ui;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import gerenciador.tarefas.android.R;
import gerenciador.tarefas.android.rede.Rede;
import java.util.Date;
import modelDominio.Arquivo;
import modelDominio.EntregaTarefa;
import modelDominio.Tarefa;
import modelDominio.Usuario;

/** FormEntrega1 do desktop: o participante entrega a tarefa com uma descrição e um link. */
public class EntregaFormActivity extends BaseActivity {

    public static final String EXTRA_TAREFA = "tarefa";

    private Tarefa tarefa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (semSessao()) {
            return;
        }
        setContentView(R.layout.activity_form_entrega);
        tarefa = (Tarefa) getIntent().getSerializableExtra(EXTRA_TAREFA);
        configurarToolbar("Entregar tarefa", true);
        String projeto = tarefa.getProjeto() != null ? tarefa.getProjeto().getTitulo() + " · " : "";
        ((TextView) findViewById(R.id.txtSubtitulo)).setText(
                projeto + tarefa.getTitulo() + "\nPrazo: " + Status.data(tarefa.getPrazo()));
        findViewById(R.id.botaoSalvar).setOnClickListener(v -> enviar());
    }

    private void enviar() {
        String descricao = texto(findViewById(R.id.inputDescricao));
        String link = texto(findViewById(R.id.inputLink));
        if (descricao.isEmpty()) {
            alerta("Descreva a entrega.");
            return;
        }
        if (!link.matches("(?i)^https?://\\S+$")) {
            alerta("Informe um link válido, começando com http:// ou https://");
            return;
        }
        Usuario logado = conexao().getUsuarioLogado();
        Arquivo arquivo = new Arquivo(descricao, link, logado, tarefa);
        EntregaTarefa entrega = new EntregaTarefa(new Date(), "enviada", tarefa, arquivo);
        esconderTeclado();
        ocupado(true);
        Rede.rodar(this, () -> conexao().entregaTarefaEnviar(entrega), ok -> {
            ocupado(false);
            if (ok) {
                Toast.makeText(this, "Entrega enviada! O criador do projeto vai revisar.", Toast.LENGTH_LONG).show();
                finish();
            } else {
                alerta("Erro ao enviar a entrega. Tente novamente.");
            }
        });
    }
}
