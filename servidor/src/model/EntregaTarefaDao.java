package model;

import factory.Conexao;
import java.sql.*;
import java.util.ArrayList;
import modelDominio.Arquivo;
import modelDominio.EntregaTarefa;
import modelDominio.Projeto;
import modelDominio.Tarefa;
import modelDominio.Usuario;

public class EntregaTarefaDao {

    private Connection con;

    public EntregaTarefaDao() {
        con = Conexao.getConexao();
    }

    private static final String SQL_LISTA = "SELECT e.*, t.titulo tituloTarefa, t.prazo prazoTarefa, t.status statusTarefa, "
            + "t.projeto_id, p.titulo tituloProjeto, t.usuario_id, u.nome nomeUsuario, "
            + "a.nomeArquivo nomeArquivo, a.arquivoUrl arquivoUrl "
            + "FROM entregatarefa e "
            + "INNER JOIN tarefa t ON e.tarefa_id = t.id "
            + "INNER JOIN projeto p ON t.projeto_id = p.id "
            + "INNER JOIN usuario u ON t.usuario_id = u.id "
            + "LEFT JOIN arquivo a ON e.arquivo_id = a.id";

    private ArrayList<EntregaTarefa> consultar(PreparedStatement stmt) throws SQLException {
        ArrayList<EntregaTarefa> lista = new ArrayList();
        ResultSet res = stmt.executeQuery();
        while (res.next()) {
            Projeto projeto = new Projeto(res.getInt("projeto_id"), res.getString("tituloProjeto"));
            Usuario responsavel = new Usuario(
                    res.getInt("usuario_id"),
                    res.getString("nomeUsuario"),
                    null, null, null, null, null
            );
            Tarefa tarefa = new Tarefa(
                    res.getInt("tarefa_id"),
                    res.getString("tituloTarefa"),
                    res.getDate("prazoTarefa"),
                    res.getString("statusTarefa"),
                    projeto,
                    responsavel,
                    null
            );
            Arquivo arquivo = (res.getObject("arquivo_id") != null)
                    ? new Arquivo(res.getInt("arquivo_id"), res.getString("nomeArquivo"), res.getString("arquivoUrl"), responsavel, tarefa, null)
                    : null;
            EntregaTarefa entrega = new EntregaTarefa(
                    res.getInt("id"),
                    res.getTimestamp("dataEntrega"),
                    res.getString("status"),
                    tarefa,
                    arquivo
            );
            lista.add(entrega);
        }
        return lista;
    }

    public ArrayList<EntregaTarefa> getLista() {
        try {
            PreparedStatement stmt = con.prepareStatement(SQL_LISTA + " ORDER BY e.dataEntrega DESC");
            return consultar(stmt);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Entregas das tarefas dos projetos de um Criador (para aprovar/rejeitar)
    public ArrayList<EntregaTarefa> getListaPorCriador(int criadorId) {
        try {
            PreparedStatement stmt = con.prepareStatement(SQL_LISTA + " WHERE p.usuario_id = ? ORDER BY e.dataEntrega DESC");
            stmt.setInt(1, criadorId);
            return consultar(stmt);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Entregas feitas por um Participante (para acompanhar o resultado)
    public ArrayList<EntregaTarefa> getListaPorResponsavel(int usuarioId) {
        try {
            PreparedStatement stmt = con.prepareStatement(SQL_LISTA + " WHERE t.usuario_id = ? ORDER BY e.dataEntrega DESC");
            stmt.setInt(1, usuarioId);
            return consultar(stmt);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // RF006: grava o link/arquivo (se houver) e registra a entrega como "enviada"
    public boolean enviar(EntregaTarefa entrega) {
        try {
            Integer arquivoId = null;
            Arquivo arquivo = entrega.getArquivo();
            if (arquivo != null) {
                String sqlArquivo = "insert into arquivo (nomeArquivo, arquivoUrl, usuario_id, tarefa_id) values (?, ?, ?, ?)";
                PreparedStatement stmtArquivo = con.prepareStatement(sqlArquivo, Statement.RETURN_GENERATED_KEYS);
                stmtArquivo.setString(1, arquivo.getNomeArquivo());
                stmtArquivo.setString(2, arquivo.getArquivoUrl());
                stmtArquivo.setInt(3, arquivo.getUsuario().getId());
                stmtArquivo.setInt(4, entrega.getTarefa().getId());
                stmtArquivo.executeUpdate();
                ResultSet chaves = stmtArquivo.getGeneratedKeys();
                if (chaves.next()) {
                    arquivoId = chaves.getInt(1);
                }
                stmtArquivo.close();
            }

            String sql = "insert into entregatarefa (status, tarefa_id, arquivo_id) values ('enviada', ?, ?)";
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setInt(1, entrega.getTarefa().getId());
            if (arquivoId != null) {
                stmt.setInt(2, arquivoId);
            } else {
                stmt.setNull(2, java.sql.Types.INTEGER);
            }
            stmt.execute();
            stmt.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Criador aprova (tarefa vira "concluida") ou rejeita (tarefa volta para "em_andamento")
    public boolean avaliar(EntregaTarefa entrega) {
        try {
            String sql = "Update entregatarefa set status = (?) where id = (?)";
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setString(1, entrega.getStatus());
            stmt.setInt(2, entrega.getId());
            stmt.execute();
            stmt.close();

            String statusTarefa = "aprovada".equals(entrega.getStatus()) ? "concluida" : "em_andamento";
            return new TarefaDao().atualizarStatus(entrega.getTarefa().getId(), statusTarefa);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean inserir(EntregaTarefa t) {
        try {
            String sql = "insert into entregatarefa (status, tarefa_id, arquivo_id) values (?, ?, ?)";
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setString(1, t.getStatus());
            stmt.setInt(2, t.getTarefa().getId());
            if (t.getArquivo() != null) {
                stmt.setInt(3, t.getArquivo().getId());
            } else {
                stmt.setNull(3, java.sql.Types.INTEGER);
            }
            stmt.execute();
            stmt.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean atualizar(EntregaTarefa t) {
        try {
            String sql = "Update entregatarefa set status = (?), tarefa_id = (?), arquivo_id = (?) where id = (?)";
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setString(1, t.getStatus());
            stmt.setInt(2, t.getTarefa().getId());
            if (t.getArquivo() != null) {
                stmt.setInt(3, t.getArquivo().getId());
            } else {
                stmt.setNull(3, java.sql.Types.INTEGER);
            }
            stmt.setInt(4, t.getId());
            stmt.execute();
            stmt.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean excluir(EntregaTarefa t) {
        try {
            String sql = "delete from entregatarefa where id = (?)";
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
