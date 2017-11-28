package st.mape.jobs;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by Ramalho on 07/11/2017.
 */

public class Perfil extends AppCompatActivity {

    private FirebaseAuth auth;
    private Button btnConfirma, btnCancela;
    private ImageView fotoPerfil;
    private EditText txtNome, txtTel;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.perfil);

        auth = FirebaseAuth.getInstance();

        inicializaComponentes();
        eventoClicks();

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
    }

    private void inicializaComponentes() {

        btnCancela = (Button) findViewById(R.id.btnCancela);
        btnConfirma = (Button) findViewById(R.id.btnConfirma);
        fotoPerfil = (ImageView) findViewById(R.id.imageViewPerfil);
        txtNome = (EditText) findViewById(R.id.txtEditNome);
        txtTel = (EditText) findViewById(R.id.txtEditTel);

    }
}
