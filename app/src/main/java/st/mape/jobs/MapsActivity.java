package st.mape.jobs;

import android.*;
import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    public static final int MAP_PERMISSION_ACCESS_FINE_LOCATION = 9999;

    private iRetrofitTCU apiPosto;

    private double latitude;
    private double longitude;
    public double latp;
    public double longp;

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("http://mobile-aceite.tcu.gov.br/mapa-da-saude/")// iRetrofitTCU.ENDPOINT
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tela_mapa);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        longitude = location.getLongitude();
        latitude = location.getLatitude();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        iRetrofitTCU service = retrofit.create(iRetrofitTCU.class);

        Call<List<Posto>> callListPostoSINE = service.listPosto(String.valueOf(latitude), String.valueOf(longitude), "500"); //getPosto("2334007-0")
        callListPostoSINE.enqueue(new retrofit2.Callback<List<Posto>>() {
            @Override
            public void onResponse(Call<List<Posto>> call, Response<List<Posto>> response) {
                List<Posto> listPosto = new ArrayList<Posto>();
                if(response.isSuccessful()){
                    for(Posto posto : response.body()){
                        listPosto.add(posto);
                        for (int i = 0; i < listPosto.size(); i++) {
                            latp = listPosto.get(i).getLatitude();
                            longp = listPosto.get(i).getLongitude();
                            String nome = listPosto.get(i).getNome();
                            LatLng latLngp = new LatLng(latp, longp);
                            mMap.addMarker(new MarkerOptions().position(latLngp).title(nome));
                        }
                    }
                    //rodar o APP e verificar qual dos 3 log esta entrando
                    Log.e("RESPOSTA","Esta no onResponse! Boa Muleke!");
                }else{
                    Log.e("RESPOSTA","ERRO esta no else");
                }
            }
            @Override
            public void onFailure(Call<List<Posto>> call, Throwable t) {
                Log.e("RESPOSTA","ERRO esta no onFailure");
            }
        });

        LatLng latLng = new LatLng(latitude, longitude);
        mMap.addMarker(new MarkerOptions().position(latLng).title("Você está aqui"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10.2f));
    }
}