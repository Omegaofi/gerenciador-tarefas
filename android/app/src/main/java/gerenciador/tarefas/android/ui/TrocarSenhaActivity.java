package gerenciador.tarefas.android.ui;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import gerenciador.tarefas.android.R;
import gerenciador.tarefas.android.cripto.ChatCifrado;
import gerenciador.tarefas.android.cripto.CriptografiaUtil;
import gerenciador.tarefas.android.rede.ConexaoController;
import gerenciador.tarefas.android.rede.Rede;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import modelDominio.Usuario;

/** TelaTrocarSenha do desktop: troca a senha e tranca de novo a chave privada do chat. */
public class TrocarSenhaActivity extends BaseActivity {

    private static final int TAMANHO_MINIMO = 8;

    /** Respostas possíveis da troca feita em segundo plano. */
    private enum Resultado { OK, SENHA_ATUAL_ERRADA }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (semSessao()) {
            return;
        }
        setContentView(R.layout.activity_trocar_senha);
        configurarToolbar("Alterar senha", true);
        ((TextView) findViewById(R.id.txtSubtitulo)).setText(
                "A senha também protege a chave do chat cifrado. Suas mensagens antigas continuam abrindo.");
        String digital = conexao().getChatCifrado().impressaoDigital();
        ((TextView) findViewById(R.id.txtDigital)).setText(digital.isEmpty() ? ""
                : "Impressão digital da sua chave: " + digital + "\nOs membros podem conferir este código pessoalmente.");
        findViewById(R.id.botaoSalvar).setOnClickListener(v -> salvar());
    }

    private void salvar() {
        TextView campoAtual = findViewById(R.id.inputAtual);
        TextView campoNova = findViewById(R.id.inputNova);
        TextView campoConfirmar = findViewById(R.id.inputConfirmar);
        char[] atual = senha(campoAtual);
        char[] nova = senha(campoNova);
        char[] confirmar = senha(campoConfirmar);

        String erro = null;
        if (atual.length == 0 || nova.length == 0 || confirmar.length == 0) {
            erro = "Preencha os três campos.";
        } else if (nova.length < TAMANHO_MINIMO) {
            erro = "A nova senha precisa ter ao menos " + TAMANHO_MINIMO + " caracteres.";
        } else if (!Arrays.equals(nova, confirmar)) {
            erro = "A confirmação não confere com a nova senha.";
        } else if (Arrays.equals(nova, atual)) {
            erro = "A nova senha precisa ser diferente da atual.";
        }
        Arrays.fill(confirmar, '\0');
        if (erro != null) {
            Arrays.fill(atual, '\0');
            Arrays.fill(nova, '\0');
            alerta(erro);
            return;
        }
        esconderTeclado();
        alerta("");
        ocupado(true);

        Rede.rodar(this, () -> {
            try {
                ConexaoController conexao = conexao();
                Usuario logado = conexao.getUsuarioLogado();
                ChatCifrado chat = conexao.getChatCifrado();

                // Se o cofre do chat não abriu no login, abre agora com a senha atual
                // (isso também confere a senha atual antes de mandar qualquer coisa ao servidor)
                if (!chat.isCofreAberto()) {
                    try {
                        chat.abrirCofre(logado, atual);
                    } catch (GeneralSecurityException e) {
                        return Resultado.SENHA_ATUAL_ERRADA;
                    }
                }
                String shaAtual = CriptografiaUtil.shaSenha(atual);
                String shaNovo = CriptografiaUtil.shaSenha(nova);
                Usuario novosDados = chat.trancarCofreComNovaSenha(nova);
                novosDados.setSenha(shaNovo);
                if (!conexao.usuarioTrocarSenha(shaAtual, novosDados)) {
                    return Resultado.SENHA_ATUAL_ERRADA;
                }
                chat.confirmarNovoCofre(novosDados);
                return Resultado.OK;
            } finally {
                Arrays.fill(atual, '\0');
                Arrays.fill(nova, '\0');
            }
        }, r -> {
            ocupado(false);
            if (r == Resultado.OK) {
                Toast.makeText(this, "Senha alterada com sucesso. Use a nova senha no próximo login.",
                        Toast.LENGTH_LONG).show();
                finish();
            } else {
                alerta("Senha atual incorreta.");
                campoAtual.setText("");
                campoAtual.requestFocus();
            }
        }, e -> {
            ocupado(false);
            e.printStackTrace();
            alerta("Não foi possível trocar a senha. Tente novamente.");
        });
    }
}
