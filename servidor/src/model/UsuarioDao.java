package model;

import factory.Conexao;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.sql.*;
import java.util.ArrayList;
import modelDominio.Usuario;

public class UsuarioDao {

    private Connection con;

    public UsuarioDao() {
        con = Conexao.getConexao();
    }

    // Todas as senhas são guardadas como SHA-256 em hexadecimal (64 caracteres)
    private String hash(String senha) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(senha.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // A senha nunca sai do servidor: os objetos devolvidos vão com senha null
    private Usuario montarUsuario(ResultSet res) throws SQLException {
        return new Usuario(
                res.getInt("id"),
                res.getString("nome"),
                res.getString("email"),
                null,
                res.getString("biografia"),
                res.getString("tipo"),
                res.getTimestamp("criado_em")
        );
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
            stmt.setString(3, hash(j.getSenha()));
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
                stmt.setString(i++, hash(j.getSenha()));
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

    public Usuario login(Usuario user) {
        try {
            String sql = "SELECT * FROM usuario WHERE email = ? AND senha = ?";
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setString(1, user.getEmail());
            stmt.setString(2, hash(user.getSenha()));
            ResultSet res = stmt.executeQuery();
            if (res.next()) {
                return montarUsuario(res);
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
