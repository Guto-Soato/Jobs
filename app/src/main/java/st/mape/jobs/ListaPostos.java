package st.mape.jobs;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static st.mape.jobs.ControladorRetrofit.BASE_URL;
import static st.mape.jobs.MapsActivity.MAP_PERMISSION_ACCESS_FINE_LOCATION;
import static st.mape.jobs.MyAdapter.verificaFinalizar;

/**
 * Created by Matheus Rodrigues on 29/09/2017.
 */

public class ListaPostos extends AppCompatActivity implements OnMapReadyCallback{

    // variaveis recyclerView
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.Adapter mAdapter;

    private LatLng origem;
    private LocationManager locManager;
    private iRetrofitTCU apiPosto;

    private List<Posto> listPostos = new ArrayList<Posto>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tela_lista_posto);


        // Trecho relacionado ao RecycleView e Retrofit
        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);// instancia a var. com o objeto recycler do xml
        mRecyclerView.setHasFixedSize(true); //fixa o tam do recycler

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager); //escolhendo um gerenciador de layout p/ o recycler

        if (verificaFinalizar == true) {
            finish();
            verificaFinalizar = false;
        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)// iRetrofitTCU.ENDPOINT
                .addConverterFactory(GsonConverterFactory.create())
                .build();


        apiPosto = retrofit.create(iRetrofitTCU.class);

        locManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, MAP_PERMISSION_ACCESS_FINE_LOCATION);
        } else {
            getLastLocation();
            getLocation();
        }

        Call<List<Posto>> callListPostoSINE = apiPosto.listPostos(); //getPosto("2334007-0")
        callListPostoSINE.enqueue(new Callback<List<Posto>>() {
            @Override
            public void onResponse(Call<List<Posto>> call, Response<List<Posto>> response) {

                if(response.isSuccessful()){

                    for(Posto p : response.body()){
                        listPostos.add(p);
                    }
                    mAdapter = new MyAdapter(ListaPostos.this, listPostos);
                    //rodar o APP e verificar qual dos 3 log esta entrando
                    Log.e("RESPOSTA","Esta no onResponse! Boa Muleke!");
                    mRecyclerView.setAdapter(mAdapter);
                }else{
                    Log.e("RESPOSTA","ERRO esta no else");
                }
            }

            @Override
            public void onFailure(Call<List<Posto>> call, Throwable t) {
                Log.e("RESPOSTA","ERRO esta no onFailure");
            }
        });
        // Fim do trecho
    }

    public void getLastLocation() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ) {
            Location lastKnownLocation = locManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            LatLng me = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
            origem = me;
            //mMap.addMarker(new MarkerOptions().position(me).title("Eu estava aqui quando o anrdoid me localizou pela última vez!!!"));
            //mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(me, 10));
            Call<List<Posto>> callLatLong = apiPosto.listPosto(String.valueOf(origem.latitude), String.valueOf(origem.longitude),"200");
            callLatLong.enqueue(new Callback<List<Posto>>() {
                @Override
                public void onResponse(Call<List<Posto>> call, Response<List<Posto>> response) {
                    if(response.isSuccessful()){
                        for(Posto e : response.body()){
                            listPostos.add(e);

                        }
                        //mAdapter = new MyAdapterEstabelecimento(TelaListaEstabelecimento.this, listEst);
                        Log.e("","Funcionou");
                        //recyclerView.setAdapter(mAdapter);
                    }else {
                        Log.e("", "NAO PASSOU222");
                    }
                }

                @Override
                public void onFailure(Call<List<Posto>> call, Throwable t) {
                    Log.e("", "NAO PASSOU333");
                }
            });
        }
    }

    public void getLocation() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ) {
            LocationListener locationListener = new LocationListener() {
                public void onLocationChanged(Location location) {
                    LatLng me = new LatLng(location.getLatitude(), location.getLongitude());
                    origem = me;
                    //mMap.addMarker(new MarkerOptions().position(me).title("Estou Aqui!!!"));
                    //mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(me, 10));
                    Call<List<Posto>> callLatLong = apiPosto.listPosto(String.valueOf(origem.latitude), String.valueOf(origem.longitude),"200");
                    callLatLong.enqueue(new Callback<List<Posto>>() {
                        @Override
                        public void onResponse(Call<List<Posto>> call, Response<List<Posto>> response) {
                            if(response.isSuccessful()){
                                for(Posto e : response.body()){
                                    listPostos.add(e);

                                }
                                //mAdapter = new MyAdapterEstabelecimento(TelaListaEstabelecimento.this, listEst);
                                Log.e("","Funcionou");
                                //recyclerView.setAdapter(mAdapter);
                            }else {
                                Log.e("", "NAO PASSOU222");
                            }
                        }

                        @Override
                        public void onFailure(Call<List<Posto>> call, Throwable t) {
                            Log.e("", "NAO PASSOU333");
                        }
                    });
                }

                public void onStatusChanged(String provider, int status, Bundle extras) {
                }

                public void onProviderEnabled(String provider) {
                }

                public void onProviderDisabled(String provider) {
                }
            };
            locManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 40000, 0, locationListener);
        }
    }

    public void onMapReady(GoogleMap googleMap) {
        //mMap = googleMap;

        locManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, MAP_PERMISSION_ACCESS_FINE_LOCATION);
        } else {
            getLastLocation();
            getLocation();
        }
        //mMap.setMyLocationEnabled(true);
    }
}
