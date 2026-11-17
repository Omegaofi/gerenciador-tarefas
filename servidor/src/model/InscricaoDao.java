package model;

import factory.Conexao;
import java.sql.*;
import java.util.ArrayList;
import modelDominio.Inscricao;
import modelDominio.Projeto;
import modelDominio.Usuario;

/**
 * Inscrições em projetos (RF007 / RF008), guardadas na tabela projeto_participante.
 * Os ids de usuário que chegam aqui são sempre os do usuário logado na conexão,
 * nunca um id escolhido pelo cliente.
 */
public class InscricaoDao {

    private final Connection con;

    public InscricaoDao() {
        con = Conexao.getConexao();
    }

    /**
     * Projetos abertos (planejado ou em andamento) de outros criadores, nos quais o usuário
     * ainda não é membro. Cada um vem com a inscrição do usuário: status null = não inscrito.
     * Por privacidade, só vão título, descrição e área: o andamento e o criador ficam no servidor.
     */
    public ArrayList<Inscricao> projetosDisponiveis(int usuarioId) {
        ArrayList<Inscricao> lista = new ArrayList<>();
        String sql = "SELECT p.id, p.titulo, p.descricao, p.area_atuacao, p.criado_em, "
                + "pp.status statusInscricao, pp.data_inscricao "
                + "FROM projeto p "
                + "LEFT JOIN projeto_participante pp ON pp.projeto_id = p.id AND pp.usuario_id = ? "
                + "WHERE p.status IN ('planejado', 'em_andamento') "
                + "AND p.usuario_id <> ? "
                + "AND (pp.status IS NULL OR pp.status <> 'aceito') "
                + "AND NOT EXISTS (SELECT 1 FROM tarefa t WHERE t.projeto_id = p.id AND t.usuario_id = ?) "
                + "ORDER BY p.criado_em DESC";
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, usuarioId);
            stmt.setInt(2, usuarioId);
            stmt.setInt(3, usuarioId);
            ResultSet res = stmt.executeQuery();
            while (res.next()) {
                Projeto projeto = new Projeto(res.getInt("id"), res.getString("titulo"), res.getString("descricao"),
                        res.getString("area_atuacao"), null, null, res.getTimestamp("criado_em"));
                lista.add(new Inscricao(projeto, null, res.getString("statusInscricao"),
                        res.getTimestamp("data_inscricao")));
            }
            return lista;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Inscreve o usuário no projeto (status pendente). Quem foi recusado pode tentar de novo.
     * Não aceita: projeto fechado, projeto do próprio usuário ou inscrição já pendente/aceita.
     */
    public boolean inscrever(int projetoId, int usuarioId) {
        try {
            // 1) Projeto precisa existir, estar aberto e não ser do próprio usuário
            PreparedStatement proj = con.prepareStatement("SELECT 1 FROM projeto WHERE id = ? AND usuario_id <> ? "
                    + "AND status IN ('planejado', 'em_andamento')");
            proj.setInt(1, projetoId);
            proj.setInt(2, usuarioId);
            boolean projetoAberto = proj.executeQuery().next();
            proj.close();
            if (!projetoAberto) {
                return false;
            }

            // 2) Já existe inscrição? Pendente ou aceita: nada a fazer. Recusada: volta a pendente.
            PreparedStatement busca = con.prepareStatement(
                    "SELECT status FROM projeto_participante WHERE projeto_id = ? AND usuario_id = ?");
            busca.setInt(1, projetoId);
            busca.setInt(2, usuarioId);
            ResultSet res = busca.executeQuery();
            String statusAtual = res.next() ? res.getString("status") : null;
            busca.close();

            PreparedStatement stmt;
            if (statusAtual == null) {
                stmt = con.prepareStatement("INSERT INTO projeto_participante (projeto_id, usuario_id) VALUES (?, ?)");
            } else if (Inscricao.RECUSADO.equals(statusAtual)) {
                stmt = con.prepareStatement("UPDATE projeto_participante SET status = 'pendente', data_inscricao = NOW() "
                        + "WHERE projeto_id = ? AND usuario_id = ? AND status = 'recusado'");
            } else {
                return false;
            }
            stmt.setInt(1, projetoId);
            stmt.setInt(2, usuarioId);
            boolean ok = stmt.executeUpdate() == 1;
            stmt.close();
            return ok;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /** Inscrições pendentes nos projetos de um criador, com os dados de quem se inscreveu. */
    public ArrayList<Inscricao> pendentesDoCriador(int criadorId) {
        ArrayList<Inscricao> lista = new ArrayList<>();
        String sql = "SELECT pp.status, pp.data_inscricao, p.id idProjeto, p.titulo, "
                + "u.id idUsuario, u.nome, u.email, u.biografia "
                + "FROM projeto_participante pp "
                + "INNER JOIN projeto p ON p.id = pp.projeto_id "
                + "INNER JOIN usuario u ON u.id = pp.usuario_id "
                + "WHERE p.usuario_id = ? AND pp.status = 'pendente' "
                + "ORDER BY pp.data_inscricao";
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, criadorId);
            ResultSet res = stmt.executeQuery();
            while (res.next()) {
                Projeto projeto = new Projeto(res.getInt("idProjeto"), res.getString("titulo"));
                Usuario candidato = new Usuario(res.getInt("idUsuario"), res.getString("nome"), res.getString("email"),
                        null, res.getString("biografia"), "participante", null);
                lista.add(new Inscricao(projeto, candidato, res.getString("status"), res.getTimestamp("data_inscricao")));
            }
            return lista;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /** O criador aceita ou recusa. Só funciona se o projeto for dele e a inscrição estiver pendente. */
    public boolean responder(int projetoId, int usuarioId, int criadorId, boolean aceitar) {
        String sql = "UPDATE projeto_participante pp INNER JOIN projeto p ON p.id = pp.projeto_id "
                + "SET pp.status = ? "
                + "WHERE pp.projeto_id = ? AND pp.usuario_id = ? AND p.usuario_id = ? AND pp.status = 'pendente'";
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, aceitar ? Inscricao.ACEITO : Inscricao.RECUSADO);
            stmt.setInt(2, projetoId);
            stmt.setInt(3, usuarioId);
            stmt.setInt(4, criadorId);
            return stmt.executeUpdate() == 1;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Quem pode receber tarefa no projeto: participantes aceitos e quem já tem tarefa nele
     * (tarefas antigas, de antes das inscrições). Null se o projeto não for do criador.
     */
    public ArrayList<Usuario> participantesDoProjeto(int projetoId, int criadorId) {
        ArrayList<Usuario> lista = new ArrayList<>();
        String sql = "SELECT u.id, u.nome, u.email, u.tipo FROM usuario u WHERE u.id IN ("
                + "  SELECT pp.usuario_id FROM projeto_participante pp WHERE pp.projeto_id = ? AND pp.status = 'aceito' "
                + "  UNION SELECT t.usuario_id FROM tarefa t WHERE t.projeto_id = ?"
                + ") AND EXISTS (SELECT 1 FROM projeto p WHERE p.id = ? AND p.usuario_id = ?) "
                + "ORDER BY u.nome";
        try {
            if (!isDono(projetoId, criadorId)) {
                return null;
            }
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setInt(1, projetoId);
            stmt.setInt(2, projetoId);
            stmt.setInt(3, projetoId);
            stmt.setInt(4, criadorId);
            ResultSet res = stmt.executeQuery();
            while (res.next()) {
                lista.add(new Usuario(res.getInt("id"), res.getString("nome"), res.getString("email"),
                        null, null, res.getString("tipo"), null));
            }
            stmt.close();
            return lista;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /** Tarefa só pode ir para um membro do projeto, e o projeto tem que ser de quem está criando a tarefa. */
    public boolean podeReceberTarefa(int projetoId, int responsavelId, int criadorId) {
        ArrayList<Usuario> membros = participantesDoProjeto(projetoId, criadorId);
        if (membros == null) {
            return false;
        }
        for (Usuario u : membros) {
            if (u.getId() == responsavelId) {
                return true;
            }
        }
        return false;
    }

    /** Projetos em que o usuário foi aceito (para o Dashboard do participante). */
    public ArrayList<Projeto> projetosParticipando(int usuarioId) {
        ArrayList<Projeto> lista = new ArrayList<>();
        String sql = "SELECT p.*, u.nome nomeCriador FROM projeto p "
                + "INNER JOIN projeto_participante pp ON pp.projeto_id = p.id "
                + "INNER JOIN usuario u ON u.id = p.usuario_id "
                + "WHERE pp.usuario_id = ? AND pp.status = 'aceito' "
                + "ORDER BY p.criado_em DESC";
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, usuarioId);
            ResultSet res = stmt.executeQuery();
            while (res.next()) {
                Usuario criador = new Usuario(res.getInt("usuario_id"), res.getString("nomeCriador"),
                        null, null, null, null, null);
                lista.add(new Projeto(res.getInt("id"), res.getString("titulo"), res.getString("descricao"),
                        res.getString("area_atuacao"), res.getString("status"), criador, res.getTimestamp("criado_em")));
            }
            return lista;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private boolean isDono(int projetoId, int usuarioId) throws SQLException {
        try (PreparedStatement stmt = con.prepareStatement("SELECT 1 FROM projeto WHERE id = ? AND usuario_id = ?")) {
            stmt.setInt(1, projetoId);
            stmt.setInt(2, usuarioId);
            return stmt.executeQuery().next();
        }
    }

    /** True se o usuário já participa de algum projeto (inscrição aceita ou tarefa atribuída). */
    public boolean participaDeAlgumProjeto(int usuarioId) {
        String sql = "SELECT 1 FROM projeto_participante WHERE usuario_id = ? AND status = 'aceito' "
                + "UNION SELECT 1 FROM tarefa WHERE usuario_id = ? LIMIT 1";
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, usuarioId);
            stmt.setInt(2, usuarioId);
            return stmt.executeQuery().next();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
