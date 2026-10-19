package controller;

import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import model.UsuarioDao;
import model.ProjetoDao;
import model.MensagemDao;
import model.TarefaDao;
import model.ArquivoDao;
import model.EntregaTarefaDao;
import modelDominio.Usuario;
import modelDominio.Projeto;
import modelDominio.Mensagem;
import modelDominio.Tarefa;
import modelDominio.Arquivo;
import modelDominio.EntregaTarefa;

public class TratarClienteController extends Thread {

    private Socket cliente;
    private ObjectInput in;
    private ObjectOutput out;
    private int idUnico;

    public TratarClienteController(Socket cliente, int idUnico) {
        this.cliente = cliente;
        this.idUnico = idUnico;
        try {
            this.in = new ObjectInputStream(cliente.getInputStream());
            this.out = new ObjectOutputStream(cliente.getOutputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        String comando;
        boolean res;

        System.out.println("Aguardando comando do cliente:" + idUnico);
        try {
            comando = (String) in.readObject();
            while (!comando.equalsIgnoreCase("fim")) {
                System.out.println("Cliente " + idUnico + " enviou comando: " + comando);
                switch (comando) {

                    case "UsuarioLogin":
                        out.writeObject("ok");
                        Usuario user = (Usuario) in.readObject();
                        System.out.println(user);
                        UsuarioDao usuarioDao = new UsuarioDao();
                        Usuario userLogado = usuarioDao.login(user);
                        out.writeObject(userLogado);
                        break;

                    case "UsuarioLista":
                        out.writeObject("ok");
                        UsuarioDao uDao = new UsuarioDao();
                        ArrayList<Usuario> listaUsuario = uDao.getLista();
                        out.writeObject(listaUsuario);
                        break;
                    case "UsuarioInserir":
                        out.writeObject("OK");
                        Usuario u = (Usuario) in.readObject();
                        System.out.println(u);
                        uDao = new UsuarioDao();
                        res = uDao.inserir(u);
                        out.writeObject(res);
                        break;
                    case "UsuarioAtualizar":
                        out.writeObject("OK");
                        u = (Usuario) in.readObject();
                        System.out.println(u);
                        uDao = new UsuarioDao();
                        res = uDao.atualizar(u);
                        out.writeObject(res);
                        break;
                    case "UsuarioExcluir":
                        out.writeObject("OK");
                        u = (Usuario) in.readObject();
                        System.out.println(u);
                        uDao = new UsuarioDao();
                        res = uDao.excluir(u);
                        out.writeObject(res);
                        break;

                    case "ProjetoLista":
                        out.writeObject("ok");
                        ProjetoDao projetoDao = new ProjetoDao();
                        ArrayList<Projeto> listaProjeto = projetoDao.getLista();
                        out.writeObject(listaProjeto);
                        break;
                    case "ProjetoListaPorCriador":
                        out.writeObject("ok");
                        int criadorProjetoId = (Integer) in.readObject();
                        projetoDao = new ProjetoDao();
                        out.writeObject(projetoDao.getListaPorCriador(criadorProjetoId));
                        break;
                    case "ProjetoInserir":
                        out.writeObject("OK");
                        Projeto p = (Projeto) in.readObject();
                        System.out.println(p);
                        projetoDao = new ProjetoDao();
                        res = projetoDao.inserir(p);
                        out.writeObject(res);
                        break;
                    case "ProjetoAtualizar":
                        out.writeObject("OK");
                        p = (Projeto) in.readObject();
                        System.out.println(p);
                        projetoDao = new ProjetoDao();
                        res = projetoDao.atualizar(p);
                        out.writeObject(res);
                        break;
                    case "ProjetoExcluir":
                        out.writeObject("OK");
                        p = (Projeto) in.readObject();
                        System.out.println(p);
                        projetoDao = new ProjetoDao();
                        res = projetoDao.excluir(p);
                        out.writeObject(res);
                        break;

                    case "MensagemLista":
                        out.writeObject("ok");
                        MensagemDao mensagemDao = new MensagemDao();
                        ArrayList<Mensagem> listaMensagem = mensagemDao.getLista();
                        out.writeObject(listaMensagem);
                        break;
                    case "MensagemInserir":
                        out.writeObject("OK");
                        Mensagem m = (Mensagem) in.readObject();
                        System.out.println(m);
                        mensagemDao = new MensagemDao();
                        res = mensagemDao.inserir(m);
                        out.writeObject(res);
                        break;
                    case "MensagemAtualizar":
                        out.writeObject("OK");
                        m = (Mensagem) in.readObject();
                        System.out.println(m);
                        mensagemDao = new MensagemDao();
                        res = mensagemDao.atualizar(m);
                        out.writeObject(res);
                        break;
                    case "MensagemExcluir":
                        out.writeObject("OK");
                        m = (Mensagem) in.readObject();
                        System.out.println(m);
                        mensagemDao = new MensagemDao();
                        res = mensagemDao.excluir(m);
                        out.writeObject(res);
                        break;

                    case "TarefaLista":
                        out.writeObject("ok");
                        TarefaDao tarefaDao = new TarefaDao();
                        ArrayList<Tarefa> listaTarefa = tarefaDao.getLista();
                        out.writeObject(listaTarefa);
                        break;
                    case "TarefaListaPorResponsavel":
                        out.writeObject("ok");
                        int responsavelId = (Integer) in.readObject();
                        tarefaDao = new TarefaDao();
                        out.writeObject(tarefaDao.getListaPorResponsavel(responsavelId));
                        break;
                    case "TarefaListaPorCriador":
                        out.writeObject("ok");
                        int criadorTarefaId = (Integer) in.readObject();
                        tarefaDao = new TarefaDao();
                        out.writeObject(tarefaDao.getListaPorCriador(criadorTarefaId));
                        break;
                    case "TarefaInserir":
                        out.writeObject("OK");
                        Tarefa t = (Tarefa) in.readObject();
                        System.out.println(t);
                        tarefaDao = new TarefaDao();
                        res = tarefaDao.inserir(t);
                        out.writeObject(res);
                        break;
                    case "TarefaAtualizar":
                        out.writeObject("OK");
                        t = (Tarefa) in.readObject();
                        System.out.println(t);
                        tarefaDao = new TarefaDao();
                        res = tarefaDao.atualizar(t);
                        out.writeObject(res);
                        break;
                    case "TarefaExcluir":
                        out.writeObject("OK");
                        t = (Tarefa) in.readObject();
                        System.out.println(t);
                        tarefaDao = new TarefaDao();
                        res = tarefaDao.excluir(t);
                        out.writeObject(res);
                        break;

                    case "ArquivoLista":
                        out.writeObject("ok");
                        ArquivoDao arquivoDao = new ArquivoDao();
                        ArrayList<Arquivo> listaArquivo = arquivoDao.getLista();
                        out.writeObject(listaArquivo);
                        break;
                    case "ArquivoInserir":
                        out.writeObject("OK");
                        Arquivo a = (Arquivo) in.readObject();
                        System.out.println(a);
                        arquivoDao = new ArquivoDao();
                        res = arquivoDao.inserir(a);
                        out.writeObject(res);
                        break;
                    case "ArquivoAtualizar":
                        out.writeObject("OK");
                        a = (Arquivo) in.readObject();
                        System.out.println(a);
                        arquivoDao = new ArquivoDao();
                        res = arquivoDao.atualizar(a);
                        out.writeObject(res);
                        break;
                    case "ArquivoExcluir":
                        out.writeObject("OK");
                        a = (Arquivo) in.readObject();
                        System.out.println(a);
                        arquivoDao = new ArquivoDao();
                        res = arquivoDao.excluir(a);
                        out.writeObject(res);
                        break;

                    case "EntregaTarefaLista":
                        out.writeObject("ok");
                        EntregaTarefaDao entregaDao = new EntregaTarefaDao();
                        ArrayList<EntregaTarefa> listaEntrega = entregaDao.getLista();
                        out.writeObject(listaEntrega);
                        break;
                    case "EntregaTarefaListaPorCriador":
                        out.writeObject("ok");
                        int criadorEntregaId = (Integer) in.readObject();
                        entregaDao = new EntregaTarefaDao();
                        out.writeObject(entregaDao.getListaPorCriador(criadorEntregaId));
                        break;
                    case "EntregaTarefaListaPorResponsavel":
                        out.writeObject("ok");
                        int responsavelEntregaId = (Integer) in.readObject();
                        entregaDao = new EntregaTarefaDao();
                        out.writeObject(entregaDao.getListaPorResponsavel(responsavelEntregaId));
                        break;
                    case "EntregaTarefaEnviar":
                        out.writeObject("OK");
                        EntregaTarefa entregaEnviar = (EntregaTarefa) in.readObject();
                        System.out.println(entregaEnviar);
                        entregaDao = new EntregaTarefaDao();
                        out.writeObject(entregaDao.enviar(entregaEnviar));
                        break;
                    case "EntregaTarefaAvaliar":
                        out.writeObject("OK");
                        EntregaTarefa entregaAvaliar = (EntregaTarefa) in.readObject();
                        System.out.println(entregaAvaliar);
                        entregaDao = new EntregaTarefaDao();
                        out.writeObject(entregaDao.avaliar(entregaAvaliar));
                        break;
                    case "EntregaTarefaInserir":
                        out.writeObject("OK");
                        EntregaTarefa e = (EntregaTarefa) in.readObject();
                        System.out.println(e);
                        entregaDao = new EntregaTarefaDao();
                        res = entregaDao.inserir(e);
                        out.writeObject(res);
                        break;
                    case "EntregaTarefaAtualizar":
                        out.writeObject("OK");
                        e = (EntregaTarefa) in.readObject();
                        System.out.println(e);
                        entregaDao = new EntregaTarefaDao();
                        res = entregaDao.atualizar(e);
                        out.writeObject(res);
                        break;
                    case "EntregaTarefaExcluir":
                        out.writeObject("OK");
                        e = (EntregaTarefa) in.readObject();
                        System.out.println(e);
                        entregaDao = new EntregaTarefaDao();
                        res = entregaDao.excluir(e);
                        out.writeObject(res);
                        break;

                    default:
                        System.out.println("Comando inválido");
                }
                comando = (String) in.readObject();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}