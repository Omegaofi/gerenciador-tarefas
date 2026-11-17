package model;

import factory.Conexao;
import java.sql.*;
import java.util.ArrayList;
import modelDominio.Usuario;
import org.mindrot.jbcrypt.BCrypt;

public class UsuarioDao {

    private Connection con;

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

    public ArrayList<Usuario> getLista() {
        ArrayList<Usuario> lista = new ArrayList();
        try {
            String sql = "SELECT * FROM usuario ORDER BY nome";
            PreparedStatement stmt = con.prepareStatement(sql);
            ResultSet res = stmt.executeQuery();
            while (res.next()) {
                lista.add(montarUsuario(res));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return lista;
    }

    public boolean inserir(Usuario j) {
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

    // A senha só é alterada quando uma nova senha é enviada
    public boolean atualizar(Usuario j) {
        try {
            boolean trocarSenha = j.getSenha() != null && !j.getSenha().isEmpty();
            String sql = "Update usuario set nome = (?), email = (?), biografia = (?), tipo = (?)"
                    + (trocarSenha ? ", senha = (?)" : "")
                    + " where id = (?)";
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setString(1, j.getNome());
            stmt.setString(2, j.getEmail());
            stmt.setString(3, j.getBiografia());
            stmt.setString(4, j.getTipo());
            int i = 5;
            if (trocarSenha) {
                stmt.setString(i++, j.getSenha());
            }
            stmt.setInt(i, j.getId());
            stmt.execute();
            stmt.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean excluir(Usuario j) {
        try {
            String sql = "delete from usuario where id = (?)";
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setInt(1, j.getId());
            stmt.execute();
            stmt.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // O hash BCrypt tem salt, então a senha é conferida com checkpw e não no SQL
    public Usuario login(Usuario user) {
        try {
            String sql = "SELECT * FROM usuario WHERE email = ?";
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setString(1, user.getEmail());
            ResultSet res = stmt.executeQuery();
            if (res.next() && user.getSenha() != null
                    && confereSenha(user.getSenha(), res.getString("senha"))) {
                // Só o próprio usuário recebe a cópia trancada da privada (ele precisa da senha para abrir)
                Usuario logado = montarUsuario(res);
                logado.setChavePrivadaCifrada(res.getString("chave_privada_cifrada"));
                logado.setSaltChave(res.getString("salt_chave"));
                logado.setIvChave(res.getString("iv_chave"));
                return logado;
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Hash inválido no banco (ex.: senha antiga em SHA-256) faz o checkpw lançar exceção: conta como senha errada
    private boolean confereSenha(String recebida, String guardada) {
        try {
            return guardada != null && BCrypt.checkpw(recebida, guardada);
        } catch (IllegalArgumentException e) {
            return false;
        }
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

    /** Conta antiga: passa a guardar BCrypt(hashLogin) no lugar de BCrypt(senha). */
    public boolean trocarSenhaGuardada(int usuarioId, String novoHashBcrypt) {
        try {
            if (novoHashBcrypt == null || !novoHashBcrypt.startsWith("$2")) {
                return false;   // só aceita um hash BCrypt, nunca texto puro
            }
            PreparedStatement stmt = con.prepareStatement("UPDATE usuario SET senha = ? WHERE id = ?");
            stmt.setString(1, novoHashBcrypt);
            stmt.setInt(2, usuarioId);
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
     * <p>O servidor só troca se o hashLogin da senha ATUAL conferir com o que está guardado.
     * Se o usuário já tem chaves do chat, a cópia da chave privada trancada com a senha nova
     * tem que vir junto: senha e cofre mudam no mesmo UPDATE, senão o chat ficaria trancado
     * com uma senha que não existe mais.</p>
     *
     * @param hashAtual   PBKDF2(senha atual, e-mail), calculado no cliente
     * @param novoDados   senha = BCrypt(PBKDF2(senha nova, e-mail)); chave_privada_cifrada, salt e iv novos
     */
    public boolean trocarSenha(int usuarioId, String hashAtual, Usuario novoDados) {
        try {
            String novoHash = novoDados == null ? null : novoDados.getSenha();
            if (hashAtual == null || novoHash == null || !novoHash.startsWith("$2")) {
                return false;   // só aceita um hash BCrypt, nunca texto puro
            }

            PreparedStatement busca = con.prepareStatement("SELECT senha, chave_publica FROM usuario WHERE id = ?");
            busca.setInt(1, usuarioId);
            ResultSet res = busca.executeQuery();
            if (!res.next() || !confereSenha(hashAtual, res.getString("senha"))) {
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
            stmt.setString(i++, novoHash);
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
