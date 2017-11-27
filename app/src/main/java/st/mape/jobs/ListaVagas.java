package st.mape.jobs;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Matheus Rodrigues on 04/11/2017.
 */

public class ListaVagas extends AppCompatActivity{

    EditText editCNPJ,editCargo,editSalario,editDesc,editEmail;
    ListView listV_dados;

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

        inicializaComponentes();
        inicializaFirebase();
        eventoFirebase();

        // Update e Delete - pega o indice da pessoa selecionada na lista
        listV_dados.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                vagaSelecionada = (Vaga)adapterView.getItemAtPosition(i);
                editCNPJ.setText(vagaSelecionada.getCNPJ());
                editCargo.setText(vagaSelecionada.getCargo());
                editSalario.setText(vagaSelecionada.getSalario());
                editDesc.setText(vagaSelecionada.getDesc());
                editEmail.setText(vagaSelecionada.getEmail());
            }

        });


    }


    @Override //método para exibir o menu
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_vagas,menu);//o meu menu criado, e o q esta no parametro
        return super.onCreateOptionsMenu(menu);
    }

    @Override //Ações do click nos itens criados: "novo","atualiza" e "deleta" no menu
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId(); //pega pelo id o item superior do menu q foi clicado

        if(id==R.id.menu_novo){ //se o click for no btn de "novo"
            Vaga v = new Vaga();
            v.setId(UUID.randomUUID().toString()); //classe q gera um id Randômico
            v.setCNPJ(editCNPJ.getText().toString());
            v.setCargo(editCargo.getText().toString());
            v.setSalario(editSalario.getText().toString());
            v.setDesc(editDesc.getText().toString());
            v.setEmail(editEmail.getText().toString());
            databaseReference.child("Vaga").child(v.getId()).setValue(v); //cria a tabela,a chave primaria, e seta o id no objeto
            limpaCampos();


            //Atualiza
        }else if(id==R.id.menu_atualiza) { //se o click for no btn de atualiza

            Vaga v = new Vaga();
            v.setId(vagaSelecionada.getId()); //pega o id da Vaga que foi clicada
            v.setCNPJ(editCNPJ.getText().toString());
            v.setCargo(editCargo.getText().toString());
            v.setSalario(editSalario.getText().toString());
            v.setDesc(editDesc.getText().toString());
            v.setEmail(editEmail.getText().toString());
            databaseReference.child("Vaga").child(v.getId()).setValue(v);
            limpaCampos();

        //Deleta
        }else if(id==R.id.menu_deleta){

            Vaga v = new Vaga();
            v.setId(vagaSelecionada.getId());
            databaseReference.child("Vaga").child(v.getId()).removeValue();//
            limpaCampos();


        }
        return true;
    }




    private void inicializaComponentes() {
        editCNPJ = (EditText)findViewById(R.id.editCNPJ);
        editCargo = (EditText)findViewById(R.id.editCargo);
        editSalario = (EditText)findViewById(R.id.editSalario);
        editDesc = (EditText)findViewById(R.id.editDesc);
        editEmail = (EditText)findViewById(R.id.editEmail);
        listV_dados = (ListView)findViewById(R.id.listV_dados);
    }

    //inicializa a conexão com o banco de dados e suas variaveis
    private void inicializaFirebase() {
        FirebaseApp.initializeApp(ListaVagas.this);
        if (firebaseDatabase == FirebaseDatabase.getInstance()){
            firebaseDatabase.setPersistenceEnabled(true);   //permite salvar e alterar os arqs na nuvem e no app
        } else {
            firebaseDatabase = FirebaseDatabase.getInstance();

        }
        databaseReference = firebaseDatabase.getReference();

    }

    private void eventoFirebase() {
        databaseReference.child("Vaga").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) { //traz todos os elementos de "Vaga"
                listVaga.clear(); //para nao sobrescrever dados q já tem lá
                for (DataSnapshot objSnapShot:dataSnapshot.getChildren()){
                    Vaga v = objSnapShot.getValue(Vaga.class); // passa pra var. cada obj vaga na ordem q esta no banco
                    listVaga.add(v);
                }
                arrayAdapterVaga = new ArrayAdapter<Vaga>(ListaVagas.this,android.R.layout.simple_list_item_1,listVaga);
                listV_dados.setAdapter(arrayAdapterVaga);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void limpaCampos() {

        editCNPJ.setText("");
        editCargo.setText("");
        editSalario.setText("");
        editDesc.setText("");
        editEmail.setText("");
    }

}
