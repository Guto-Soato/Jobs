package st.mape.jobs;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Ramalho on 07/11/2017.
 */

public class Perfil extends AppCompatActivity {

    private FirebaseDatabase dbUser;
    private DatabaseReference dbRef;
    private FirebaseAuth auth;
    private Button btnConfirma, btnCancela;
    private ImageView fotoPerfil;
    private EditText txtNome, txtTel;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.perfil);

        auth = FirebaseAuth.getInstance();

        inicializaFirebase();
        inicializaComponentes();
        eventoClicks();

    }

    private void inicializaFirebase() {
        FirebaseApp.initializeApp(Perfil.this);
        dbUser = FirebaseDatabase.getInstance();
        dbRef = dbUser.getReference();
    }

    private void eventoClicks() {
        btnCancela.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent chamaTelaPrincipal = new Intent(Perfil.this, TelaPrincipalVagas.class);
                startActivity(chamaTelaPrincipal);
                finish();
            }
        });
        btnConfirma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                atualiza();
                alerta("Salvando dados...");
                Intent chamaTelaPrincipal = new Intent(Perfil.this, TelaPrincipalVagas.class);
                startActivity(chamaTelaPrincipal);
                finish();
            }
        });
    }

    private void atualiza() {
        Pessoa p = new Pessoa();
        p.setNome(txtNome.getText().toString());
        p.setTelefone(txtTel.getText().toString());
        dbRef.child("Perfil").child(txtNome.getText().toString()).setValue(p);
    }

    private void alerta(String s) {
        Toast.makeText(Perfil.this,s,Toast.LENGTH_SHORT).show();
    }

    private void inicializaComponentes() {

        btnCancela = (Button) findViewById(R.id.btnCancela);
        btnConfirma = (Button) findViewById(R.id.btnConfirma);
        fotoPerfil = (ImageView) findViewById(R.id.imageViewPerfil);
        txtNome = (EditText) findViewById(R.id.txtEditNome);
        txtTel = (EditText) findViewById(R.id.txtEditTel);

    }
}
