package st.mape.jobs;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by Matheus Rodrigues on 03/09/2017.
 */

public class ActivityBuscar extends AppCompatActivity {


    private Button btnVoltar, btnBuscar;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tela_buscar);

        inicializaComponentes();
        eventoClicks();

        auth = FirebaseAuth.getInstance();

}

    public boolean onCreateOptionsMenu (Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected (MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.menu_sair){
            logoutUser();
            LoginManager.getInstance().logOut();
        }
        return true;
    }

    //Método responsável pelos eventos de clicks nos botões
    private void eventoClicks() {

        btnVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent chamaTelaMapa = new Intent(ActivityBuscar.this,ActivityMapa.class);
                startActivity(chamaTelaMapa);
            }
        });
    }

    private void logoutUser(){
        auth.signOut();
        if (auth.getCurrentUser() == null){
            Intent chamaLogin = new Intent(ActivityBuscar.this, MainActivity.class);
            startActivity(chamaLogin);
            finish();
        }
    }

    // Método p inicializar as variaveis com os componentes da tela
    private void inicializaComponentes(){

        btnVoltar = (Button) findViewById(R.id.btnVoltar);
        btnBuscar = (Button) findViewById(R.id.btnBuscar);

    }


}
