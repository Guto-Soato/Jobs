package st.mape.jobs;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by gustavo.soato on 03/10/2017.
 */

public class ActivityMapa extends AppCompatActivity {

    private FragmentManager fragmentManager;
    private FirebaseAuth auth;



    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tela_mapa);

        fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
     //   transaction.add(R.id.map, new MapsActivity(), "Mapa");
        transaction.commitAllowingStateLoss();
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

    private void logoutUser() {
        auth.signOut();
        if (auth.getCurrentUser() == null) {
            Intent chamaLogin = new Intent(ActivityMapa.this, MainActivity.class);
            startActivity(chamaLogin);
            finish();
        }
    }
}
