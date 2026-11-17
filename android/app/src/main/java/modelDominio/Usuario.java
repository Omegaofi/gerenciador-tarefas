package modelDominio;

import java.io.Serializable;
import java.util.Date;

public class Usuario implements Serializable {

    private static final long serialVersionUID = 121L;
    private int id;
    private String nome;
    private String email;
    private String senha;
    private String biografia;
    private String tipo;
    private Date criadoEm;
    // Chaves do chat cifrado (opção A do projeto "Chat Cifrado"), todas em Base64.
    // A pública vai para os outros membros; a privada só viaja trancada com a senha do usuário.
    private String chavePublica;
    private String chavePrivadaCifrada;
    private String saltChave;
    private String ivChave;

    public Usuario(int id, String nome, String email, String senha, String biografia, String tipo, Date criadoEm) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.biografia = biografia;
        this.tipo = tipo;
        this.criadoEm = criadoEm;
    }

    public Usuario(String nome, String email, String senha, String biografia, String tipo) {
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.biografia = biografia;
        this.tipo = tipo;
    }

    public Usuario(String email, String senha) {
        this.email = email;
        this.senha = senha;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }
    public String getBiografia() { return biografia; }
    public void setBiografia(String biografia) { this.biografia = biografia; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public Date getCriadoEm() { return criadoEm; }
    public void setCriadoEm(Date criadoEm) { this.criadoEm = criadoEm; }
    public String getChavePublica() { return chavePublica; }
    public void setChavePublica(String chavePublica) { this.chavePublica = chavePublica; }
    public String getChavePrivadaCifrada() { return chavePrivadaCifrada; }
    public void setChavePrivadaCifrada(String chavePrivadaCifrada) { this.chavePrivadaCifrada = chavePrivadaCifrada; }
    public String getSaltChave() { return saltChave; }
    public void setSaltChave(String saltChave) { this.saltChave = saltChave; }
    public String getIvChave() { return ivChave; }
    public void setIvChave(String ivChave) { this.ivChave = ivChave; }
    public boolean temChaves() { return chavePublica != null && !chavePublica.isEmpty(); }

    @Override
    public String toString() {
        return "Usuario{" + "id=" + id + ", nome=" + nome + ", email=" + email + ", tipo=" + tipo + "}";
    }
}