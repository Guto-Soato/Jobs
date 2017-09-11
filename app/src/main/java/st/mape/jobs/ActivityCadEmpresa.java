package st.mape.jobs;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by Matheus Rodrigues on 04/09/2017.
 */

public class ActivityCadEmpresa extends AppCompatActivity {

    // vaiáveis p receber os componentes do xml
    private EditText editEmpresa, editEmail, editSenha, editConfSenha;
    private Button btnCadastrar, btnVoltar;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cadastro_empresa);

        // recebe o click do botao Empressa, na tela de cadastro
        Intent chamaCadEmpresa = getIntent();

        inicializaComponentes();
        eventoClicks2();

    }

    //Método responsável pelos eventos de clicks nos botões
    private void eventoClicks() {

        btnCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = editEmail.getText().toString().trim();
                String senha = editSenha.getText().toString().trim();
                cadastraUsuario(email, senha); //
            }
        });
    }

    private void eventoClicks2() {

        btnVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void onStart(){
        super.onStart();

        mAuth = Conexao.getFirebaseAuth();
    }

    private void cadastraUsuario(String email, String senha){
        mAuth.createUserWithEmailAndPassword(email,senha)
                .addOnCompleteListener(ActivityCadEmpresa.this, new OnCompleteListener<AuthResult>(){
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task){
                    if (task.isSuccessful()){
                        alerta("Cadastro realizado com sucesso!");
                        Intent chamaTelaBuscar = new Intent(ActivityCadEmpresa.this,ActivityBuscar.class);
                        startActivity(chamaTelaBuscar);
                        finish();
                    }else{
                        alerta("Erro no cadastro");
                    }

            }

        });

    }



    //Método para exibir mensagem na tela
    private void alerta(String s) {
        Toast.makeText(ActivityCadEmpresa.this,s,Toast.LENGTH_SHORT).show();
    }


    // Método p inicializar as variaveis com os campos da tela
    private void inicializaComponentes(){

        editEmpresa = (EditText) findViewById(R.id.txtEditNomeEmp);
        editEmail = (EditText) findViewById(R.id.txtEditMail);
        editSenha = (EditText) findViewById(R.id.txtEditSenha);
        editConfSenha = (EditText) findViewById(R.id.txtEditConfSenha);
        btnCadastrar = (Button) findViewById(R.id.btnCad);
        btnVoltar = (Button) findViewById(R.id.btnVoltar);

    }
}