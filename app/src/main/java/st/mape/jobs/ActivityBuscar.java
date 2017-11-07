package st.mape.jobs;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by Matheus Rodrigues on 03/09/2017.
 */

public class ActivityBuscar extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{


    private Button btnBuscar;
    private TextView txtHeadName;
    private FirebaseAuth auth;
    private DrawerLayout mDrawer;
    private ActionBarDrawerToggle mToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tela_buscar);

        inicializaComponentes();
        eventoClicks();

        auth = FirebaseAuth.getInstance();

        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        mToggle = new ActionBarDrawerToggle(this, mDrawer, R.string.open, R.string.close);
        mDrawer.addDrawerListener(mToggle);
        mToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
}

    public boolean onCreateOptionsMenu (Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected (MenuItem item) {
        int id = item.getItemId();
        if (mToggle.onOptionsItemSelected(item)){
            return true;
        }
        if(id == R.id.menu_sair){
            logoutUser();
            LoginManager.getInstance().logOut();
        }
        return true;
    }

    public boolean onNavigationItemSelected (MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_sair) {
            logoutUser();
            LoginManager.getInstance().logOut();
        }
        if (id == R.id.nav_postos) {
            Intent chamaPosto = new Intent(ActivityBuscar.this, ListaPostos.class);
            startActivity(chamaPosto);
        }
        if (id == R.id.nav_conta) {
            Intent chamaPerfil = new Intent(ActivityBuscar.this, Perfil.class);
            startActivity(chamaPerfil);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //Método responsável pelos eventos de clicks nos botões
    private void eventoClicks() {

        btnBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent chamaTelaMapa = new Intent(ActivityBuscar.this, MapsActivity.class);
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

        btnBuscar = (Button) findViewById(R.id.btnBuscar);
        txtHeadName = (TextView) findViewById(R.id.txtHeadName);
    }


}
