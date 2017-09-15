package st.mape.jobs;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

public class  MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener{

    // vaiáveis p receber os componentes do xml
    private EditText editEmail, editSenha;
    private Button btnLogar;
    private TextView txtCadastrar;

    //variaveis p botão Google Sign in
    private SignInButton btnSignIn;
    private GoogleApiClient mGoogleApiClient;

    private FirebaseAuth auth;
    //private static final String TAG = "Login";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inicializaComponentes();
        eventoClicks();
        conectarGoogleApi();

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

        txtCadastrar.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent chamaTelaCadastro = new Intent(MainActivity.this, ActivityCadastro.class);
                startActivity(chamaTelaCadastro);
            }

        });
        //botão do Google
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });

    }

    private void signIn() {
        Intent i = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(i,1); // resultado p ver se está tudo ok
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==1){
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                if (result.isSuccess()){
                    GoogleSignInAccount account = result.getSignInAccount();
                    firebaseLoginGoogle(account);
                }
        }
    }

    private void firebaseLoginGoogle(GoogleSignInAccount account) {
        AuthCredential credencial = GoogleAuthProvider.getCredential(account.getIdToken(),null);
        auth.signInWithCredential(credencial)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Intent chamaTelaBuscar = new Intent(MainActivity.this,ActivityBuscar.class);
                            startActivity(chamaTelaBuscar);
                        }else{
                            alerta("Falha na Autenticação");
                        }
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

        //iniciando a variavel de conexão do Firebase
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
        btnSignIn = (SignInButton) findViewById(R.id.btnGoogle);

    }

    private void conectarGoogleApi(){

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this,this)
                .addApi(Auth.GOOGLE_SIGN_IN_API,gso)
                .build();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        alerta("Falha na conexão!");
    }


}
