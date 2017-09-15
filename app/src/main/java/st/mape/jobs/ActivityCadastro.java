package st.mape.jobs;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;


public class ActivityCadastro extends AppCompatActivity {

    // vaiáveis p receber os componentes do xml
    private Button btnEmpresa,btnPessoa,btnVoltar;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cadastro);

        // recebe o click do botao Entrar
        //Intent chamaTelaCadastro = getIntent();

        // recebe o click de cadastre-se
        //Intent t = getIntent();

        inicializaComponentes();
        eventoClicks();

    }

    //Método responsável pelos eventos de clicks nos botões
    private void eventoClicks() {

        btnEmpresa.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent chamaCadEmpresa = new Intent(ActivityCadastro.this,ActivityCadEmpresa.class);
                startActivity(chamaCadEmpresa);
            }
        });

        btnVoltar.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                finish();
            }

        });
    }
        // Método p inicializar as variaveis com os componentes da tela
        private void inicializaComponentes(){

            btnEmpresa = (Button) findViewById(R.id.btnEmpresa);
            btnPessoa =  (Button) findViewById(R.id.btnPessoa);
            btnVoltar = (Button) findViewById(R.id.btnVoltar);

        }


    }



