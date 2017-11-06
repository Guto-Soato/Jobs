package st.mape.jobs;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Matheus Rodrigues on 04/11/2017.
 */

public class ListaVagas extends AppCompatActivity{

    EditText editCNPJ,editCargo,editSalario,editDesc,editEmail;
    ListView listView;

    //variaveis p conexao c o banco
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    //relacionado a parte do select
    private List<Vaga> listVaga = new ArrayList<Vaga>();
    private ArrayAdapter<Vaga> arrayAdapterVaga;

    Vaga vagaSelecionada;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lista_vagas);



    }


}
