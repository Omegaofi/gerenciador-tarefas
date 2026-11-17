package gerenciador.tarefas.cliente.cripto;

import static gerenciador.tarefas.cliente.cripto.CriptografiaUtil.aad;
import static gerenciador.tarefas.cliente.cripto.CriptografiaUtil.aleatorio;
import static gerenciador.tarefas.cliente.cripto.CriptografiaUtil.b64;
import static gerenciador.tarefas.cliente.cripto.CriptografiaUtil.deB64;

import gerenciador.tarefas.cliente.controller.ConexaoController;
import gerenciador.tarefas.cliente.cripto.MensagemAberta.Estado;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import modelDominio.ChaveMensagem;
import modelDominio.Mensagem;
import modelDominio.Projeto;
import modelDominio.Usuario;

/**
 * Chat cifrado de ponta a ponta (opção A do projeto "Chat Cifrado NexProject").
 *
 * <p>Cada mensagem é cifrada com uma chave aleatória K (AES-256-GCM). K é cifrada
 * uma vez para cada membro do projeto com a chave do par (ECDH P-256 + SHA-256).
 * Servidor e MySQL só veem dados embaralhados.</p>
 *
 * <p>A chave privada só existe destrancada na memória deste objeto, depois do login.</p>
 */
public class ChatCifrado {

    private static final String PREFIXO_COFRE = "NexProject/cofre/v1|";

    private final ConexaoController conexao;
    private Usuario eu;
    private PrivateKey minhaPrivada;
    private PublicKey minhaPublica;
    private final Map<Integer, PublicKey> publicasPorUsuario = new HashMap<>();

    public ChatCifrado(ConexaoController conexao) {
        this.conexao = conexao;
    }

    public boolean isCofreAberto() {
        return minhaPrivada != null;
    }

    /** Esquece a chave privada (logout). */
    public void fechar() {
        eu = null;
        minhaPrivada = null;
        minhaPublica = null;
        publicasPorUsuario.clear();
    }

    // ---------- Cofre: criação e abertura das chaves do usuário ----------

    /**
     * Chamado logo depois do login, com a senha digitada.
     * Se o usuário já tem chaves, destranca a privada. No primeiro login depois da
     * atualização, gera o par, tranca a privada com a senha e envia ao servidor.
     *
     * @throws GeneralSecurityException se a cópia da privada não abrir com esta senha
     * @throws IllegalStateException se o servidor não aceitar as chaves novas
     */
    public void abrirCofre(Usuario logado, char[] senha) throws GeneralSecurityException {
        fechar();
        if (logado.temChaves()) {
            byte[] chaveCofre = CriptografiaUtil.derivarChave(senha, deB64(logado.getSaltChave()));
            byte[] pkcs8 = null;
            try {
                pkcs8 = CriptografiaUtil.decifrar(chaveCofre, deB64(logado.getIvChave()),
                        PREFIXO_COFRE + logado.getId(), deB64(logado.getChavePrivadaCifrada()));
                minhaPrivada = CriptografiaUtil.lerPrivada(pkcs8);
                minhaPublica = CriptografiaUtil.lerPublica(logado.getChavePublica());
            } finally {
                Arrays.fill(chaveCofre, (byte) 0);
                if (pkcs8 != null) {
                    Arrays.fill(pkcs8, (byte) 0);
                }
            }
        } else {
            criarChaves(logado, senha);
        }
        eu = logado;
        publicasPorUsuario.put(logado.getId(), minhaPublica);
    }

    private void criarChaves(Usuario logado, char[] senha) throws GeneralSecurityException {
        KeyPair par = CriptografiaUtil.gerarParDeChaves();
        byte[] salt = aleatorio(CriptografiaUtil.TAMANHO_SALT);
        byte[] iv = aleatorio(CriptografiaUtil.TAMANHO_IV);
        byte[] chaveCofre = CriptografiaUtil.derivarChave(senha, salt);
        byte[] pkcs8 = par.getPrivate().getEncoded();
        byte[] privadaCifrada;
        try {
            privadaCifrada = CriptografiaUtil.cifrar(chaveCofre, iv, PREFIXO_COFRE + logado.getId(), pkcs8);
        } finally {
            Arrays.fill(chaveCofre, (byte) 0);
            Arrays.fill(pkcs8, (byte) 0);
        }

        Usuario chaves = new Usuario(logado.getEmail(), null);
        chaves.setId(logado.getId());
        chaves.setChavePublica(b64(par.getPublic().getEncoded()));
        chaves.setChavePrivadaCifrada(b64(privadaCifrada));
        chaves.setSaltChave(b64(salt));
        chaves.setIvChave(b64(iv));
        if (!conexao.usuarioSalvarChaves(chaves)) {
            throw new IllegalStateException("O servidor não aceitou as chaves do chat.");
        }
        logado.setChavePublica(chaves.getChavePublica());
        logado.setChavePrivadaCifrada(chaves.getChavePrivadaCifrada());
        logado.setSaltChave(chaves.getSaltChave());
        logado.setIvChave(chaves.getIvChave());
        minhaPrivada = par.getPrivate();
        minhaPublica = par.getPublic();
    }

    // ---------- Troca de senha ----------

    /**
     * Tranca de novo a chave privada (que está aberta na memória) com a senha nova.
     * O par de chaves não muda, então as mensagens antigas continuam abrindo.
     * Nada é gravado aqui: o resultado vai para o servidor junto com a senha nova.
     *
     * @return usuário só com chave_privada_cifrada, salt_chave e iv_chave novos
     */
    public Usuario trancarCofreComNovaSenha(char[] novaSenha) throws GeneralSecurityException {
        exigirCofre();
        byte[] salt = aleatorio(CriptografiaUtil.TAMANHO_SALT);
        byte[] iv = aleatorio(CriptografiaUtil.TAMANHO_IV);
        byte[] chaveCofre = CriptografiaUtil.derivarChave(novaSenha, salt);
        byte[] pkcs8 = minhaPrivada.getEncoded();
        try {
            byte[] privadaCifrada = CriptografiaUtil.cifrar(chaveCofre, iv, PREFIXO_COFRE + eu.getId(), pkcs8);
            Usuario cofre = new Usuario(eu.getEmail(), null);
            cofre.setId(eu.getId());
            cofre.setChavePublica(eu.getChavePublica());
            cofre.setChavePrivadaCifrada(b64(privadaCifrada));
            cofre.setSaltChave(b64(salt));
            cofre.setIvChave(b64(iv));
            return cofre;
        } finally {
            Arrays.fill(chaveCofre, (byte) 0);
            Arrays.fill(pkcs8, (byte) 0);
        }
    }

    /** Depois que o servidor aceitou a troca, a cópia em memória passa a ser a nova. */
    public void confirmarNovoCofre(Usuario cofre) {
        if (eu != null && cofre != null) {
            eu.setChavePrivadaCifrada(cofre.getChavePrivadaCifrada());
            eu.setSaltChave(cofre.getSaltChave());
            eu.setIvChave(cofre.getIvChave());
        }
    }

    /** Impressão digital curta da chave pública, para os membros conferirem pessoalmente (seção 13). */
    public String impressaoDigital() {
        if (minhaPublica == null) {
            return "";
        }
        try {
            byte[] h = java.security.MessageDigest.getInstance("SHA-256").digest(minhaPublica.getEncoded());
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 8; i++) {
                if (i > 0 && i % 2 == 0) {
                    sb.append(' ');
                }
                sb.append(String.format("%02X", h[i]));
            }
            return sb.toString();
        } catch (GeneralSecurityException e) {
            return "";
        }
    }

    // ---------- Envio ----------

    /**
     * Cifra e envia o texto para todos os membros do projeto.
     *
     * @return nomes dos membros que ficaram sem cópia por ainda não terem ativado o chat
     */
    public List<String> enviar(Projeto projeto, String texto) throws GeneralSecurityException {
        exigirCofre();
        int euId = eu.getId();
        ArrayList<Usuario> membros = conexao.projetoMembrosChaves(projeto.getId());
        if (membros == null) {
            throw new IllegalStateException("Você não é membro deste projeto.");
        }

        byte[] k = aleatorio(CriptografiaUtil.TAMANHO_CHAVE_AES);
        byte[] iv = aleatorio(CriptografiaUtil.TAMANHO_IV);
        List<String> semCopia = new ArrayList<>();
        try {
            String conteudo = b64(CriptografiaUtil.cifrar(k, iv, aad(CriptografiaUtil.VERSAO, projeto.getId(), euId),
                    texto.getBytes(StandardCharsets.UTF_8)));

            ArrayList<ChaveMensagem> copias = new ArrayList<>();
            boolean incluiuEu = false;
            for (Usuario m : membros) {
                PublicKey publica = m.getId() == euId ? minhaPublica : publicaDe(m);
                if (publica == null) {
                    semCopia.add(m.getNome());
                    continue;
                }
                copias.add(copiaDeK(k, publica, projeto.getId(), euId, m.getId()));
                incluiuEu |= m.getId() == euId;
            }
            if (!incluiuEu) {
                // garante que quem enviou consegue reler a própria mensagem
                copias.add(copiaDeK(k, minhaPublica, projeto.getId(), euId, euId));
            }

            Mensagem mensagem = new Mensagem(conteudo, b64(iv), eu, projeto, copias);
            if (!conexao.mensagemInserirCifrada(mensagem)) {
                throw new IllegalStateException("O servidor recusou a mensagem.");
            }
        } finally {
            Arrays.fill(k, (byte) 0);   // apaga K da memória
        }
        return semCopia;
    }

    private ChaveMensagem copiaDeK(byte[] k, PublicKey publicaDestino, int projetoId, int remetenteId,
            int destinatarioId) throws GeneralSecurityException {
        byte[] par = CriptografiaUtil.chavePar(minhaPrivada, publicaDestino, remetenteId, destinatarioId);
        try {
            byte[] iv2 = aleatorio(CriptografiaUtil.TAMANHO_IV);
            byte[] kc = CriptografiaUtil.cifrar(par, iv2,
                    aad(CriptografiaUtil.VERSAO, projetoId, remetenteId, destinatarioId), k);
            return new ChaveMensagem(destinatarioId, b64(kc), b64(iv2));
        } finally {
            Arrays.fill(par, (byte) 0);
        }
    }

    // ---------- Leitura ----------

    /** Busca e decifra as mensagens do projeto com id maior que {@code depoisDoId}. */
    public List<MensagemAberta> carregar(Projeto projeto, int depoisDoId) {
        exigirCofre();
        List<MensagemAberta> abertas = new ArrayList<>();
        ArrayList<Mensagem> recebidas = conexao.mensagemListaPorProjeto(projeto.getId(), depoisDoId);
        for (Mensagem msg : recebidas) {
            abertas.add(abrir(msg, projeto.getId()));
        }
        return abertas;
    }

    private MensagemAberta abrir(Mensagem msg, int projetoId) {
        Usuario autor = msg.getUsuario();
        int remetenteId = autor != null ? autor.getId() : 0;
        String nome = autor != null ? autor.getNome() : "Desconhecido";

        ChaveMensagem minha = msg.getMinhaChave();
        if (minha == null) {
            return new MensagemAberta(msg.getId(), remetenteId, nome, msg.getDataEnvio(),
                    "Mensagem enviada antes de você entrar no projeto", Estado.ANTES_DE_ENTRAR);
        }
        byte[] par = null;
        byte[] k = null;
        try {
            PublicKey publicaRemetente = remetenteId == eu.getId() ? minhaPublica : publicaDe(autor);
            if (publicaRemetente == null) {
                throw new GeneralSecurityException("remetente sem chave pública");
            }
            int euId = eu.getId();
            par = CriptografiaUtil.chavePar(minhaPrivada, publicaRemetente, remetenteId, euId);
            k = CriptografiaUtil.decifrar(par, deB64(minha.getIv()),
                    aad(CriptografiaUtil.VERSAO, projetoId, remetenteId, euId), deB64(minha.getChaveCifrada()));
            String texto = new String(CriptografiaUtil.decifrar(k, deB64(msg.getIv()),
                    aad(CriptografiaUtil.VERSAO, projetoId, remetenteId), deB64(msg.getConteudo())),
                    StandardCharsets.UTF_8);
            return new MensagemAberta(msg.getId(), remetenteId, nome, msg.getDataEnvio(), texto, Estado.CIFRADA_OK);
        } catch (GeneralSecurityException | IllegalArgumentException e) {
            // AEADBadTagException (tag não confere) ou Base64 inválido: dado alterado no banco
            return new MensagemAberta(msg.getId(), remetenteId, nome, msg.getDataEnvio(),
                    "Mensagem corrompida ou alterada", Estado.CORROMPIDA);
        } finally {
            if (par != null) {
                Arrays.fill(par, (byte) 0);
            }
            if (k != null) {
                Arrays.fill(k, (byte) 0);
            }
        }
    }

    private PublicKey publicaDe(Usuario usuario) throws GeneralSecurityException {
        if (usuario == null || !usuario.temChaves()) {
            return null;
        }
        PublicKey publica = publicasPorUsuario.get(usuario.getId());
        if (publica == null) {
            publica = CriptografiaUtil.lerPublica(usuario.getChavePublica());
            publicasPorUsuario.put(usuario.getId(), publica);
        }
        return publica;
    }

    private void exigirCofre() {
        if (!isCofreAberto()) {
            throw new IllegalStateException("As chaves do chat não estão abertas. Faça login novamente.");
        }
    }
}
