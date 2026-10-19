package gerenciador.tarefas.cliente.controller;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import modelDominio.Arquivo;
import modelDominio.EntregaTarefa;
import modelDominio.Mensagem;
import modelDominio.Projeto;
import modelDominio.Tarefa;
import modelDominio.Usuario;

public class ConexaoController {

    private final ObjectInputStream in;
    private final ObjectOutputStream out;
    private Usuario usuarioLogado;

    public ConexaoController(ObjectInputStream in, ObjectOutputStream out) {
        this.in = in;
        this.out = out;
    }

    public Usuario getUsuarioLogado() {
        return usuarioLogado;
    }

    public void setUsuarioLogado(Usuario usuarioLogado) {
        this.usuarioLogado = usuarioLogado;
    }

    // Criador = idealizador do projeto; Participante = colaborador
    public boolean isCriador() {
        return usuarioLogado != null && "criador".equals(usuarioLogado.getTipo());
    }

    // Envia um comando que recebe o id de um usuário e devolve uma lista
    private <T> ArrayList<T> listaPorUsuario(String comando, int usuarioId) {
        try {
            out.writeObject(comando);
            in.readObject();
            out.writeObject(usuarioId);
            ArrayList<T> lista = (ArrayList<T>) in.readObject();
            return lista != null ? lista : new ArrayList<>();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public ArrayList<Projeto> projetoListaPorCriador(int criadorId) {
        return listaPorUsuario("ProjetoListaPorCriador", criadorId);
    }

    public ArrayList<Tarefa> tarefaListaPorResponsavel(int usuarioId) {
        return listaPorUsuario("TarefaListaPorResponsavel", usuarioId);
    }

    public ArrayList<Tarefa> tarefaListaPorCriador(int criadorId) {
        return listaPorUsuario("TarefaListaPorCriador", criadorId);
    }

    public ArrayList<EntregaTarefa> entregaTarefaListaPorCriador(int criadorId) {
        return listaPorUsuario("EntregaTarefaListaPorCriador", criadorId);
    }

    public ArrayList<EntregaTarefa> entregaTarefaListaPorResponsavel(int usuarioId) {
        return listaPorUsuario("EntregaTarefaListaPorResponsavel", usuarioId);
    }

    public boolean entregaTarefaEnviar(EntregaTarefa entrega) {
        try {
            out.writeObject("EntregaTarefaEnviar");
            in.readObject();
            out.writeObject(entrega);
            return (boolean) in.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean entregaTarefaAvaliar(EntregaTarefa entrega) {
        try {
            out.writeObject("EntregaTarefaAvaliar");
            in.readObject();
            out.writeObject(entrega);
            return (boolean) in.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public Usuario usuarioLogin(Usuario usuario) {
        try {
            out.writeObject("UsuarioLogin");
            in.readObject();
            out.writeObject(usuario);
            return (Usuario) in.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public ArrayList<Usuario> usuarioLista() {
        try {
            out.writeObject("UsuarioLista");
            in.readObject();
            ArrayList<Usuario> lista = (ArrayList<Usuario>) in.readObject();
            return lista != null ? lista : new ArrayList<>();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public boolean usuarioInserir(Usuario usuario) {
        try {
            out.writeObject("UsuarioInserir");
            in.readObject();
            out.writeObject(usuario);
            return (boolean) in.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean usuarioAtualizar(Usuario usuario) {
        try {
            out.writeObject("UsuarioAtualizar");
            in.readObject();
            out.writeObject(usuario);
            return (boolean) in.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean usuarioExcluir(Usuario usuario) {
        try {
            out.writeObject("UsuarioExcluir");
            in.readObject();
            out.writeObject(usuario);
            return (boolean) in.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public ArrayList<Projeto> projetoLista() {
        try {
            out.writeObject("ProjetoLista");
            in.readObject();
            ArrayList<Projeto> lista = (ArrayList<Projeto>) in.readObject();
            return lista != null ? lista : new ArrayList<>();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public boolean projetoInserir(Projeto projeto) {
        try {
            out.writeObject("ProjetoInserir");
            in.readObject();
            out.writeObject(projeto);
            return (boolean) in.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean projetoAtualizar(Projeto projeto) {
        try {
            out.writeObject("ProjetoAtualizar");
            in.readObject();
            out.writeObject(projeto);
            return (boolean) in.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean projetoExcluir(Projeto projeto) {
        try {
            out.writeObject("ProjetoExcluir");
            in.readObject();
            out.writeObject(projeto);
            return (boolean) in.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public ArrayList<Mensagem> mensagemLista() {
        try {
            out.writeObject("MensagemLista");
            in.readObject();
            ArrayList<Mensagem> lista = (ArrayList<Mensagem>) in.readObject();
            return lista != null ? lista : new ArrayList<>();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public boolean mensagemInserir(Mensagem mensagem) {
        try {
            out.writeObject("MensagemInserir");
            in.readObject();
            out.writeObject(mensagem);
            return (boolean) in.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean mensagemAtualizar(Mensagem mensagem) {
        try {
            out.writeObject("MensagemAtualizar");
            in.readObject();
            out.writeObject(mensagem);
            return (boolean) in.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean mensagemExcluir(Mensagem mensagem) {
        try {
            out.writeObject("MensagemExcluir");
            in.readObject();
            out.writeObject(mensagem);
            return (boolean) in.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public ArrayList<Tarefa> tarefaLista() {
        try {
            out.writeObject("TarefaLista");
            in.readObject();
            ArrayList<Tarefa> lista = (ArrayList<Tarefa>) in.readObject();
            return lista != null ? lista : new ArrayList<>();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public boolean tarefaInserir(Tarefa tarefa) {
        try {
            out.writeObject("TarefaInserir");
            in.readObject();
            out.writeObject(tarefa);
            return (boolean) in.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean tarefaAtualizar(Tarefa tarefa) {
        try {
            out.writeObject("TarefaAtualizar");
            in.readObject();
            out.writeObject(tarefa);
            return (boolean) in.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean tarefaExcluir(Tarefa tarefa) {
        try {
            out.writeObject("TarefaExcluir");
            in.readObject();
            out.writeObject(tarefa);
            return (boolean) in.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public ArrayList<Arquivo> arquivoLista() {
        try {
            out.writeObject("ArquivoLista");
            in.readObject();
            ArrayList<Arquivo> lista = (ArrayList<Arquivo>) in.readObject();
            return lista != null ? lista : new ArrayList<>();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public boolean arquivoInserir(Arquivo arquivo) {
        try {
            out.writeObject("ArquivoInserir");
            in.readObject();
            out.writeObject(arquivo);
            return (boolean) in.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean arquivoAtualizar(Arquivo arquivo) {
        try {
            out.writeObject("ArquivoAtualizar");
            in.readObject();
            out.writeObject(arquivo);
            return (boolean) in.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean arquivoExcluir(Arquivo arquivo) {
        try {
            out.writeObject("ArquivoExcluir");
            in.readObject();
            out.writeObject(arquivo);
            return (boolean) in.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public ArrayList<EntregaTarefa> entregaTarefaLista() {
        try {
            out.writeObject("EntregaTarefaLista");
            in.readObject();
            ArrayList<EntregaTarefa> lista = (ArrayList<EntregaTarefa>) in.readObject();
            return lista != null ? lista : new ArrayList<>();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public boolean entregaTarefaInserir(EntregaTarefa entrega) {
        try {
            out.writeObject("EntregaTarefaInserir");
            in.readObject();
            out.writeObject(entrega);
            return (boolean) in.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean entregaTarefaAtualizar(EntregaTarefa entrega) {
        try {
            out.writeObject("EntregaTarefaAtualizar");
            in.readObject();
            out.writeObject(entrega);
            return (boolean) in.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean entregaTarefaExcluir(EntregaTarefa entrega) {
        try {
            out.writeObject("EntregaTarefaExcluir");
            in.readObject();
            out.writeObject(entrega);
            return (boolean) in.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}