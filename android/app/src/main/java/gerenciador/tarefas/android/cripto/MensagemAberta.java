package gerenciador.tarefas.android.cripto;

import java.util.Date;

/** Mensagem do chat já decifrada (ou o motivo de não ter aberto), pronta para a tela. */
public final class MensagemAberta {

    public enum Estado {
        /** Decifrada e conferida pela tag do GCM. */
        CIFRADA_OK,
        /** Não há cópia da chave para o usuário: ele entrou no projeto depois. */
        ANTES_DE_ENTRAR,
        /** A tag não conferiu: conteúdo, projeto ou remetente foram alterados no banco. */
        CORROMPIDA
    }

    private final int id;
    private final int autorId;
    private final String autorNome;
    private final Date dataEnvio;
    private final String texto;
    private final Estado estado;

    public MensagemAberta(int id, int autorId, String autorNome, Date dataEnvio, String texto, Estado estado) {
        this.id = id;
        this.autorId = autorId;
        this.autorNome = autorNome;
        this.dataEnvio = dataEnvio;
        this.texto = texto;
        this.estado = estado;
    }

    public int id() { return id; }
    public int autorId() { return autorId; }
    public String autorNome() { return autorNome; }
    public Date dataEnvio() { return dataEnvio; }
    public String texto() { return texto; }
    public Estado estado() { return estado; }
}
