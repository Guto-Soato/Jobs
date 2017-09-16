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
 * Created by Ramalho on 11/09/2017.
 */

public class ActivityCadPessoa extends AppCompatActivity {

    // variáveis p receber os componentes do xml
    private EditText editPessoa, editEmail, editSenha, editConfSenha;
    private Button btnCadastrar, btnVoltar;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cadastro_pessoa);

        // recebe o click do botao Pessoa, na tela de cadastro


        inicializaComponentes();
        eventoClicks();

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
                .addOnCompleteListener(ActivityCadPessoa.this, new OnCompleteListener<AuthResult>(){
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task){
                        if (task.isSuccessful()){
                            alerta("Cadastro realizado com sucesso!");
                            Intent chamaTelaBuscar = new Intent(ActivityCadPessoa.this,ActivityBuscar.class);
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
        Toast.makeText(ActivityCadPessoa.this,s,Toast.LENGTH_SHORT).show();
    }


    // Método p inicializar as variaveis com os campos da tela
    private void inicializaComponentes(){

        editPessoa = (EditText) findViewById(R.id.txtEditNomePes);
        editEmail = (EditText) findViewById(R.id.txtEditMail);
        editSenha = (EditText) findViewById(R.id.txtEditSenha);
        editConfSenha = (EditText) findViewById(R.id.txtEditConfSenha);
        btnCadastrar = (Button) findViewById(R.id.btnCad);
        btnVoltar = (Button) findViewById(R.id.btnVoltar);

    }
}