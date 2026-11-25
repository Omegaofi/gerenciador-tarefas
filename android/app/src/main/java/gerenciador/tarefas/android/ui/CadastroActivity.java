package gerenciador.tarefas.android.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.button.MaterialButtonToggleGroup;
import gerenciador.tarefas.android.R;
import gerenciador.tarefas.android.cripto.CriptografiaUtil;
import gerenciador.tarefas.android.rede.Rede;
import gerenciador.tarefas.android.rede.Sessao;
import java.util.Arrays;
import modelDominio.Usuario;

/** TelaCadastro1 do desktop. */
public class CadastroActivity extends BaseActivity {

    private static final int TAMANHO_MINIMO = 8;

    private MaterialButtonToggleGroup grupoTipo;
    private TextView txtTipoDica;

    @Override
    protected boolean exigeLogin() {
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);
        configurarToolbar("Cadastro", true);

        grupoTipo = findViewById(R.id.grupoTipo);
        txtTipoDica = findViewById(R.id.txtTipoDica);
        grupoTipo.addOnButtonCheckedListener((g, id, marcado) -> mostrarDica());
        mostrarDica();
        findViewById(R.id.botaoCriar).setOnClickListener(v -> criar());
    }

    private String tipo() {
        return grupoTipo.getCheckedButtonId() == R.id.botaoParticipante ? "participante" : "criador";
    }

    private void mostrarDica() {
        txtTipoDica.setText("criador".equals(tipo())
                ? "Criador: cria projetos, distribui tarefas e revisa entregas."
                : "Participante: entra em projetos, recebe tarefas e envia entregas.");
    }

    private void criar() {
        String nome = texto(findViewById(R.id.inputNome));
        String email = texto(findViewById(R.id.inputEmail));
        char[] senha = senha(findViewById(R.id.inputSenha));
        char[] confirmar = senha(findViewById(R.id.inputConfirmar));
        String tipo = tipo();

        if (nome.isEmpty() || email.isEmpty() || senha.length == 0) {
            alerta("Preencha nome, e-mail e senha.");
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            alerta("Informe um e-mail válido.");
            return;
        }
        // A senha também tranca a chave privada do chat: senha fraca, chave fraca
        if (senha.length < TAMANHO_MINIMO) {
            alerta("A senha precisa ter ao menos " + TAMANHO_MINIMO + " caracteres.");
            return;
        }
        if (!Arrays.equals(senha, confirmar)) {
            alerta("As senhas não conferem.");
            ((TextView) findViewById(R.id.inputConfirmar)).setText("");
            return;
        }
        Arrays.fill(confirmar, '\0');
        esconderTeclado();
        alerta("");
        ocupado(true);

        Rede.rodar(this, () -> {
            try {
                // O servidor nunca recebe a senha: recebe o SHA-256 dela (código de Segurança da Informação) e guarda esse SHA-256
                String shaSenha = CriptografiaUtil.shaSenha(senha);
                Usuario novo = new Usuario(nome, email, shaSenha, null, tipo);
                boolean ok = Sessao.novaConexao(this).usuarioInserir(novo);
                Sessao.exigirConexao();
                return ok;
            } finally {
                Arrays.fill(senha, '\0');
            }
        }, ok -> {
            ocupado(false);
            if (ok) {
                Toast.makeText(this, "Conta criada com sucesso! Faça login para continuar.", Toast.LENGTH_LONG).show();
                Intent i = new Intent(this, LoginActivity.class);
                i.putExtra("email", email);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
                finish();
            } else {
                alerta("Não foi possível criar a conta. Este e-mail já está em uso?");
            }
        }, e -> {
            ocupado(false);
            e.printStackTrace();
            alerta("Não foi possível conectar ao servidor (" + Sessao.host(this) + ":" + Sessao.porta(this) + ").");
        });
    }
}
