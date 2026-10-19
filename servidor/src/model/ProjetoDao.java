package model;

import factory.Conexao;
import java.sql.*;
import java.util.ArrayList;
import modelDominio.Projeto;
import modelDominio.Usuario;

public class ProjetoDao {

    private Connection con;

    public ProjetoDao() {
        con = Conexao.getConexao();
    }

    private static final String SQL_LISTA = "SELECT p.*, u.nome nomeUsuario FROM projeto p "
            + "INNER JOIN usuario u ON p.usuario_id = u.id";

    private ArrayList<Projeto> consultar(PreparedStatement stmt) throws SQLException {
        ArrayList<Projeto> lista = new ArrayList();
        ResultSet res = stmt.executeQuery();
        while (res.next()) {
            Usuario dono = new Usuario(
                    res.getInt("usuario_id"),
                    res.getString("nomeUsuario"),
                    null, null, null, null, null
            );
            Projeto projeto = new Projeto(
                    res.getInt("id"),
                    res.getString("titulo"),
                    res.getString("descricao"),
                    res.getString("area_atuacao"),
                    res.getString("status"),
                    dono,
                    res.getTimestamp("criado_em")
            );
            lista.add(projeto);
        }
        return lista;
    }

    public ArrayList<Projeto> getLista() {
        try {
            PreparedStatement stmt = con.prepareStatement(SQL_LISTA + " ORDER BY p.criado_em DESC");
            return consultar(stmt);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Projetos criados por um idealizador (Criador)
    public ArrayList<Projeto> getListaPorCriador(int criadorId) {
        try {
            PreparedStatement stmt = con.prepareStatement(SQL_LISTA + " WHERE p.usuario_id = ? ORDER BY p.criado_em DESC");
            stmt.setInt(1, criadorId);
            return consultar(stmt);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean inserir(Projeto t) {
        try {
            String sql = "insert into projeto (titulo, descricao, area_atuacao, status, usuario_id) values (?, ?, ?, ?, ?)";
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setString(1, t.getTitulo());
            stmt.setString(2, t.getDescricao());
            stmt.setString(3, t.getAreaAtuacao());
            stmt.setString(4, t.getStatus());
            stmt.setInt(5, t.getUsuario().getId());
            stmt.execute();
            stmt.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean atualizar(Projeto t) {
        try {
            String sql = "Update projeto set titulo = (?), descricao = (?), area_atuacao = (?), status = (?), usuario_id = (?) where id = (?)";
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setString(1, t.getTitulo());
            stmt.setString(2, t.getDescricao());
            stmt.setString(3, t.getAreaAtuacao());
            stmt.setString(4, t.getStatus());
            stmt.setInt(5, t.getUsuario().getId());
            stmt.setInt(6, t.getId());
            stmt.execute();
            stmt.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean excluir(Projeto t) {
        try {
            String sql = "delete from projeto where id = (?)";
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setInt(1, t.getId());
            stmt.execute();
            stmt.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
