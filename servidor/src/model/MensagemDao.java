package model;

import factory.Conexao;
import java.sql.*;
import java.util.ArrayList;
import modelDominio.ChaveMensagem;
import modelDominio.Mensagem;
import modelDominio.Projeto;
import modelDominio.Usuario;

public class MensagemDao {

    private Connection con;

    // Pega a conexão com o banco
    public MensagemDao() {
        con = Conexao.getConexao();
    }

    // ---------- Chat cifrado ----------

    /**
     * Membros do projeto: o criador, os participantes com inscrição aceita
     * (projeto_participante) e quem tem tarefa no projeto.
     */
    private static final String SQL_MEMBROS =
            "SELECT usuario_id FROM projeto WHERE id = ? "
            + "UNION SELECT usuario_id FROM projeto_participante WHERE projeto_id = ? AND status = 'aceito' "
            + "UNION SELECT usuario_id FROM tarefa WHERE projeto_id = ?";

    // Diz se o usuário é membro do projeto (criador ou participante)
    public boolean isMembro(int projetoId, int usuarioId) {
        try {
            PreparedStatement stmt = con.prepareStatement(
                    "SELECT 1 FROM (" + SQL_MEMBROS + ") membros WHERE usuario_id = ?");
            stmt.setInt(1, projetoId);
            stmt.setInt(2, projetoId);
            stmt.setInt(3, projetoId);
            stmt.setInt(4, usuarioId);
            ResultSet res = stmt.executeQuery();
            boolean membro = res.next();
            stmt.close();
            return membro;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /** Membros com id, nome e chave pública (nunca a privada). */
    public ArrayList<Usuario> membrosComChaves(int projetoId) {
        ArrayList<Usuario> lista = new ArrayList<>();
        try {
            PreparedStatement stmt = con.prepareStatement(
                    "SELECT id, nome, chave_publica FROM usuario WHERE id IN (" + SQL_MEMBROS + ") ORDER BY nome");
            stmt.setInt(1, projetoId);
            stmt.setInt(2, projetoId);
            stmt.setInt(3, projetoId);
            ResultSet res = stmt.executeQuery();
            while (res.next()) {
                Usuario u = new Usuario(res.getInt("id"), res.getString("nome"), null, null, null, null, null);
                u.setChavePublica(res.getString("chave_publica"));
                lista.add(u);
            }
            stmt.close();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return lista;
    }

    /**
     * Grava a mensagem cifrada e uma linha de mensagem_chave por destinatário, numa transação.
     * O remetente é sempre o usuário logado na conexão, não o que o cliente mandou.
     */
    public boolean inserirCifrada(Mensagem m, int remetenteId) {
        int projetoId = m.getProjeto().getId();
        if (m.getChaves() == null || m.getChaves().isEmpty() || !isMembro(projetoId, remetenteId)) {
            return false;
        }
        for (ChaveMensagem c : m.getChaves()) {
            if (!isMembro(projetoId, c.getDestinatarioId())) {
                return false;
            }
        }
        // A conexão é compartilhada entre as threads dos clientes: a transação não pode se misturar
        synchronized (con) {
            try {
                con.setAutoCommit(false);
                PreparedStatement stmt = con.prepareStatement(
                        "INSERT INTO mensagem (conteudo, iv, usuario_id, projeto_id) VALUES (?, ?, ?, ?)",
                        Statement.RETURN_GENERATED_KEYS);
                stmt.setString(1, m.getConteudo());
                stmt.setString(2, m.getIv());
                stmt.setInt(3, remetenteId);
                stmt.setInt(4, projetoId);
                stmt.executeUpdate();
                ResultSet chave = stmt.getGeneratedKeys();
                chave.next();
                int mensagemId = chave.getInt(1);
                stmt.close();

                PreparedStatement copia = con.prepareStatement(
                        "INSERT INTO mensagem_chave (mensagem_id, destinatario_id, chave_cifrada, iv) VALUES (?, ?, ?, ?)");
                for (ChaveMensagem c : m.getChaves()) {
                    copia.setInt(1, mensagemId);
                    copia.setInt(2, c.getDestinatarioId());
                    copia.setString(3, c.getChaveCifrada());
                    copia.setString(4, c.getIv());
                    copia.addBatch();
                }
                copia.executeBatch();
                copia.close();
                con.commit();
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                try {
                    con.rollback();
                } catch (SQLException ignorada) {
                }
                return false;
            } finally {
                try {
                    con.setAutoCommit(true);
                } catch (SQLException ignorada) {
                }
            }
        }
    }

    /** Mensagens do projeto depois de um id, cada uma só com a cópia de K do destinatário. */
    public ArrayList<Mensagem> listaPorProjeto(int projetoId, int depoisDoId, int destinatarioId) {
        ArrayList<Mensagem> lista = new ArrayList<>();
        try {
            String sql = "SELECT m.id, m.conteudo, m.iv, m.dataEnvio, m.usuario_id, "
                    + "u.nome nomeUsuario, u.chave_publica, mc.chave_cifrada, mc.iv ivChave "
                    + "FROM mensagem m "
                    + "INNER JOIN usuario u ON m.usuario_id = u.id "
                    + "LEFT JOIN mensagem_chave mc ON mc.mensagem_id = m.id AND mc.destinatario_id = ? "
                    + "WHERE m.projeto_id = ? AND m.id > ? "
                    + "ORDER BY m.id";
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setInt(1, destinatarioId);
            stmt.setInt(2, projetoId);
            stmt.setInt(3, depoisDoId);
            ResultSet res = stmt.executeQuery();
            while (res.next()) {
                Usuario autor = new Usuario(res.getInt("usuario_id"), res.getString("nomeUsuario"),
                        null, null, null, null, null);
                autor.setChavePublica(res.getString("chave_publica"));
                Mensagem msg = new Mensagem(res.getInt("id"), res.getString("conteudo"),
                        res.getTimestamp("dataEnvio"), autor, new Projeto(projetoId, null));
                msg.setIv(res.getString("iv"));
                if (res.getString("chave_cifrada") != null) {
                    msg.setMinhaChave(new ChaveMensagem(destinatarioId,
                            res.getString("chave_cifrada"), res.getString("ivChave")));
                }
                lista.add(msg);
            }
            stmt.close();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return lista;
    }
}
