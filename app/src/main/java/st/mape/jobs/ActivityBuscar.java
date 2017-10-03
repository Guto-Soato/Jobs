package st.mape.jobs;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

/**
 * Created by Matheus Rodrigues on 03/09/2017.
 */

public class ActivityBuscar extends AppCompatActivity {


    private Button btnVoltar, btnBuscar;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tela_buscar);

        inicializaComponentes();
        eventoClicks();
        eventoClicks2();

}

    //Método responsável pelos eventos de clicks nos botões
    private void eventoClicks() {

        btnVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void eventoClicks2() {

        btnBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent chamaTelaMapa = new Intent(ActivityBuscar.this,ActivityMapa.class);
                startActivity(chamaTelaMapa);
            }
        });
    }

    // Método p inicializar as variaveis com os componentes da tela
    private void inicializaComponentes(){

        btnVoltar = (Button) findViewById(R.id.btnVoltar);
        btnBuscar = (Button) findViewById(R.id.btnBuscar);

    }


}
