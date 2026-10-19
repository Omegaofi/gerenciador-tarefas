package model;

import factory.Conexao;
import java.sql.*;
import java.util.ArrayList;
import modelDominio.Mensagem;
import modelDominio.Projeto;
import modelDominio.Usuario;

public class MensagemDao {

    private Connection con;

    public MensagemDao() {
        con = Conexao.getConexao();
    }

    public ArrayList<Mensagem> getLista() {
        ArrayList<Mensagem> lista = new ArrayList();
        try {
            String sql = "SELECT m.*, u.nome nomeUsuario, p.titulo tituloProjeto "
                    + "FROM mensagem m "
                    + "INNER JOIN usuario u ON m.usuario_id = u.id "
                    + "INNER JOIN projeto p ON m.projeto_id = p.id";
            PreparedStatement stmt = con.prepareStatement(sql);
            ResultSet res = stmt.executeQuery();
            while (res.next()) {
                Usuario usuario = new Usuario(
                        res.getInt("usuario_id"),
                        res.getString("nomeUsuario"),
                        null, null, null, null, null
                );
                Projeto projeto = new Projeto(
                        res.getInt("projeto_id"),
                        res.getString("tituloProjeto")
                );
                Mensagem mensagem = new Mensagem(
                        res.getInt("id"),
                        res.getString("conteudo"),
                        res.getTimestamp("dataEnvio"),
                        usuario,
                        projeto
                );
                lista.add(mensagem);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return lista;
    }

    public boolean inserir(Mensagem t) {
        try {
            String sql = "insert into mensagem (conteudo, usuario_id, projeto_id) values (?, ?, ?)";
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setString(1, t.getConteudo());
            stmt.setInt(2, t.getUsuario().getId());
            stmt.setInt(3, t.getProjeto().getId());
            stmt.execute();
            stmt.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean atualizar(Mensagem t) {
        try {
            String sql = "Update mensagem set conteudo = (?), usuario_id = (?), projeto_id = (?) where id = (?)";
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setString(1, t.getConteudo());
            stmt.setInt(2, t.getUsuario().getId());
            stmt.setInt(3, t.getProjeto().getId());
            stmt.setInt(4, t.getId());
            stmt.execute();
            stmt.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean excluir(Mensagem t) {
        try {
            String sql = "delete from mensagem where id = (?)";
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