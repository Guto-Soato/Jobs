package st.mape.jobs;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.login.LoginManager;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Matheus Rodrigues on 06/11/2017.
 */

public class TelaPrincipalVagas extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    ListView listV_vagas;
    private TextView emailMenu, nomeMenu;

    //variaveis p conexao c o banco
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    private FirebaseAuth auth;

    //relacionado a parte do select
    private List<Vaga> listV = new ArrayList<Vaga>();
    private ArrayAdapter<Vaga> arrayAdapterV;
    private List<Pessoa> listPessoa = new ArrayList<Pessoa>();

    Vaga vagaSelecionada;

    private DrawerLayout mDrawer;
    private ActionBarDrawerToggle mToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tela_principal_vagas);

        inicializaComponentes();
        inicializaFirebase();
        eventoLista();
        eventoFirebase();

        auth = FirebaseAuth.getInstance();

        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        mToggle = new ActionBarDrawerToggle(this, mDrawer, R.string.open, R.string.close);
        mDrawer.addDrawerListener(mToggle);
        mToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Update e Delete - pega o indice da pessoa selecionada na lista
        listV_vagas.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                vagaSelecionada = (Vaga)adapterView.getItemAtPosition(i);
                vagaSelecionada.getCargo();
            }

        });
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
            Intent chamaPosto = new Intent(TelaPrincipalVagas.this, ListaPostos.class);
            startActivity(chamaPosto);
        }
        if (id == R.id.nav_conta) {
            Intent chamaPerfil = new Intent(TelaPrincipalVagas.this, Perfil.class);
            startActivity(chamaPerfil);
            finish();
        }
        if (id == R.id.nav_vaga) {
            Intent chamaVaga = new Intent(TelaPrincipalVagas.this, ListaVagas.class);
            startActivity(chamaVaga);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void inicializaFirebase() {
        FirebaseApp.initializeApp(TelaPrincipalVagas.this);
        if (firebaseDatabase == FirebaseDatabase.getInstance()){
            firebaseDatabase.setPersistenceEnabled(true);   //permite salvar e alterar os arqs na nuvem e no app
        } else {
            firebaseDatabase = FirebaseDatabase.getInstance();

        }
        databaseReference = firebaseDatabase.getReference();
    }

    private void eventoLista() {
        databaseReference.child("Vaga").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listV.clear();
                for (DataSnapshot objSnapShot:dataSnapshot.getChildren()){
                    Vaga v = objSnapShot.getValue(Vaga.class);
                    listV.add(v);
                }
                arrayAdapterV = new ArrayAdapter<Vaga>(TelaPrincipalVagas.this,
                        android.R.layout.simple_list_item_1,listV);
                listV_vagas.setAdapter(arrayAdapterV);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void logoutUser(){
        auth.signOut();
        if (auth.getCurrentUser() == null){
            Intent chamaLogin = new Intent(TelaPrincipalVagas.this, MainActivity.class);
            startActivity(chamaLogin);
            finish();
        }
    }

    private void eventoFirebase(){
        databaseReference.child("Perfil").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnap:dataSnapshot.getChildren()){
                    Pessoa p = dataSnap.getValue(Pessoa.class);
                    nomeMenu.setText(p.getNome().toString());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void inicializaComponentes() {
        listV_vagas = (ListView)findViewById(R.id.listV_vagas);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        nomeMenu = (TextView)headerView.findViewById(R.id.txtNomeMenu);
        emailMenu = (TextView)headerView.findViewById(R.id.txtEmailMenu);
    }
}
