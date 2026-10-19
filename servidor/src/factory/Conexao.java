package factory;

import java.sql.*;

public class Conexao {

    private static Connection con;

    public static synchronized Connection getConexao() {
        try {
            if (con == null || con.isClosed()) {
                String url = "jdbc:mysql://localhost:3306/gerenciador_projetos";
                String usuario = "root";
                String senha = "";
                con = DriverManager.getConnection(url, usuario, senha);
            }
            return con;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}