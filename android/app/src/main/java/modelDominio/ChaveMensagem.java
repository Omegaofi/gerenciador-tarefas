package modelDominio;

import java.io.Serializable;

/** Cópia da chave K de uma mensagem, cifrada para um membro do projeto (tabela mensagem_chave). */
public class ChaveMensagem implements Serializable {

    private static final long serialVersionUID = 127L;
    private int destinatarioId;
    private String chaveCifrada;   // AES-GCM(chavePar, iv, K) em Base64
    private String iv;             // 12 bytes em Base64, próprio desta cópia

    public ChaveMensagem(int destinatarioId, String chaveCifrada, String iv) {
        this.destinatarioId = destinatarioId;
        this.chaveCifrada = chaveCifrada;
        this.iv = iv;
    }

    public int getDestinatarioId() { return destinatarioId; }
    public void setDestinatarioId(int destinatarioId) { this.destinatarioId = destinatarioId; }
    public String getChaveCifrada() { return chaveCifrada; }
    public void setChaveCifrada(String chaveCifrada) { this.chaveCifrada = chaveCifrada; }
    public String getIv() { return iv; }
    public void setIv(String iv) { this.iv = iv; }

    @Override
    public String toString() {
        return "ChaveMensagem{destinatario=" + destinatarioId + "}";
    }
}
