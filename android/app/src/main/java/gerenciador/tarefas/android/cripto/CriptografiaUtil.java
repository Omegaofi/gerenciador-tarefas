package gerenciador.tarefas.android.cripto;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Base64;
import javax.crypto.AEADBadTagException;
import javax.crypto.Cipher;
import javax.crypto.KeyAgreement;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Primitivas do chat cifrado, só com as classes padrão do JDK (java.security / javax.crypto).
 *
 * <ul>
 * <li>Par de chaves: ECDH na curva P-256 (secp256r1), compatível com Android.</li>
 * <li>Cifra: AES-256-GCM, IV de 12 bytes, tag de 128 bits, com AAD.</li>
 * <li>Chave do par remetente → destinatário: SHA-256(segredo ECDH + "NexProject/chat/v1|rem|dest").</li>
 * <li>Senha → chave: PBKDF2-HMAC-SHA256, 120.000 iterações.</li>
 * </ul>
 *
 * O app Android deve usar exatamente as mesmas constantes para abrir as mesmas mensagens.
 */
public final class CriptografiaUtil {

    public static final String VERSAO = "v1";
    public static final int ITERACOES_PBKDF2 = 120_000;
    public static final int TAMANHO_SALT = 16;
    public static final int TAMANHO_IV = 12;
    public static final int TAMANHO_CHAVE_AES = 32;
    private static final int TAG_BITS = 128;
    private static final String CURVA = "secp256r1";
    private static final String PREFIXO_PAR = "NexProject/chat/v1|";

    private static final SecureRandom ALEATORIO = new SecureRandom();

    private CriptografiaUtil() {
    }

    // ---------- Aleatoriedade e Base64 ----------

    public static byte[] aleatorio(int tamanho) {
        byte[] bytes = new byte[tamanho];
        ALEATORIO.nextBytes(bytes);
        return bytes;
    }

    public static String b64(byte[] bytes) {
        return Base64.getEncoder().encodeToString(bytes);
    }

    public static byte[] deB64(String texto) {
        return Base64.getDecoder().decode(texto);
    }

    /** Junta as partes do AAD com "|", ex.: aad("v1", 7, 1) = "v1|7|1". */
    public static String aad(Object... partes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < partes.length; i++) {
            if (i > 0) {
                sb.append('|');
            }
            sb.append(partes[i]);
        }
        return sb.toString();
    }

    // ---------- Chaves do usuário ----------

    /** Gera o par de chaves EC P-256 do usuário. */
    public static KeyPair gerarParDeChaves() throws GeneralSecurityException {
        KeyPairGenerator gerador = KeyPairGenerator.getInstance("EC");
        gerador.initialize(new ECGenParameterSpec(CURVA), ALEATORIO);
        return gerador.generateKeyPair();
    }

    /** Lê a chave pública guardada em usuario.chave_publica (X.509 em Base64). */
    public static PublicKey lerPublica(String base64) throws GeneralSecurityException {
        return KeyFactory.getInstance("EC").generatePublic(new X509EncodedKeySpec(deB64(base64)));
    }

    /** Lê a chave privada depois de destrancada (PKCS#8). */
    public static PrivateKey lerPrivada(byte[] pkcs8) throws GeneralSecurityException {
        return KeyFactory.getInstance("EC").generatePrivate(new PKCS8EncodedKeySpec(pkcs8));
    }

    // ---------- Senha ----------

    /** PBKDF2-HMAC-SHA256 com 120.000 iterações, devolvendo 32 bytes (chave AES-256). */
    public static byte[] derivarChave(char[] senha, byte[] salt) throws GeneralSecurityException {
        PBEKeySpec spec = new PBEKeySpec(senha, salt, ITERACOES_PBKDF2, TAMANHO_CHAVE_AES * 8);
        try {
            return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded();
        } finally {
            spec.clearPassword();
        }
    }

    /**
     * Valor enviado ao servidor no lugar da senha (cadastro, login e troca de senha):
     * SHA-256 da senha em hexadecimal, o código da disciplina de Segurança da Informação,
     * igual ao calculado na TelaLogin1 e na TelaCadastro1 do desktop.
     * O servidor guarda esse SHA-256 no banco.
     */
    public static String shaSenha(char[] senha) throws GeneralSecurityException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] bytes = new String(senha).getBytes(StandardCharsets.UTF_8);
        byte[] digest = md.digest(bytes);
        Arrays.fill(bytes, (byte) 0);
        StringBuilder sb = new StringBuilder();
        for (byte b : digest) {
            sb.append(String.format("%02x", 0xFF & b));
        }
        String senhaCriptografada = sb.toString();
        return senhaCriptografada;
    }

    public static String normalizarEmail(String email) {
        return email == null ? "" : email.trim().toLowerCase();
    }

    // ---------- Chave do par ----------

    /**
     * Chave AES do par remetente → destinatário. Os dois lados chegam ao mesmo valor:
     * o remetente com (sua privada, pública do destinatário) e o destinatário com
     * (sua privada, pública do remetente). Os ids amarram a chave a esse par de pessoas.
     */
    public static byte[] chavePar(PrivateKey minha, PublicKey outro, int remetenteId, int destinatarioId)
            throws GeneralSecurityException {
        KeyAgreement acordo = KeyAgreement.getInstance("ECDH");
        acordo.init(minha);
        acordo.doPhase(outro, true);
        byte[] segredo = acordo.generateSecret();
        try {
            MessageDigest sha = MessageDigest.getInstance("SHA-256");
            sha.update(segredo);
            sha.update((PREFIXO_PAR + remetenteId + "|" + destinatarioId).getBytes(StandardCharsets.UTF_8));
            return sha.digest();
        } finally {
            Arrays.fill(segredo, (byte) 0);
        }
    }

    // ---------- AES-256-GCM ----------

    /** Cifra com AES-GCM. O resultado já inclui a tag de 16 bytes no final. */
    public static byte[] cifrar(byte[] chave, byte[] iv, String aad, byte[] dados) throws GeneralSecurityException {
        Cipher cifra = Cipher.getInstance("AES/GCM/NoPadding");
        cifra.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(chave, "AES"), new GCMParameterSpec(TAG_BITS, iv));
        cifra.updateAAD(aad.getBytes(StandardCharsets.UTF_8));
        return cifra.doFinal(dados);
    }

    /**
     * Decifra com AES-GCM.
     *
     * @throws AEADBadTagException se a chave estiver errada ou se o dado, o IV ou o AAD
     * (projeto, remetente, destinatário) tiverem sido alterados.
     */
    public static byte[] decifrar(byte[] chave, byte[] iv, String aad, byte[] dados) throws GeneralSecurityException {
        Cipher cifra = Cipher.getInstance("AES/GCM/NoPadding");
        cifra.init(Cipher.DECRYPT_MODE, new SecretKeySpec(chave, "AES"), new GCMParameterSpec(TAG_BITS, iv));
        cifra.updateAAD(aad.getBytes(StandardCharsets.UTF_8));
        return cifra.doFinal(dados);
    }
}
