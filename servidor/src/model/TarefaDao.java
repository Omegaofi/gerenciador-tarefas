package model;

import factory.Conexao;
import java.sql.*;
import java.util.ArrayList;
import modelDominio.Projeto;
import modelDominio.Tarefa;
import modelDominio.Usuario;

public class TarefaDao {

    private Connection con;

    // Pega a conexão com o banco
    public TarefaDao() {
        con = Conexao.getConexao();
    }

    private static final String SQL_LISTA = "SELECT t.*, p.titulo tituloProjeto, u.nome nomeUsuario "
            + "FROM tarefa t "
            + "INNER JOIN projeto p ON t.projeto_id = p.id "
            + "INNER JOIN usuario u ON t.usuario_id = u.id";

    // Executa a consulta e monta a lista de tarefas com projeto e responsável
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

    // RF005: tarefas atribuídas a um colaborador (Participante)
    public ArrayList<Tarefa> getListaPorResponsavel(int usuarioId) {
        try (PreparedStatement stmt = con.prepareStatement(SQL_LISTA + " WHERE t.usuario_id = ? ORDER BY t.prazo")) {
            stmt.setInt(1, usuarioId);
            return consultar(stmt);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Tarefas dos projetos criados por um idealizador (Criador)
    public ArrayList<Tarefa> getListaPorCriador(int criadorId) {
        try (PreparedStatement stmt = con.prepareStatement(SQL_LISTA + " WHERE p.usuario_id = ? ORDER BY t.prazo")) {
            stmt.setInt(1, criadorId);
            return consultar(stmt);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Insere uma tarefa no banco (o controller já conferiu projeto e responsável)
    public boolean inserir(Tarefa t) {
        String sql = "insert into tarefa (titulo, prazo, status, projeto_id, usuario_id) values (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, t.getTitulo());
            stmt.setDate(2, new java.sql.Date(t.getPrazo().getTime()));
            stmt.setString(3, t.getStatus());
            stmt.setInt(4, t.getProjeto().getId());
            stmt.setInt(5, t.getUsuario().getId());
            stmt.execute();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Atualiza uma tarefa, só se ela JÁ for de um projeto do criador logado.
     * (O controller confere à parte se o projeto novo e o responsável novo são válidos.)
     */
    public boolean atualizar(Tarefa t, int criadorId) {
        String sql = "UPDATE tarefa t INNER JOIN projeto p ON p.id = t.projeto_id "
                + "SET t.titulo = ?, t.prazo = ?, t.status = ?, t.projeto_id = ?, t.usuario_id = ? "
                + "WHERE t.id = ? AND p.usuario_id = ?";
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, t.getTitulo());
            stmt.setDate(2, new java.sql.Date(t.getPrazo().getTime()));
            stmt.setString(3, t.getStatus());
            stmt.setInt(4, t.getProjeto().getId());
            stmt.setInt(5, t.getUsuario().getId());
            stmt.setInt(6, t.getId());
            stmt.setInt(7, criadorId);
            return stmt.executeUpdate() == 1;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Muda só o status de uma tarefa (usado na avaliação da entrega, que já conferiu o dono)
    public boolean atualizarStatus(int tarefaId, String status) {
        try (PreparedStatement stmt = con.prepareStatement("Update tarefa set status = ? where id = ?")) {
            stmt.setString(1, status);
            stmt.setInt(2, tarefaId);
            stmt.execute();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /** Exclui uma tarefa, só se ela for de um projeto do criador logado. */
    public boolean excluir(int tarefaId, int criadorId) {
        String sql = "DELETE t FROM tarefa t INNER JOIN projeto p ON p.id = t.projeto_id "
                + "WHERE t.id = ? AND p.usuario_id = ?";
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, tarefaId);
            stmt.setInt(2, criadorId);
            return stmt.executeUpdate() == 1;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
