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
    public static final int MAP_PERMISSION_ACCESS_COURSE_LOCATION = 9999;

    private iRetrofitTCU apiPosto;

    LocationManager locManager;

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
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        locManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions( this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION}, MAP_PERMISSION_ACCESS_COURSE_LOCATION);
        } else {
            getLastLocation();
            getLocation();

            Location lastKnownLocation = locManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        }
    }

    private void getLastLocation() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED ) {
            Location lastKnownLocation = locManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            LatLng me = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
            mMap.addMarker(new MarkerOptions().position(me).title("Você está aqui!"));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(me, 10));

            iRetrofitTCU service = retrofit.create(iRetrofitTCU.class);

            Call<List<Posto>> callListPostoSINE = service.listPosto(String.valueOf(me.latitude), String.valueOf(me.longitude), "500"); //getPosto("2334007-0")
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
        }
    }

    private void getLocation() {
       if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED ) {
            LocationListener locationListener = new LocationListener() {
                public void onLocationChanged(Location location) {
                    LatLng me = new LatLng(location.getLatitude(), location.getLongitude());
                    mMap.addMarker(new MarkerOptions().position(me).title("Estou Aqui!!!"));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(me, 10));

                    iRetrofitTCU service = retrofit.create(iRetrofitTCU.class);

                    Call<List<Posto>> callListPostoSINE = service.listPosto(String.valueOf(me.latitude), String.valueOf(me.longitude), "500"); //getPosto("2334007-0")
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
                                Log.e("RESPOSTA","Esta no onResponse! Boa Cara!");
                            }else{
                                Log.e("RESPOSTA","ERRO esta no else");
                            }
                        }
                        @Override
                        public void onFailure(Call<List<Posto>> call, Throwable t) {
                            Log.e("RESPOSTA","ERRO esta no onFailure");
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
            locManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 600000, 0, locationListener);
       }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MAP_PERMISSION_ACCESS_COURSE_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getLastLocation();
                    getLocation();
                } else {
                    //Permissão negada
                }
                return;
            }
        }
    }
}