package gerenciador.tarefas.android.rede;

import android.content.Context;
import android.content.SharedPreferences;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Guarda a conexão com o servidor (o "conexaoCliente" do desktop) e o endereço do servidor.
 *
 * <p>No emulador do Android Studio, 10.0.2.2 é o "localhost" do computador onde roda o
 * gerenciador-tarefas-servidor. Num celular de verdade, use o IP do computador na rede Wi-Fi.</p>
 */
public final class Sessao {

    public static final String HOST_PADRAO = "10.0.2.2";
    public static final int PORTA_PADRAO = 4321;
    private static final int TEMPO_CONEXAO_MS = 5000;
    private static final String PREFS = "nexproject";

    private static volatile ConexaoController conexao;

    private Sessao() {
    }

    public static ConexaoController conexao() {
        return conexao;
    }

    /** True se há conexão ativa e alguém logado nela. */
    public static boolean logado() {
        ConexaoController c = conexao;
        return c != null && !c.isPerdida() && c.getUsuarioLogado() != null;
    }

    /**
     * Abre um socket novo (fechando o anterior, que pode estar morto se o servidor reiniciou).
     * Usado no login e no cadastro. Chame fora da thread principal.
     */
    public static synchronized ConexaoController novaConexao(Context contexto) throws IOException {
        encerrar();
        Socket socket = new Socket();
        try {
            socket.connect(new InetSocketAddress(host(contexto), porta(contexto)), TEMPO_CONEXAO_MS);
            conexao = new ConexaoController(socket);
        } catch (IOException e) {
            socket.close();
            throw e;
        }
        return conexao;
    }

    /** Lança IOException se o socket caiu no meio de um comando (para não confundir com "senha errada"). */
    public static void exigirConexao() throws IOException {
        if (conexao == null || conexao.isPerdida()) {
            throw new IOException("Conexão com o servidor perdida");
        }
    }

    /** Logout: esquece o usuário, avisa o servidor e fecha o socket. Chame fora da thread principal. */
    public static synchronized void encerrar() {
        if (conexao != null) {
            conexao.setUsuarioLogado(null);
            conexao.fechar();
            conexao = null;
        }
    }

    // ---------- Endereço do servidor ----------

    private static SharedPreferences prefs(Context c) {
        return c.getApplicationContext().getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    public static String host(Context c) {
        return prefs(c).getString("host", HOST_PADRAO);
    }

    public static int porta(Context c) {
        return prefs(c).getInt("porta", PORTA_PADRAO);
    }

    public static void salvarServidor(Context c, String host, int porta) {
        prefs(c).edit().putString("host", host).putInt("porta", porta).apply();
    }

    public static String ultimoEmail(Context c) {
        return prefs(c).getString("email", "");
    }

    public static void salvarEmail(Context c, String email) {
        prefs(c).edit().putString("email", email).apply();
    }
}
