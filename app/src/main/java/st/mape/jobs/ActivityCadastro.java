package st.mape.jobs;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Ramalho on 11/09/2017.
 */

public class ActivityCadastro extends AppCompatActivity {

    // variáveis p receber os componentes do xml
    private EditText editPessoa, editEmail, editSenha, editConfSenha;
    private Button btnCadastrar, btnVoltar;
    private ProgressDialog dialog;

    private FirebaseAuth mAuth;
    private FirebaseDatabase dbUser;
    private DatabaseReference dbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cadastro_pessoa);

        // recebe o click do botao Pessoa, na tela de cadastro
        inicializaComponentes();
        inicializaFirebase();
        eventoClicks();

    }

    private void inicializaFirebase() {
        FirebaseApp.initializeApp(ActivityCadastro.this);
        dbUser = FirebaseDatabase.getInstance();
        dbRef = dbUser.getReference();
    }

    //Método responsável pelos eventos de clicks nos botões
    private void eventoClicks() {

        btnCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = editEmail.getText().toString().trim();
                String senha = editSenha.getText().toString().trim();
                String confsenha = editConfSenha.getText().toString().trim();
                if (confsenha.equals(senha)){
                    dialog = ProgressDialog.show(ActivityCadastro.this,"Jobs","Realizando cadastro, aguarde", false, true);
                    dialog.setIcon(R.mipmap.ic_launcher_round);
                    dialog.setCancelable(false);
                    cadastraUsuario(email, senha);
                    atualiza();

                    new Thread() {
                        public void run () {
                            try {
                                Thread.sleep(10000);
                                //dialog.dismiss();
                            } catch (Exception e) {

                            }
                        }
                    }.start();
                } else {
                    alerta("As senhas são diferentes!");
                }
            }
        });


        btnVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent chamaLogin = new Intent(ActivityCadastro.this, MainActivity.class);
                startActivity(chamaLogin);
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
                .addOnCompleteListener(ActivityCadastro.this, new OnCompleteListener<AuthResult>(){
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task){
                        if (task.isSuccessful()){
                            dialog.dismiss();
                            alerta("Cadastro realizado com sucesso!");
                            Intent chamaTelaBuscar = new Intent(ActivityCadastro.this,TelaPrincipalVagas.class);
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
        Toast.makeText(ActivityCadastro.this,s,Toast.LENGTH_SHORT).show();
    }

    private void atualiza() {
        Pessoa p = new Pessoa();
        p.setNome(editPessoa.getText().toString());
        dbRef.child("Perfil").child(editPessoa.getText().toString()).setValue(p);
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