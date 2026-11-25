package gerenciador.tarefas.android.rede;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import gerenciador.tarefas.android.ui.LoginActivity;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Roda chamadas ao servidor fora da thread principal (o Android proíbe rede nela)
 * e entrega o resultado de volta na tela.
 *
 * <p>Uma única thread: o protocolo é pergunta-resposta em um só socket,
 * então dois comandos ao mesmo tempo embaralhariam as respostas.</p>
 */
public final class Rede {

    public interface Trabalho<T> {
        T executar() throws Exception;
    }

    public interface Sucesso<T> {
        void ok(T resultado);
    }

    public interface Falha {
        void erro(Exception e);
    }

    private static final ExecutorService FILA = Executors.newSingleThreadExecutor();
    private static final Handler PRINCIPAL = new Handler(Looper.getMainLooper());

    private Rede() {
    }

    public static <T> void rodar(LifecycleOwner dono, Trabalho<T> trabalho, Sucesso<T> sucesso) {
        rodar(dono, trabalho, sucesso, null);
    }

    public static <T> void rodar(LifecycleOwner dono, Trabalho<T> trabalho, Sucesso<T> sucesso, Falha falha) {
        FILA.execute(() -> {
            T resultado = null;
            Exception erro = null;
            try {
                resultado = trabalho.executar();
            } catch (Exception e) {
                erro = e;
            }
            ConexaoController c = Sessao.conexao();
            boolean caiu = c != null && c.isPerdida() && c.getUsuarioLogado() != null;
            final T r = resultado;
            final Exception e = erro;
            PRINCIPAL.post(() -> {
                if (dono.getLifecycle().getCurrentState() == Lifecycle.State.DESTROYED) {
                    return;   // a tela já fechou
                }
                if (caiu) {
                    conexaoPerdida(dono);
                    return;
                }
                if (e != null) {
                    if (falha != null) {
                        falha.erro(e);
                    } else {
                        Activity a = activity(dono);
                        if (a != null) {
                            Toast.makeText(a, "Erro: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                } else if (sucesso != null) {
                    sucesso.ok(r);
                }
            });
        });
    }

    /** Roda algo na fila sem esperar resposta (ex.: logout). */
    public static void emSegundoPlano(Runnable r) {
        FILA.execute(r);
    }

    private static Activity activity(LifecycleOwner dono) {
        if (dono instanceof Activity) {
            return (Activity) dono;
        }
        if (dono instanceof Fragment) {
            return ((Fragment) dono).getActivity();
        }
        return null;
    }

    private static void conexaoPerdida(LifecycleOwner dono) {
        emSegundoPlano(Sessao::encerrar);
        Activity a = activity(dono);
        if (a == null) {
            return;
        }
        Toast.makeText(a, "A conexão com o servidor caiu. Entre novamente.", Toast.LENGTH_LONG).show();
        Intent i = new Intent(a, LoginActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        a.startActivity(i);
    }
}
