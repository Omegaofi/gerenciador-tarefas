package gerenciador.tarefas.android.ui;

import android.os.Bundle;
import gerenciador.tarefas.android.R;

/** TelaInscricoes do desktop: o criador aprova ou recusa quem pediu para entrar. */
public class InscricoesActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (semSessao()) {
            return;
        }
        setContentView(R.layout.activity_lista);
        configurarToolbar("Inscrições", true);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.conteudo, new InscricoesFragment())
                    .commit();
        }
    }
}
