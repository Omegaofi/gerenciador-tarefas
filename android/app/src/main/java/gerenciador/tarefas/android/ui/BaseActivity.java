package gerenciador.tarefas.android.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.activity.SystemBarStyle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.google.android.material.appbar.MaterialToolbar;
import gerenciador.tarefas.android.R;
import gerenciador.tarefas.android.rede.ConexaoController;
import gerenciador.tarefas.android.rede.Sessao;

/** Base das telas: barra superior e volta ao login se o app perdeu a sessão. */
public abstract class BaseActivity extends AppCompatActivity {

    /** Login e cadastro não exigem usuário logado. */
    protected boolean exigeLogin() {
        return true;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        // Android 15+ desenha sempre de ponta a ponta: barra de status com ícones claros (topo preto)
        EdgeToEdge.enable(this, SystemBarStyle.dark(Color.TRANSPARENT),
                SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT));
        super.onCreate(savedInstanceState);
        if (exigeLogin() && !Sessao.logado()) {
            // O Android pode ter fechado o app em segundo plano: o socket se perdeu
            Intent i = new Intent(this, LoginActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
            finish();
        }
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        aplicarRecuos();
    }

    /**
     * Topo preto (barra ou faixa da marca) cresce por baixo da barra de status;
     * a tela inteira sobe acima da barra de navegação e do teclado.
     */
    private void aplicarRecuos() {
        View raiz = ((ViewGroup) findViewById(android.R.id.content)).getChildAt(0);
        View topo = findViewById(R.id.barraTopo);
        int[] base = {raiz.getPaddingBottom(), topo != null ? topo.getPaddingTop() : 0};
        ViewCompat.setOnApplyWindowInsetsListener(raiz, (v, insets) -> {
            Insets barras = insets.getInsets(WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.displayCutout());
            Insets teclado = insets.getInsets(WindowInsetsCompat.Type.ime());
            v.setPadding(barras.left, v.getPaddingTop(), barras.right, base[0] + Math.max(barras.bottom, teclado.bottom));
            if (topo != null) {
                topo.setPadding(topo.getPaddingLeft(), base[1] + barras.top, topo.getPaddingRight(), topo.getPaddingBottom());
            } else {
                v.setPadding(v.getPaddingLeft(), barras.top, v.getPaddingRight(), v.getPaddingBottom());
            }
            return WindowInsetsCompat.CONSUMED;
        });
    }

    /** True se onCreate já mandou para o login (a subclasse deve parar). */
    protected boolean semSessao() {
        return exigeLogin() && !Sessao.logado();
    }

    protected ConexaoController conexao() {
        return Sessao.conexao();
    }

    protected MaterialToolbar configurarToolbar(String titulo, boolean voltar) {
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(titulo);
        if (voltar) {
            toolbar.setNavigationIcon(R.drawable.ic_voltar);
            toolbar.setNavigationContentDescription("Voltar");
            toolbar.setNavigationOnClickListener(v -> finish());
        }
        return toolbar;
    }

    protected static String texto(TextView campo) {
        return campo.getText() == null ? "" : campo.getText().toString().trim();
    }

    /** Copia a senha do campo para um char[] (que depois é zerado, como no desktop). */
    protected static char[] senha(TextView campo) {
        CharSequence cs = campo.getText();
        char[] c = new char[cs == null ? 0 : cs.length()];
        for (int i = 0; i < c.length; i++) {
            c[i] = cs.charAt(i);
        }
        return c;
    }

    protected void esconderTeclado() {
        View v = getCurrentFocus();
        if (v != null) {
            InputMethodManager imm = getSystemService(InputMethodManager.class);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }

    /** Liga/desliga o botão e a barrinha de progresso enquanto fala com o servidor. */
    protected void ocupado(boolean sim) {
        View botao = findViewById(R.id.botaoSalvar);
        if (botao == null) {
            botao = findViewById(R.id.botaoEntrar);
        }
        if (botao == null) {
            botao = findViewById(R.id.botaoCriar);
        }
        if (botao != null) {
            botao.setEnabled(!sim);
        }
        View progresso = findViewById(R.id.progresso);
        if (progresso != null) {
            progresso.setVisibility(sim ? View.VISIBLE : View.INVISIBLE);
        }
    }

    protected void alerta(String mensagem) {
        TextView alerta = findViewById(R.id.txtAlerta);
        if (alerta != null) {
            alerta.setText(mensagem);
        }
    }
}
