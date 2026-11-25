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

    // Pega a conexão com o banco
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

    // Executa a consulta e monta a lista de entregas com tarefa, projeto e arquivo
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

    // Entregas das tarefas dos projetos de um Criador (para aprovar/rejeitar)
    public ArrayList<EntregaTarefa> getListaPorCriador(int criadorId) {
        try (PreparedStatement stmt = con.prepareStatement(SQL_LISTA + " WHERE p.usuario_id = ? ORDER BY e.dataEntrega DESC")) {
            stmt.setInt(1, criadorId);
            return consultar(stmt);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Entregas feitas por um Participante (para acompanhar o resultado)
    public ArrayList<EntregaTarefa> getListaPorResponsavel(int usuarioId) {
        try (PreparedStatement stmt = con.prepareStatement(SQL_LISTA + " WHERE t.usuario_id = ? ORDER BY e.dataEntrega DESC")) {
            stmt.setInt(1, usuarioId);
            return consultar(stmt);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * RF006: grava o link/arquivo (se houver) e registra a entrega como "enviada".
     * Só o responsável pela tarefa entrega, e o arquivo fica no nome dele (não no usuário do objeto).
     */
    public boolean enviar(EntregaTarefa entrega, int responsavelId) {
        try {
            if (entrega.getTarefa() == null) {
                return false;
            }
            int tarefaId = entrega.getTarefa().getId();
            try (PreparedStatement dona = con.prepareStatement("SELECT 1 FROM tarefa WHERE id = ? AND usuario_id = ?")) {
                dona.setInt(1, tarefaId);
                dona.setInt(2, responsavelId);
                if (!dona.executeQuery().next()) {
                    return false;   // a tarefa não é de quem está entregando
                }
            }

            Integer arquivoId = null;
            Arquivo arquivo = entrega.getArquivo();
            if (arquivo != null) {
                String sqlArquivo = "insert into arquivo (nomeArquivo, arquivoUrl, usuario_id, tarefa_id) values (?, ?, ?, ?)";
                try (PreparedStatement stmtArquivo = con.prepareStatement(sqlArquivo, Statement.RETURN_GENERATED_KEYS)) {
                    stmtArquivo.setString(1, arquivo.getNomeArquivo());
                    stmtArquivo.setString(2, arquivo.getArquivoUrl());
                    stmtArquivo.setInt(3, responsavelId);
                    stmtArquivo.setInt(4, tarefaId);
                    stmtArquivo.executeUpdate();
                    ResultSet chaves = stmtArquivo.getGeneratedKeys();
                    if (chaves.next()) {
                        arquivoId = chaves.getInt(1);
                    }
                }
            }

            String sql = "insert into entregatarefa (status, tarefa_id, arquivo_id) values ('enviada', ?, ?)";
            try (PreparedStatement stmt = con.prepareStatement(sql)) {
                stmt.setInt(1, tarefaId);
                if (arquivoId != null) {
                    stmt.setInt(2, arquivoId);
                } else {
                    stmt.setNull(2, java.sql.Types.INTEGER);
                }
                stmt.execute();
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Dono do projeto aprova (tarefa vira "concluida") ou rejeita (tarefa volta para "em_andamento").
     * Só aceita "aprovada" ou "rejeitada", só em entrega que ainda aguarda revisão, e a tarefa
     * que muda de status é a da entrega no banco, não a que veio no objeto.
     */
    public boolean avaliar(EntregaTarefa entrega, int criadorId) {
        String novoStatus = entrega.getStatus();
        if (!"aprovada".equals(novoStatus) && !"rejeitada".equals(novoStatus)) {
            return false;
        }
        try {
            int tarefaId;
            String busca = "SELECT e.tarefa_id FROM entregatarefa e "
                    + "INNER JOIN tarefa t ON t.id = e.tarefa_id "
                    + "INNER JOIN projeto p ON p.id = t.projeto_id "
                    + "WHERE e.id = ? AND p.usuario_id = ? AND e.status IN ('enviada', 'em_revisao')";
            try (PreparedStatement stmt = con.prepareStatement(busca)) {
                stmt.setInt(1, entrega.getId());
                stmt.setInt(2, criadorId);
                ResultSet res = stmt.executeQuery();
                if (!res.next()) {
                    return false;   // não é do criador logado ou já foi avaliada
                }
                tarefaId = res.getInt(1);
            }

            String sql = "Update entregatarefa set status = ? where id = ? and status IN ('enviada', 'em_revisao')";
            try (PreparedStatement stmt = con.prepareStatement(sql)) {
                stmt.setString(1, novoStatus);
                stmt.setInt(2, entrega.getId());
                if (stmt.executeUpdate() != 1) {
                    return false;
                }
            }

            String statusTarefa = "aprovada".equals(novoStatus) ? "concluida" : "em_andamento";
            return new TarefaDao().atualizarStatus(tarefaId, statusTarefa);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
