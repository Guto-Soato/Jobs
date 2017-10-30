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
import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by gustavo.soato on 16/10/2017.
 */

public class ActivityRecupSenha extends AppCompatActivity{
    private EditText editEmail;
    private Button btnCancela, btnConfirma;
    private FirebaseAuth auth;

    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recup_senha);

        inicializaComponentes();
        eventoClicks();
        auth = FirebaseAuth.getInstance();
    }

    private void eventoClicks() {

        btnConfirma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetSenha (editEmail.getText().toString());
            }
        });

        btnCancela.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent chamaTelaLogin = new Intent(ActivityRecupSenha.this, MainActivity.class);
                startActivity(chamaTelaLogin);
                finish();
            }
        });
    }

    private void resetSenha(final String email) {
        auth.sendPasswordResetEmail(email).addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    alerta("Instruções enviada com sucesso para " + email);
                    finish();
                } else {
                    alerta("Erro ao tentar recuperar senha");
                }
            }
        });
    }

    private void inicializaComponentes(){
        editEmail = (EditText) findViewById(R.id.txtRecupEmail);
        btnCancela = (Button) findViewById(R.id.btnCancela);
        btnConfirma = (Button) findViewById(R.id.btnConfirma);
    }

    private void alerta(String s) {
        Toast.makeText(ActivityRecupSenha.this,s,Toast.LENGTH_SHORT).show();
    }
}
