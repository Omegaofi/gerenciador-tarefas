package gerenciador.tarefas.cliente.view;

import gerenciador.tarefas.cliente.GerenciadorTarefasCliente;
import gerenciador.tarefas.cliente.view.tema.Tema;
import java.awt.Frame;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import modelDominio.Arquivo;
import modelDominio.EntregaTarefa;
import modelDominio.Tarefa;
import modelDominio.Usuario;

/** RF006: o colaborador entrega a tarefa informando o link do trabalho (GitHub, Drive...). */
public class FormEntrega extends FormBase {

    private final JTextField jTextFieldDescricao = Tema.campoTexto();
    private final JTextField jTextFieldLink = Tema.campoTexto();
    private final Tarefa tarefa;
    private final Runnable aoEnviar;

    public FormEntrega(Frame dono, Tarefa tarefa, Runnable aoEnviar) {
        super(dono, "Enviar entrega", subtitulo(tarefa), "Enviar entrega");
        this.tarefa = tarefa;
        this.aoEnviar = aoEnviar;

        jTextFieldDescricao.setText("Entrega - " + tarefa.getTitulo());
        adicionarCampo("Descrição da entrega", jTextFieldDescricao);
        adicionarCampo("Link do arquivo ou repositório", jTextFieldLink);
        javax.swing.JLabel dica = Tema.subtitulo("Ex.: https://github.com/usuario/projeto ou link do Google Drive");
        dica.setAlignmentX(LEFT_ALIGNMENT);
        campos.add(dica);
        finalizarLayout();
    }

    private static String subtitulo(Tarefa tarefa) {
        String prazo = tarefa.getPrazo() != null ? new SimpleDateFormat("dd/MM/yyyy").format(tarefa.getPrazo()) : "sem prazo";
        String projeto = tarefa.getProjeto() != null ? tarefa.getProjeto().getTitulo() : "";
        return tarefa.getTitulo() + " · " + projeto + " · prazo " + prazo;
    }

    @Override
    protected void salvar() {
        String descricao = jTextFieldDescricao.getText().trim();
        String link = jTextFieldLink.getText().trim();
        if (descricao.isEmpty()) {
            alerta("Descreva a entrega.");
            jTextFieldDescricao.requestFocus();
            return;
        }
        if (!link.matches("(?i)^https?://\\S+$")) {
            alerta("Informe um link válido, começando com http:// ou https://");
            jTextFieldLink.requestFocus();
            return;
        }

        Usuario logado = GerenciadorTarefasCliente.conexaoCliente.getUsuarioLogado();
        Arquivo arquivo = new Arquivo(descricao, link, logado, tarefa);
        EntregaTarefa entrega = new EntregaTarefa(new Date(), "enviada", tarefa, arquivo);
        if (GerenciadorTarefasCliente.conexaoCliente.entregaTarefaEnviar(entrega)) {
            JOptionPane.showMessageDialog(this, "Entrega enviada! O criador do projeto vai revisar.");
            dispose();
            if (aoEnviar != null) {
                aoEnviar.run();
            }
        } else {
            alerta("Erro ao enviar a entrega. Tente novamente.");
        }
    }
}
