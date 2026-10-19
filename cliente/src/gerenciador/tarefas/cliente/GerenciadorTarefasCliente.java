package gerenciador.tarefas.cliente;

import gerenciador.tarefas.cliente.controller.ConexaoController;
import gerenciador.tarefas.cliente.view.TelaLogin;
import gerenciador.tarefas.cliente.view.tema.Tema;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class GerenciadorTarefasCliente {

    public static ConexaoController conexaoCliente;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Tema.aplicar();
            try {
                Socket socket = new Socket("localhost", 4321);
                ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
                conexaoCliente = new ConexaoController(in, out);
                TelaLogin telaLogin = new TelaLogin();
                telaLogin.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null,
                        "Não foi possível conectar ao servidor (localhost:4321).\n"
                        + "Inicie o projeto gerenciador-tarefas-servidor e tente novamente.",
                        "NexProject", JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
        });
    }
}
