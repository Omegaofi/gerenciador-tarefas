package gerenciador.tarefas.android.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import gerenciador.tarefas.android.R;
import gerenciador.tarefas.android.cripto.CriptografiaUtil;
import gerenciador.tarefas.android.rede.ConexaoController;
import gerenciador.tarefas.android.rede.Rede;
import gerenciador.tarefas.android.rede.Sessao;
import java.net.SocketTimeoutException;
import java.util.Arrays;
import modelDominio.Usuario;
import org.mindrot.jbcrypt.BCrypt;

/** TelaLogin1 do desktop. */
public class LoginActivity extends BaseActivity {

    /** Resultado do login feito em segundo plano. */
    private static final class Resultado {
        Usuario usuario;
        boolean chatFalhou;
        boolean semProjeto;
    }

    private TextInputEditText inputEmail;
    private TextInputEditText inputSenha;
    private TextView linkServidor;

    @Override
    protected boolean exigeLogin() {
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        TextView logo = findViewById(R.id.txtLogo);
        SpannableString marca = new SpannableString("■ NexProject");
        marca.setSpan(new ForegroundColorSpan(getColor(R.color.limao)), 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        logo.setText(marca);

        inputEmail = findViewById(R.id.inputEmail);
        inputSenha = findViewById(R.id.inputSenha);
        linkServidor = findViewById(R.id.linkServidor);

        String email = getIntent().getStringExtra("email");
        inputEmail.setText(email != null ? email : Sessao.ultimoEmail(this));

        findViewById(R.id.botaoEntrar).setOnClickListener(v -> entrar());
        inputSenha.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                entrar();
                return true;
            }
            return false;
        });
        findViewById(R.id.linkCadastro).setOnClickListener(v ->
                startActivity(new Intent(this, CadastroActivity.class)));
        findViewById(R.id.linkEsqueci).setOnClickListener(v -> new MaterialAlertDialogBuilder(this)
                .setTitle("Esqueceu a senha?")
                .setMessage("A recuperação de senha por e-mail estará disponível em breve.\n\n"
                        + "Se você lembra a senha atual, troque-a pelo menu Alterar senha depois de entrar.")
                .setPositiveButton("OK", null)
                .show());
        linkServidor.setOnClickListener(v -> configurarServidor());
        mostrarServidor();
    }

    private void mostrarServidor() {
        linkServidor.setText("Servidor: " + Sessao.host(this) + ":" + Sessao.porta(this) + "  ·  alterar");
    }

    private void entrar() {
        String email = texto(inputEmail);
        char[] senha = senha(inputSenha);
        if (email.isEmpty() || senha.length == 0) {
            alerta("Informe e-mail e senha.");
            return;
        }
        esconderTeclado();
        alerta("");
        ocupado(true);
        Sessao.salvarEmail(this, email);

        Rede.rodar(this, () -> {
            try {
                ConexaoController conexao = Sessao.novaConexao(this);
                Resultado r = new Resultado();

                // O servidor nunca recebe a senha: recebe PBKDF2(senha, e-mail)
                String hashLogin = CriptografiaUtil.hashLogin(senha, email);
                Usuario logado = conexao.usuarioLoginHash(email, hashLogin);

                // Conta criada antes do chat cifrado: entra com a senha e migra para o hash
                if (logado == null) {
                    logado = conexao.usuarioLogin(new Usuario(email, new String(senha)));
                    if (logado != null) {
                        conexao.usuarioMigrarLogin(BCrypt.hashpw(hashLogin, BCrypt.gensalt()));
                    }
                }
                if (logado == null) {
                    Sessao.exigirConexao();
                    return r;
                }
                conexao.setUsuarioLogado(logado);
                try {
                    conexao.getChatCifrado().abrirCofre(logado, senha);
                } catch (Exception e) {
                    e.printStackTrace();
                    r.chatFalhou = true;
                }
                r.semProjeto = !conexao.isCriador() && !conexao.usuarioParticipaDeProjeto();
                r.usuario = logado;
                return r;
            } finally {
                Arrays.fill(senha, '\0');   // a senha não fica na memória
            }
        }, r -> {
            ocupado(false);
            if (r.usuario == null) {
                alerta("E-mail ou senha incorretos.");
                inputSenha.setText("");
                inputSenha.requestFocus();
                return;
            }
            inputSenha.setText("");
            Intent i = new Intent(this, MainActivity.class);
            // Participante sem projeto começa em Explorar projetos (como o FormProcura do desktop)
            i.putExtra(MainActivity.EXTRA_EXPLORAR, r.semProjeto);
            i.putExtra(MainActivity.EXTRA_CHAT_FALHOU, r.chatFalhou);
            startActivity(i);
            finish();
        }, e -> {
            ocupado(false);
            e.printStackTrace();
            if (e instanceof java.security.GeneralSecurityException) {
                alerta("Erro ao proteger a senha. Tente novamente.");
            } else {
                alerta("Não foi possível conectar ao servidor (" + Sessao.host(this) + ":" + Sessao.porta(this)
                        + ").\nInicie o gerenciador-tarefas-servidor e confira o endereço"
                        + (e instanceof SocketTimeoutException ? " (tempo esgotado)." : "."));
            }
        });
    }

    /** Diálogo para trocar o IP/porta do servidor (emulador: 10.0.2.2; celular: IP do PC na rede). */
    private void configurarServidor() {
        LinearLayout caixa = new LinearLayout(this);
        caixa.setOrientation(LinearLayout.VERTICAL);
        int p = (int) (20 * getResources().getDisplayMetrics().density);
        caixa.setPadding(p, p / 2, p, 0);

        EditText host = campoDialogo(caixa, "Endereço (IP)", Sessao.host(this), false);
        EditText porta = campoDialogo(caixa, "Porta", String.valueOf(Sessao.porta(this)), true);

        AlertDialog dialogo = new MaterialAlertDialogBuilder(this)
                .setTitle("Servidor")
                .setMessage("No emulador, use 10.0.2.2 (o computador). No celular, use o IP do computador na mesma rede Wi-Fi.")
                .setView(caixa)
                .setPositiveButton("Salvar", null)
                .setNeutralButton("Padrão", (d, w) -> {
                    Sessao.salvarServidor(this, Sessao.HOST_PADRAO, Sessao.PORTA_PADRAO);
                    trocouServidor();
                })
                .setNegativeButton("Cancelar", null)
                .show();
        dialogo.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String h = host.getText().toString().trim();
            int numero;
            try {
                numero = Integer.parseInt(porta.getText().toString().trim());
            } catch (NumberFormatException ex) {
                numero = -1;
            }
            if (h.isEmpty() || numero < 1 || numero > 65535) {
                porta.setError("Informe um endereço e uma porta válidos");
                return;
            }
            Sessao.salvarServidor(this, h, numero);
            trocouServidor();
            dialogo.dismiss();
        });
    }

    private void trocouServidor() {
        Rede.emSegundoPlano(Sessao::encerrar);   // a próxima tentativa conecta no endereço novo
        mostrarServidor();
    }

    private EditText campoDialogo(LinearLayout caixa, String dica, String valor, boolean numero) {
        TextInputLayout layout = new TextInputLayout(this);
        layout.setHint(dica);
        TextInputEditText campo = new TextInputEditText(layout.getContext());
        campo.setText(valor);
        campo.setInputType(numero ? android.text.InputType.TYPE_CLASS_NUMBER
                : android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_URI);
        layout.addView(campo);
        caixa.addView(layout);
        return campo;
    }
}
