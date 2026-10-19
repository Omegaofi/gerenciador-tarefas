package gerenciador.tarefas.cliente.view;

import gerenciador.tarefas.cliente.GerenciadorTarefasCliente;
import gerenciador.tarefas.cliente.controller.ConexaoController;
import gerenciador.tarefas.cliente.view.tema.Tema;
import java.awt.Component;
import java.awt.Frame;
import java.util.ArrayList;
import java.util.Date;
import java.util.function.Function;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerDateModel;
import modelDominio.Projeto;
import modelDominio.Tarefa;
import modelDominio.Usuario;

/** RF004: o idealizador cria tarefas, define o prazo e escolhe o colaborador responsável. */
public class FormTarefa extends FormBase {

    private final ConexaoController conexao = GerenciadorTarefasCliente.conexaoCliente;
    private final JTextField jTextFieldTitulo = Tema.campoTexto();
    private final JComboBox<Projeto> jComboBoxProjeto = new JComboBox<>();
    private final JComboBox<Usuario> jComboBoxResponsavel = new JComboBox<>();
    private final JSpinner jSpinnerPrazo = new JSpinner(new SpinnerDateModel());
    private final JComboBox<Opcao> jComboBoxStatus = new JComboBox<>(
            Opcao.deStatus("pendente", "em_andamento", "concluida"));
    private final Tarefa tarefa;
    private final Runnable aoSalvar;

    /**
     * @param tarefa tarefa a editar, ou null para criar uma nova
     * @param aoSalvar executado depois de salvar com sucesso (ex.: atualizar a tabela)
     */
    public FormTarefa(Frame dono, Tarefa tarefa, Runnable aoSalvar) {
        super(dono, tarefa == null ? "Nova tarefa" : "Editar tarefa",
                "Defina o prazo e o colaborador responsável",
                tarefa == null ? "Criar tarefa" : "Salvar");
        this.tarefa = tarefa;
        this.aoSalvar = aoSalvar;

        carregarCombos();
        jSpinnerPrazo.setEditor(new JSpinner.DateEditor(jSpinnerPrazo, "dd/MM/yyyy"));
        jSpinnerPrazo.setBorder(javax.swing.BorderFactory.createLineBorder(Tema.BORDA));
        Tema.estilizarCombo(jComboBoxProjeto);
        Tema.estilizarCombo(jComboBoxResponsavel);
        Tema.estilizarCombo(jComboBoxStatus);

        adicionarCampo("Título", jTextFieldTitulo);
        adicionarCampo("Projeto", jComboBoxProjeto);
        adicionarCampo("Colaborador responsável", jComboBoxResponsavel);
        adicionarCampo("Prazo de entrega", jSpinnerPrazo);
        adicionarCampo("Status", jComboBoxStatus);

        if (tarefa != null) {
            preencher(tarefa);
        }
        finalizarLayout();
    }

    private void carregarCombos() {
        Usuario logado = conexao.getUsuarioLogado();
        for (Projeto projeto : conexao.projetoListaPorCriador(logado.getId())) {
            jComboBoxProjeto.addItem(projeto);
        }
        jComboBoxProjeto.setRenderer(renderer(Projeto::getTitulo));

        ArrayList<Usuario> usuarios = conexao.usuarioLista();
        for (Usuario usuario : usuarios) {
            if ("participante".equals(usuario.getTipo())) {
                jComboBoxResponsavel.addItem(usuario);
            }
        }
        jComboBoxResponsavel.setRenderer(FormTarefa.<Usuario>renderer(u -> u.getNome() + "  (" + u.getEmail() + ")"));

        if (jComboBoxProjeto.getItemCount() == 0) {
            alerta("Crie um projeto antes de cadastrar tarefas.");
        } else if (jComboBoxResponsavel.getItemCount() == 0) {
            alerta("Nenhum participante cadastrado para receber tarefas.");
        }
    }

    private void preencher(Tarefa t) {
        jTextFieldTitulo.setText(t.getTitulo());
        if (t.getPrazo() != null) {
            jSpinnerPrazo.setValue(t.getPrazo());
        }
        selecionarValor(jComboBoxStatus, t.getStatus());
        if (t.getProjeto() != null) {
            for (int i = 0; i < jComboBoxProjeto.getItemCount(); i++) {
                if (jComboBoxProjeto.getItemAt(i).getId() == t.getProjeto().getId()) {
                    jComboBoxProjeto.setSelectedIndex(i);
                    break;
                }
            }
        }
        if (t.getUsuario() != null) {
            for (int i = 0; i < jComboBoxResponsavel.getItemCount(); i++) {
                if (jComboBoxResponsavel.getItemAt(i).getId() == t.getUsuario().getId()) {
                    jComboBoxResponsavel.setSelectedIndex(i);
                    break;
                }
            }
        }
    }

    private static <T> DefaultListCellRenderer renderer(Function<T, String> texto) {
        return new DefaultListCellRenderer() {
            @Override
            @SuppressWarnings("unchecked")
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setText(value != null ? texto.apply((T) value) : "");
                return this;
            }
        };
    }

    @Override
    protected void salvar() {
        String titulo = jTextFieldTitulo.getText().trim();
        Projeto projeto = (Projeto) jComboBoxProjeto.getSelectedItem();
        Usuario responsavel = (Usuario) jComboBoxResponsavel.getSelectedItem();
        if (titulo.isEmpty()) {
            alerta("Informe o título da tarefa.");
            jTextFieldTitulo.requestFocus();
            return;
        }
        if (projeto == null) {
            alerta("Selecione um projeto.");
            return;
        }
        if (responsavel == null) {
            alerta("Selecione o colaborador responsável.");
            return;
        }
        Date prazo = (Date) jSpinnerPrazo.getValue();
        String status = ((Opcao) jComboBoxStatus.getSelectedItem()).valor();

        boolean ok;
        if (tarefa != null) {
            ok = conexao.tarefaAtualizar(new Tarefa(tarefa.getId(), titulo, prazo, status, projeto,
                    responsavel, tarefa.getCriadoEm()));
        } else {
            ok = conexao.tarefaInserir(new Tarefa(titulo, prazo, status, projeto, responsavel));
        }

        if (ok) {
            JOptionPane.showMessageDialog(this, tarefa != null ? "Tarefa atualizada com sucesso!" : "Tarefa criada com sucesso!");
            dispose();
            if (aoSalvar != null) {
                aoSalvar.run();
            }
        } else {
            alerta("Erro ao salvar a tarefa. Tente novamente.");
        }
    }
}
