package st.mape.jobs;

import android.support.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by Matheus Rodrigues on 03/09/2017.
 */

public class Conexao {

    private static FirebaseAuth mAuth;                           // variável global p pegar instancia do firebaseAuth
    private static FirebaseAuth.AuthStateListener mAuthListener; //Listener para monitorar as mudancas no estado da autenticação
    private static FirebaseUser firebaseUser;


    public static FirebaseAuth getFirebaseAuth(){
        if (mAuth == null){
            inicializarFirebaseAuth();

        }
        return mAuth;
    }

    public static void inicializarFirebaseAuth(){

        mAuth = FirebaseAuth.getInstance(); //inicia a variavel com a instancia FirebaseAuth
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                FirebaseUser user = firebaseAuth.getCurrentUser(); //pega as infos do usu corrente
                if(user != null){  //se não for nulo, passar essas infos do usu para a variavel
                    firebaseUser = user;

                }
            }
        };
        mAuth.addAuthStateListener(mAuthListener);
        }


    private static FirebaseUser getFirebaseUser() {
        return firebaseUser;
    }

    private static void logOut(){
        mAuth.signOut();
    }
}
