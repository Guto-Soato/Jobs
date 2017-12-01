package st.mape.jobs;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static st.mape.jobs.MapsActivity.MAP_PERMISSION_ACCESS_COURSE_LOCATION;

/**
 * Created by Matheus Rodrigues on 29/09/2017.
 */

public class ListaPostos extends AppCompatActivity {

    // variaveis recyclerView
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.Adapter mAdapter;
    LocationManager locManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tela_lista_posto);

        // Trecho relacionado ao RecycleView e Retrofit
        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);// instancia a var. com o objeto recycler do xml
        mRecyclerView.setHasFixedSize(true); //fixa o tam do recycler

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager); //escolhendo um gerenciador de layout p/ o recycler

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://mobile-aceite.tcu.gov.br/mapa-da-saude/")// iRetrofitTCU.ENDPOINT
                .addConverterFactory(GsonConverterFactory.create())
                .build();


        iRetrofitTCU service = retrofit.create(iRetrofitTCU.class);

        locManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions( this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION}, MAP_PERMISSION_ACCESS_COURSE_LOCATION);
        } else {

        }

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED ) {
            Location lastKnownLocation = locManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            LatLng me = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());

            Call<List<Posto>> callListPostoSINE = service.listPosto(String.valueOf(me.latitude), String.valueOf(me.longitude), "500"); //getPosto("2334007-0")
            callListPostoSINE.enqueue(new retrofit2.Callback<List<Posto>>() {
                @Override
                public void onResponse(Call<List<Posto>> call, Response<List<Posto>> response) {
                    List<Posto> listPosto = new ArrayList<Posto>();
                    if (response.isSuccessful()) {

                        for (Posto p : response.body()) {
                            listPosto.add(p);
                        }
                        mAdapter = new MyAdapter(ListaPostos.this, listPosto);
                        //rodar o APP e verificar qual dos 3 log esta entrando
                        Log.e("RESPOSTA", "Esta no onResponse! Boa Muleke!");
                        mRecyclerView.setAdapter(mAdapter);
                    } else {
                        Log.e("RESPOSTA", "ERRO esta no else");
                    }
                }

                @Override
                public void onFailure(Call<List<Posto>> call, Throwable t) {
                    Log.e("RESPOSTA", "ERRO esta no onFailure");
                }
            });
            // Fim do trecho
        }
    }
}