package modelDominio;

import java.io.Serializable;
import java.util.Date;

/**
 * Inscrição de um participante em um projeto (tabela projeto_participante).
 * A chave é o par projeto + usuário.
 * status: "pendente" (aguardando o criador), "aceito" ou "recusado".
 *
 * <p>Na tela Explorar projetos, cada projeto disponível vem dentro de uma Inscricao:
 * status null quer dizer que o usuário ainda não se inscreveu.</p>
 */
public class Inscricao implements Serializable {

    private static final long serialVersionUID = 127L;
    public static final String PENDENTE = "pendente";
    public static final String ACEITO = "aceito";
    public static final String RECUSADO = "recusado";

    private Projeto projeto;
    private Usuario usuario;
    private String status;
    private Date dataInscricao;

    // Cria uma inscrição (pedido de entrada em um projeto)
    public Inscricao(Projeto projeto, Usuario usuario, String status, Date dataInscricao) {
        this.projeto = projeto;
        this.usuario = usuario;
        this.status = status;
        this.dataInscricao = dataInscricao;
    }

    // Getters e setters
    public Projeto getProjeto() { return projeto; }
    public void setProjeto(Projeto projeto) { this.projeto = projeto; }
    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Date getDataInscricao() { return dataInscricao; }
    public void setDataInscricao(Date dataInscricao) { this.dataInscricao = dataInscricao; }

    public boolean isPendente() { return PENDENTE.equals(status); }
    public boolean isRecusado() { return RECUSADO.equals(status); }

    // Texto usado para mostrar o objeto no console/depuração
    @Override
    public String toString() {
        return "Inscricao{" + "projeto=" + (projeto != null ? projeto.getTitulo() : null)
                + ", usuario=" + (usuario != null ? usuario.getNome() : null) + ", status=" + status + "}";
    }
}
