package gerenciador.tarefas.cliente.cripto;

import java.util.Date;

/** Mensagem do chat já decifrada (ou o motivo de não ter aberto), pronta para a tela. */
public record MensagemAberta(int id, int autorId, String autorNome, Date dataEnvio, String texto, Estado estado) {

    public enum Estado {
        /** Decifrada e conferida pela tag do GCM. */
        CIFRADA_OK,
        /** Não há cópia da chave para o usuário: ele entrou no projeto depois. */
        ANTES_DE_ENTRAR,
        /** A tag não conferiu: conteúdo, projeto ou remetente foram alterados no banco. */
        CORROMPIDA
    }
}
