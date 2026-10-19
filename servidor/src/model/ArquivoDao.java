package model;

import factory.Conexao;
import java.sql.*;
import java.util.ArrayList;
import modelDominio.Arquivo;
import modelDominio.Tarefa;
import modelDominio.Usuario;

public class ArquivoDao {

    private Connection con;

    public ArquivoDao() {
        con = Conexao.getConexao();
    }

    public ArrayList<Arquivo> getLista() {
        ArrayList<Arquivo> lista = new ArrayList();
        try {
            String sql = "SELECT a.*, u.nome nomeUsuario, t.titulo tituloTarefa "
                    + "FROM arquivo a "
                    + "INNER JOIN usuario u ON a.usuario_id = u.id "
                    + "LEFT JOIN tarefa t ON a.tarefa_id = t.id";
            PreparedStatement stmt = con.prepareStatement(sql);
            ResultSet res = stmt.executeQuery();
            while (res.next()) {
                Usuario usuario = new Usuario(
                        res.getInt("usuario_id"),
                        res.getString("nomeUsuario"),
                        null, null, null, null, null
                );
                Tarefa tarefa = (res.getObject("tarefa_id") != null)
                        ? new Tarefa(res.getInt("tarefa_id"), res.getString("tituloTarefa"), null, null, null, null, null)
                        : null;
                Arquivo arquivo = new Arquivo(
                        res.getInt("id"),
                        res.getString("nomeArquivo"),
                        res.getString("arquivoUrl"),
                        usuario,
                        tarefa,
                        res.getTimestamp("criado_em")
                );
                lista.add(arquivo);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return lista;
    }

    public boolean inserir(Arquivo t) {
        try {
            String sql = "insert into arquivo (nomeArquivo, arquivoUrl, usuario_id, tarefa_id) values (?, ?, ?, ?)";
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setString(1, t.getNomeArquivo());
            stmt.setString(2, t.getArquivoUrl());
            stmt.setInt(3, t.getUsuario().getId());
            if (t.getTarefa() != null) {
                stmt.setInt(4, t.getTarefa().getId());
            } else {
                stmt.setNull(4, java.sql.Types.INTEGER);
            }
            stmt.execute();
            stmt.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean atualizar(Arquivo t) {
        try {
            String sql = "Update arquivo set nomeArquivo = (?), arquivoUrl = (?), usuario_id = (?), tarefa_id = (?) where id = (?)";
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setString(1, t.getNomeArquivo());
            stmt.setString(2, t.getArquivoUrl());
            stmt.setInt(3, t.getUsuario().getId());
            if (t.getTarefa() != null) {
                stmt.setInt(4, t.getTarefa().getId());
            } else {
                stmt.setNull(4, java.sql.Types.INTEGER);
            }
            stmt.setInt(5, t.getId());
            stmt.execute();
            stmt.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean excluir(Arquivo t) {
        try {
            String sql = "delete from arquivo where id = (?)";
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