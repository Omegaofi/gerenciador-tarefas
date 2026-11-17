package gerenciador.tarefas.cliente.view;

import gerenciador.tarefas.cliente.view.tema.Tema;
import javax.swing.JComboBox;

/** Item de combo: guarda o valor do banco e mostra o texto amigável. */
public record Opcao(String valor, String rotulo) {

    public static Opcao[] deStatus(String... valores) {
        Opcao[] opcoes = new Opcao[valores.length];
        for (int i = 0; i < valores.length; i++) {
            opcoes[i] = new Opcao(valores[i], Tema.rotuloStatus(valores[i]));
        }
        return opcoes;
    }

    public static void selecionarValor(JComboBox<Opcao> combo, String valor) {
        for (int i = 0; i < combo.getItemCount(); i++) {
            if (combo.getItemAt(i).valor().equals(valor)) {
                combo.setSelectedIndex(i);
                return;
            }
        }
    }

    @Override
    public String toString() {
        return rotulo;
    }
}
