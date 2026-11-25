package model;

import factory.Conexao;
import java.sql.*;
import java.util.ArrayList;
import modelDominio.Projeto;
import modelDominio.Usuario;

public class ProjetoDao {

    private Connection con;

    // Pega a conexão com o banco
    public ProjetoDao() {
        con = Conexao.getConexao();
    }

    private static final String SQL_LISTA = "SELECT p.*, u.nome nomeUsuario FROM projeto p "
            + "INNER JOIN usuario u ON p.usuario_id = u.id";

    // Executa a consulta e monta a lista de projetos com o dono de cada um
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

    // Projetos criados por um idealizador (Criador)
    public ArrayList<Projeto> getListaPorCriador(int criadorId) {
        try (PreparedStatement stmt = con.prepareStatement(SQL_LISTA + " WHERE p.usuario_id = ? ORDER BY p.criado_em DESC")) {
            stmt.setInt(1, criadorId);
            return consultar(stmt);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /** Insere um projeto. O dono é sempre o criador logado, não o usuário que veio no objeto. */
    public boolean inserir(Projeto t, int criadorId) {
        String sql = "insert into projeto (titulo, descricao, area_atuacao, status, usuario_id) values (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, t.getTitulo());
            stmt.setString(2, t.getDescricao());
            stmt.setString(3, t.getAreaAtuacao());
            stmt.setString(4, t.getStatus());
            stmt.setInt(5, criadorId);
            stmt.execute();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /** Atualiza um projeto, só se ele for do criador logado. O dono não muda. */
    public boolean atualizar(Projeto t, int criadorId) {
        String sql = "Update projeto set titulo = ?, descricao = ?, area_atuacao = ?, status = ? "
                + "where id = ? and usuario_id = ?";
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, t.getTitulo());
            stmt.setString(2, t.getDescricao());
            stmt.setString(3, t.getAreaAtuacao());
            stmt.setString(4, t.getStatus());
            stmt.setInt(5, t.getId());
            stmt.setInt(6, criadorId);
            return stmt.executeUpdate() == 1;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /** Exclui um projeto, só se ele for do criador logado. */
    public boolean excluir(int projetoId, int criadorId) {
        try (PreparedStatement stmt = con.prepareStatement("delete from projeto where id = ? and usuario_id = ?")) {
            stmt.setInt(1, projetoId);
            stmt.setInt(2, criadorId);
            return stmt.executeUpdate() == 1;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
