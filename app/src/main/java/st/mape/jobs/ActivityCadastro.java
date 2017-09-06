package st.mape.jobs;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;


public class ActivityCadastro extends AppCompatActivity {

    // vaiáveis p receber os componentes do xml
    private Button btnEmpresa,btnPessoa;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cadastro);

        // recebe o click do botao Entrar
        Intent chamaTelaCadastro = getIntent();

        inicializaComponentes();
        eventoClicks();

    }

    //Método responsável pelos eventos de clicks nos botões
    private void eventoClicks() {

        btnEmpresa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent chamaCadEmpresa = new Intent();
                startActivity(chamaCadEmpresa);
            }
        });
    }
        // Método p inicializar as variaveis com os componentes da tela
        private void inicializaComponentes(){

            btnEmpresa = (Button) findViewById(R.id.btnEnter);
            btnPessoa =  (Button) findViewById(R.id.btnPessoa);

        }


    }



