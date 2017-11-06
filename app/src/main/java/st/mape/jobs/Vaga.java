package st.mape.jobs;

/**
 * Created by Matheus Rodrigues on 04/11/2017.
 */

public class Vaga {

    private String id;
    private String CNPJ;
    private String cargo;
    private String salario;
    private String desc;
    private String email;


    public Vaga() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCNPJ() {
        return CNPJ;
    }

    public void setCNPJ(String CNPJ) {
        this.CNPJ = CNPJ;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public String getSalario() {
        return salario;
    }

    public void setSalario(String salario) {
        this.salario = salario;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() { // Quando aparecer na lista do app, sera mostrado o cargo
        return  cargo;
    }
}
