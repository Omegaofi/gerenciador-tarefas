package model;

import factory.Conexao;
import java.sql.*;
import java.util.ArrayList;
import modelDominio.Projeto;
import modelDominio.Tarefa;
import modelDominio.Usuario;

public class TarefaDao {

    private Connection con;

    public TarefaDao() {
        con = Conexao.getConexao();
    }

    private static final String SQL_LISTA = "SELECT t.*, p.titulo tituloProjeto, u.nome nomeUsuario "
            + "FROM tarefa t "
            + "INNER JOIN projeto p ON t.projeto_id = p.id "
            + "INNER JOIN usuario u ON t.usuario_id = u.id";

    private ArrayList<Tarefa> consultar(PreparedStatement stmt) throws SQLException {
        ArrayList<Tarefa> lista = new ArrayList();
        ResultSet res = stmt.executeQuery();
        while (res.next()) {
            Projeto projeto = new Projeto(res.getInt("projeto_id"), res.getString("tituloProjeto"));
            Usuario usuario = new Usuario(
                    res.getInt("usuario_id"),
                    res.getString("nomeUsuario"),
                    null, null, null, null, null
            );
            Tarefa tarefa = new Tarefa(
                    res.getInt("id"),
                    res.getString("titulo"),
                    res.getDate("prazo"),
                    res.getString("status"),
                    projeto,
                    usuario,
                    res.getTimestamp("criado_em")
            );
            lista.add(tarefa);
        }
        return lista;
    }

    public ArrayList<Tarefa> getLista() {
        try {
            PreparedStatement stmt = con.prepareStatement(SQL_LISTA + " ORDER BY t.prazo");
            return consultar(stmt);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // RF005: tarefas atribuídas a um colaborador (Participante)
    public ArrayList<Tarefa> getListaPorResponsavel(int usuarioId) {
        try {
            PreparedStatement stmt = con.prepareStatement(SQL_LISTA + " WHERE t.usuario_id = ? ORDER BY t.prazo");
            stmt.setInt(1, usuarioId);
            return consultar(stmt);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Tarefas dos projetos criados por um idealizador (Criador)
    public ArrayList<Tarefa> getListaPorCriador(int criadorId) {
        try {
            PreparedStatement stmt = con.prepareStatement(SQL_LISTA + " WHERE p.usuario_id = ? ORDER BY t.prazo");
            stmt.setInt(1, criadorId);
            return consultar(stmt);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean inserir(Tarefa t) {
        try {
            String sql = "insert into tarefa (titulo, prazo, status, projeto_id, usuario_id) values (?, ?, ?, ?, ?)";
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setString(1, t.getTitulo());
            stmt.setDate(2, new java.sql.Date(t.getPrazo().getTime()));
            stmt.setString(3, t.getStatus());
            stmt.setInt(4, t.getProjeto().getId());
            stmt.setInt(5, t.getUsuario().getId());
            stmt.execute();
            stmt.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean atualizar(Tarefa t) {
        try {
            String sql = "Update tarefa set titulo = (?), prazo = (?), status = (?), projeto_id = (?), usuario_id = (?) where id = (?)";
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setString(1, t.getTitulo());
            stmt.setDate(2, new java.sql.Date(t.getPrazo().getTime()));
            stmt.setString(3, t.getStatus());
            stmt.setInt(4, t.getProjeto().getId());
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

    public boolean atualizarStatus(int tarefaId, String status) {
        try {
            String sql = "Update tarefa set status = (?) where id = (?)";
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setString(1, status);
            stmt.setInt(2, tarefaId);
            stmt.execute();
            stmt.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean excluir(Tarefa t) {
        try {
            String sql = "delete from tarefa where id = (?)";
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
