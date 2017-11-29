package st.mape.jobs;

/**
 * Created by gustavo.soato on 29/11/2017.
 */

public class Pessoa {

    private String nome;
    private String telefone;
    private String email;

    public Pessoa () {

    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String toString () {
        return nome;
    }
}
