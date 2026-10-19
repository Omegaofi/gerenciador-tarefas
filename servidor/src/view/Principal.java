package view;

import controller.TratarClienteController;
import factory.Conexao;
import java.net.ServerSocket;
import java.net.Socket;

public class Principal {

    public static void main(String[] args) {
        try {
            if (Conexao.getConexao() != null) {
                System.out.println("Conectado ao bd!");
            }
            ServerSocket servidor = new ServerSocket(4321);
            System.out.println("Servidor inicializado! Aguardando conexões");
            int idUnico = 0;
            while (true) {
                Socket cliente = servidor.accept();
                System.out.println("Novo cliente conectado com id:" + idUnico + ", cliente " + cliente);
                idUnico++;
                TratarClienteController trataCliente = new TratarClienteController(cliente, idUnico);
                trataCliente.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}