package gerenciador.tarefas.android.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import androidx.fragment.app.Fragment;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import gerenciador.tarefas.android.R;
import gerenciador.tarefas.android.rede.Rede;
import gerenciador.tarefas.android.rede.Sessao;
import modelDominio.Usuario;

/**
 * Tela principal com a barra inferior: faz o papel do MenuLateral do desktop.
 * Criador: Início, Projetos, Tarefas, Entregas, Chat.
 * Participante: Início, Explorar, Minhas tarefas, Entregas, Chat.
 */
public class MainActivity extends BaseActivity {

    public static final String EXTRA_EXPLORAR = "explorar";
    public static final String EXTRA_CHAT_FALHOU = "chatFalhou";

    private BottomNavigationView navegacao;
    private MaterialToolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (semSessao()) {
            return;
        }
        setContentView(R.layout.activity_main);

        Usuario logado = conexao().getUsuarioLogado();
        boolean criador = conexao().isCriador();
        toolbar = configurarToolbar("NexProject", false);
        toolbar.setSubtitle(logado.getNome() + " · " + Status.rotulo(logado.getTipo()));
        toolbar.inflateMenu(R.menu.principal);
        toolbar.getMenu().findItem(R.id.menu_inscricoes).setVisible(criador);
        toolbar.setOnMenuItemClickListener(this::menu);

        navegacao = findViewById(R.id.navegacao);
        Menu m = navegacao.getMenu();
        m.add(Menu.NONE, R.id.nav_inicio, 0, "Início").setIcon(R.drawable.ic_inicio);
        if (criador) {
            m.add(Menu.NONE, R.id.nav_projetos, 1, "Projetos").setIcon(R.drawable.ic_projetos);
            m.add(Menu.NONE, R.id.nav_tarefas, 2, "Tarefas").setIcon(R.drawable.ic_tarefas);
        } else {
            m.add(Menu.NONE, R.id.nav_explorar, 1, "Explorar").setIcon(R.drawable.ic_explorar);
            m.add(Menu.NONE, R.id.nav_tarefas, 2, "Tarefas").setIcon(R.drawable.ic_tarefas);
        }
        m.add(Menu.NONE, R.id.nav_entregas, 3, "Entregas").setIcon(R.drawable.ic_entregas);
        m.add(Menu.NONE, R.id.nav_chat, 4, "Chat").setIcon(R.drawable.ic_chat);
        navegacao.setOnItemSelectedListener(item -> {
            abrir(item.getItemId());
            return true;
        });

        if (savedInstanceState == null) {
            boolean explorar = getIntent().getBooleanExtra(EXTRA_EXPLORAR, false);
            navegacao.setSelectedItemId(explorar ? R.id.nav_explorar : R.id.nav_inicio);
            if (getIntent().getBooleanExtra(EXTRA_CHAT_FALHOU, false)) {
                new MaterialAlertDialogBuilder(this)
                        .setTitle("Chat cifrado")
                        .setMessage("Login feito, mas as chaves do chat não abriram.\nO chat ficará indisponível nesta sessão.")
                        .setPositiveButton("OK", null)
                        .show();
            }
        }
    }

    /** Usado pelo Início para pular para outra aba. */
    public void irPara(int itemId) {
        navegacao.setSelectedItemId(itemId);
    }

    private void abrir(int itemId) {
        Fragment f;
        if (itemId == R.id.nav_projetos) {
            f = new ProjetosFragment();
        } else if (itemId == R.id.nav_explorar) {
            f = new ExplorarFragment();
        } else if (itemId == R.id.nav_tarefas) {
            f = new TarefasFragment();
        } else if (itemId == R.id.nav_entregas) {
            f = new EntregasFragment();
        } else if (itemId == R.id.nav_chat) {
            f = new ConversasFragment();
        } else {
            f = new InicioFragment();
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.conteudo, f).commit();
    }

    private boolean menu(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_inscricoes) {
            startActivity(new Intent(this, InscricoesActivity.class));
        } else if (id == R.id.menu_senha) {
            startActivity(new Intent(this, TrocarSenhaActivity.class));
        } else if (id == R.id.menu_sair) {
            new MaterialAlertDialogBuilder(this)
                    .setTitle("Sair")
                    .setMessage("Deseja sair da sua conta?")
                    .setPositiveButton("Sair", (d, w) -> sair())
                    .setNegativeButton("Cancelar", null)
                    .show();
        }
        return true;
    }

    private void sair() {
        Rede.emSegundoPlano(Sessao::encerrar);   // a chave privada do chat sai da memória
        Intent i = new Intent(this, LoginActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        finish();
    }
}
