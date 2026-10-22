package gerenciador.tarefas.cliente.view;

import gerenciador.tarefas.cliente.view.tema.Tema;
import java.awt.Dimension;
import java.awt.Frame;
import javax.swing.JComboBox;
import javax.swing.JFrame;

/**
 * Parte comum dos formulários. Os componentes ficam no designer de cada JFrame;
 * aqui entram só o ajuste da janela e os itens de combo com valor do banco.
 */
final class PainelFormulario {

    /** Item de combo: guarda o valor do banco e mostra o texto amigável. */
    record Opcao(String valor, String rotulo) {

        static Opcao[] deStatus(String... valores) {
            Opcao[] opcoes = new Opcao[valores.length];
            for (int i = 0; i < valores.length; i++) {
                opcoes[i] = new Opcao(valores[i], Tema.rotuloStatus(valores[i]));
            }
            return opcoes;
        }

        @Override
        public String toString() {
            return rotulo;
        }
    }

    private PainelFormulario() {
    }

    static void selecionarValor(JComboBox<Object> combo, String valor) {
        for (int i = 0; i < combo.getItemCount(); i++) {
            if (((Opcao) combo.getItemAt(i)).valor().equals(valor)) {
                combo.setSelectedIndex(i);
                return;
            }
        }
    }

    /** Estilo do combo e dos itens com valor do banco. */
    static void usarOpcoes(JComboBox<Object> combo, Opcao[] opcoes) {
        combo.setModel(new javax.swing.DefaultComboBoxModel<>(opcoes));
        Tema.estilizarCombo(combo);
    }

    /** Título da janela, fechamento só da própria janela e largura mínima de 460px, centralizada na tela de origem. */
    static void finalizar(JFrame janela, String titulo, Frame dono) {
        janela.setTitle(titulo);
        janela.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        janela.pack();
        janela.setMinimumSize(new Dimension(460, 0));
        janela.setSize(Math.max(460, janela.getWidth()), janela.getHeight());
        janela.setLocationRelativeTo(dono);
    }
}
