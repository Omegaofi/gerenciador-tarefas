package gerenciador.tarefas.cliente.view;

import gerenciador.tarefas.cliente.GerenciadorTarefasCliente;
import gerenciador.tarefas.cliente.view.tema.Tema;
import java.awt.Frame;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import modelDominio.Projeto;
import modelDominio.Usuario;

/** RF003: o idealizador cria/edita um projeto com título, descrição e área de atuação. */
public class FormProjeto extends FormBase {

    private static final String[] AREAS = {"Desenvolvimento Web", "Desenvolvimento Mobile",
        "Desenvolvimento Desktop", "Jogos", "Ciência de Dados", "Inteligência Artificial",
        "Design / UX", "Infraestrutura e Redes", "Outra"};

    private final JTextField jTextFieldTitulo = Tema.campoTexto();
    private final JTextArea jTextAreaDescricao = Tema.areaTexto(4);
    private final JComboBox<String> jComboBoxArea = new JComboBox<>(AREAS);
    private final JComboBox<Opcao> jComboBoxStatus = new JComboBox<>(
            Opcao.deStatus("planejado", "em_andamento", "concluido", "cancelado"));
    private final Projeto projeto;
    private final Runnable aoSalvar;

    /**
     * @param projeto projeto a editar, ou null para criar um novo
     * @param aoSalvar executado depois de salvar com sucesso (ex.: atualizar a tabela)
     */
    public FormProjeto(Frame dono, Projeto projeto, Runnable aoSalvar) {
        super(dono, projeto == null ? "Novo projeto" : "Editar projeto",
                "Responsável: " + GerenciadorTarefasCliente.conexaoCliente.getUsuarioLogado().getNome(),
                projeto == null ? "Criar projeto" : "Salvar");
        this.projeto = projeto;
        this.aoSalvar = aoSalvar;

        jComboBoxArea.setEditable(true);
        Tema.estilizarCombo(jComboBoxArea);
        Tema.estilizarCombo(jComboBoxStatus);
        JScrollPane rolagemDescricao = new JScrollPane(jTextAreaDescricao);
        rolagemDescricao.setBorder(null);

        adicionarCampo("Título", jTextFieldTitulo);
        adicionarCampo("Descrição", rolagemDescricao);
        adicionarCampo("Área de atuação", jComboBoxArea);
        adicionarCampo("Status", jComboBoxStatus);

        if (projeto != null) {
            jTextFieldTitulo.setText(projeto.getTitulo());
            jTextAreaDescricao.setText(projeto.getDescricao());
            jComboBoxArea.setSelectedItem(projeto.getAreaAtuacao());
            selecionarValor(jComboBoxStatus, projeto.getStatus());
        } else {
            jComboBoxArea.setSelectedIndex(0);
        }
        finalizarLayout();
    }

    @Override
    protected void salvar() {
        String titulo = jTextFieldTitulo.getText().trim();
        if (titulo.isEmpty()) {
            alerta("Informe o título do projeto.");
            jTextFieldTitulo.requestFocus();
            return;
        }
        Object areaSelecionada = jComboBoxArea.getSelectedItem();
        String area = areaSelecionada != null ? areaSelecionada.toString().trim() : "";
        String status = ((Opcao) jComboBoxStatus.getSelectedItem()).valor();
        Usuario logado = GerenciadorTarefasCliente.conexaoCliente.getUsuarioLogado();

        boolean ok;
        if (projeto != null) {
            Projeto projetoEditar = new Projeto(projeto.getId(), titulo, jTextAreaDescricao.getText().trim(),
                    area, status, logado, projeto.getCriadoEm());
            ok = GerenciadorTarefasCliente.conexaoCliente.projetoAtualizar(projetoEditar);
        } else {
            Projeto projetoInserir = new Projeto(titulo, jTextAreaDescricao.getText().trim(), area, status, logado);
            ok = GerenciadorTarefasCliente.conexaoCliente.projetoInserir(projetoInserir);
        }

        if (ok) {
            JOptionPane.showMessageDialog(this, projeto != null ? "Projeto atualizado com sucesso!" : "Projeto criado com sucesso!");
            dispose();
            if (aoSalvar != null) {
                aoSalvar.run();
            }
        } else {
            alerta("Erro ao salvar o projeto. Tente novamente.");
        }
    }
}
