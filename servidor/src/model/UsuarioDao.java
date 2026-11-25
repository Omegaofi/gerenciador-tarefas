package model;

import factory.Conexao;
import java.sql.*;
import modelDominio.Usuario;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

/**
 * Senhas: o cliente manda o SHA-256 da senha em hexadecimal (código da disciplina de
 * Segurança da Informação, nas telas de cadastro, login e troca de senha) e o servidor
 * guarda esse mesmo valor no banco. A senha em texto nunca chega ao servidor nem ao banco.
 */
public class UsuarioDao {

    private Connection con;

    // Pega a conexão com o banco
    public UsuarioDao() {
        con = Conexao.getConexao();
    }

    // A senha nunca sai do servidor: os objetos devolvidos vão com senha null
    // A chave pública vai junto (os outros membros precisam dela para cifrar)
    private Usuario montarUsuario(ResultSet res) throws SQLException {
        Usuario u = new Usuario(
                res.getInt("id"),
                res.getString("nome"),
                res.getString("email"),
                null,
                res.getString("biografia"),
                res.getString("tipo"),
                res.getTimestamp("criado_em")
        );
        u.setChavePublica(res.getString("chave_publica"));
        return u;
    }

    // Cadastra um usuário; recusa se a senha não vier criptografada
    public boolean inserir(Usuario j) {
        if (!ehSenhaCriptografada(j.getSenha())) {
            return false;   // só aceita a senha criptografada, nunca texto puro
        }
        try {
            String sql = "insert into usuario (nome, email, senha, biografia, tipo) values (?, ?, ?, ?, ?)";
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setString(1, j.getNome());
            stmt.setString(2, j.getEmail());
            stmt.setString(3, j.getSenha());
            stmt.setString(4, j.getBiografia());
            stmt.setString(5, j.getTipo());
            stmt.execute();
            stmt.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // A tela de login manda o SHA-256 da senha (código de Segurança da Informação);
    // aqui ele é conferido com o SHA-256 guardado no banco
    public Usuario login(Usuario user) {
        try {
            String sql = "SELECT * FROM usuario WHERE email = ?";
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setString(1, user.getEmail());
            ResultSet res = stmt.executeQuery();
            String guardada = res.next() ? res.getString("senha") : null;
            if (guardada != null && user.getSenha() != null && confereSenha(user.getSenha(), guardada)) {
                // Só o próprio usuário recebe a cópia trancada da privada (ele precisa da senha para abrir)
                Usuario logado = montarUsuario(res);
                logado.setChavePrivadaCifrada(res.getString("chave_privada_cifrada"));
                logado.setSaltChave(res.getString("salt_chave"));
                logado.setIvChave(res.getString("iv_chave"));
                stmt.close();
                return logado;
            }
            stmt.close();
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /** Confere o SHA-256 recebido com o SHA-256 guardado no banco, em tempo constante. */
    private boolean confereSenha(String recebida, String guardada) {
        if (!ehSenhaCriptografada(recebida) || guardada == null) {
            return false;
        }
        return MessageDigest.isEqual(recebida.getBytes(StandardCharsets.UTF_8),
                guardada.getBytes(StandardCharsets.UTF_8));
    }

    /** Formato do SHA-256 gerado no cadastro e no login: 64 caracteres hexadecimais. */
    private static boolean ehSenhaCriptografada(String valor) {
        return valor != null && valor.matches("[0-9a-f]{64}");
    }

    // ---------- Chat cifrado ----------

    /** Grava as chaves do chat. Só funciona uma vez: não sobrescreve chaves que já existem. */
    public boolean salvarChaves(int usuarioId, Usuario chaves) {
        try {
            String sql = "UPDATE usuario SET chave_publica = ?, chave_privada_cifrada = ?, salt_chave = ?, iv_chave = ? "
                    + "WHERE id = ? AND chave_publica IS NULL";
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setString(1, chaves.getChavePublica());
            stmt.setString(2, chaves.getChavePrivadaCifrada());
            stmt.setString(3, chaves.getSaltChave());
            stmt.setString(4, chaves.getIvChave());
            stmt.setInt(5, usuarioId);
            int alteradas = stmt.executeUpdate();
            stmt.close();
            return alteradas == 1;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Troca de senha do usuário logado.
     *
     * <p>O servidor só troca se o SHA-256 da senha ATUAL conferir com o guardado;
     * a senha nova é guardada como SHA-256 da senha nova.
     * Se o usuário já tem chaves do chat, a cópia da chave privada trancada com a senha nova
     * tem que vir junto: senha e cofre mudam no mesmo UPDATE, senão o chat ficaria trancado
     * com uma senha que não existe mais.</p>
     *
     * @param shaAtual   SHA-256(senha atual), calculado no cliente
     * @param novoDados   senha = SHA-256(senha nova); chave_privada_cifrada, salt e iv novos
     */
    public boolean trocarSenha(int usuarioId, String shaAtual, Usuario novoDados) {
        try {
            String novoSha = novoDados == null ? null : novoDados.getSenha();
            if (shaAtual == null || !ehSenhaCriptografada(novoSha)) {
                return false;   // só aceita a senha criptografada, nunca texto puro
            }

            PreparedStatement busca = con.prepareStatement("SELECT senha, chave_publica FROM usuario WHERE id = ?");
            busca.setInt(1, usuarioId);
            ResultSet res = busca.executeQuery();
            if (!res.next() || !confereSenha(shaAtual, res.getString("senha"))) {
                busca.close();
                return false;   // senha atual errada
            }
            boolean temChaves = res.getString("chave_publica") != null;
            busca.close();

            boolean trazCofre = novoDados.getChavePrivadaCifrada() != null
                    && novoDados.getSaltChave() != null && novoDados.getIvChave() != null;
            if (temChaves && !trazCofre) {
                return false;   // trocaria a senha e deixaria a chave do chat trancada com a antiga
            }

            String sql = "UPDATE usuario SET senha = ?"
                    + (temChaves ? ", chave_privada_cifrada = ?, salt_chave = ?, iv_chave = ?" : "")
                    + " WHERE id = ?";
            PreparedStatement stmt = con.prepareStatement(sql);
            int i = 1;
            stmt.setString(i++, novoSha);
            if (temChaves) {
                stmt.setString(i++, novoDados.getChavePrivadaCifrada());
                stmt.setString(i++, novoDados.getSaltChave());
                stmt.setString(i++, novoDados.getIvChave());
            }
            stmt.setInt(i, usuarioId);
            int alteradas = stmt.executeUpdate();
            stmt.close();
            return alteradas == 1;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
