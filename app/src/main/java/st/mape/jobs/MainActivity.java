package st.mape.jobs;

import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class  MainActivity extends AppCompatActivity {

    // vaiáveis p receber os componentes do xml
    private EditText editEmail, editSenha;
    private Button btnLogar;
    private TextView txtCadastrar;

    private FirebaseAuth auth;
    //private static final String TAG = "Login";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inicializaComponentes();
        eventoClicks();
        eventoClicks2();

    }

    //Método responsável pelos eventos de clicks nos botões
    private void eventoClicks() {

        btnLogar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = editEmail.getText().toString().trim();
                String senha = editSenha.getText().toString().trim();
                login(email, senha); //
            }
        });
    }
    private void eventoClicks2(){
        txtCadastrar.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent t = new Intent(MainActivity.this, ActivityCadastro.class);
                startActivity(t);
            }

        });


    }

    // método para logar com email e senha - qdo o usuario clicar em "Entrar" no app, envia dados dele para cá
    private void login(String email, String senha) {
        auth.signInWithEmailAndPassword(email,senha)
            .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        // se logar, vai para a tela de busca
                        Intent chamaTelaBuscar = new Intent(MainActivity.this,ActivityBuscar.class);
                        startActivity(chamaTelaBuscar);
                    }else{
                        alerta("e-mail ou senha incorretos");
                    }
        }
        });
    }

    @Override
    public void onStart() {
        super.onStart();

        //iniciando a variavel de conexão
        auth = Conexao.getFirebaseAuth();
    }


    @Override
    public void onStop() {
        super.onStop();
    }

    //Método para exibir mensagem na tela
    private void alerta(String s) {
        Toast.makeText(MainActivity.this,s,Toast.LENGTH_SHORT).show();
    }

    // Método p inicializar as variaveis com os campos da tela
    private void inicializaComponentes(){

        editEmail = (EditText) findViewById(R.id.txtMail);
        editSenha = (EditText) findViewById(R.id.txtPass);
        btnLogar = (Button) findViewById(R.id.btnEnter);
        txtCadastrar = (TextView) findViewById(R.id.txtCad2);

    }
}
