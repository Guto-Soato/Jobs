package st.mape.jobs;

import android.*;
import android.Manifest;
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
    private FragmentManager fragmentManager;
    private FirebaseAuth auth;
    private LocationManager locManager;
    private iRetrofitTCU apiPostos;
    private List<String> listPostos = new ArrayList<String>();
    private List<Posto> listPosto = new ArrayList<Posto>();

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://github.com//AppCivicoPlataforma/AppCivico/")// iRetrofitTCU.ENDPOINT
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    iRetrofitTCU service = retrofit.create(iRetrofitTCU.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tela_mapa);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        apiPostos = retrofit.create(iRetrofitTCU.class);

        locManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        if (locManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
            locManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    double lat = location.getLatitude();
                    double lon = location.getLongitude();
                    LatLng latLon = new LatLng(lat,lon);
                    mMap.addMarker(new MarkerOptions().position(latLon).title("Você está aqui!"));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLon, 10.2f));
                    Call<List<Posto>> callListPostoSINE = apiPostos.listPosto(String.valueOf(lat), String.valueOf(lon), "500");
                    callListPostoSINE.enqueue(new Callback<List<Posto>>() {
                        @Override
                        public void onResponse(Call<List<Posto>> call, Response<List<Posto>> response) {
                            if (response.isSuccessful()){
                                for (Posto posto : response.body()){
                                    listPostos.add(posto.getNome());
                                    listPosto.add(posto);
                                    double latp = posto.getLatitude();
                                    double lonp = posto.getLongitude();
                                    String nomep = posto.getNome();
                                    mMap.addMarker(new MarkerOptions().position(new LatLng(latp, lonp)).title(nomep));
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<List<Posto>> call, Throwable t) {

                        }
                    });
                }

                @Override
                public void onStatusChanged(String s, int i, Bundle bundle) {

                }

                @Override
                public void onProviderEnabled(String s) {

                }

                @Override
                public void onProviderDisabled(String s) {

                }
            });
        } else if (locManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    double lat = location.getLatitude();
                    double lon = location.getLongitude();
                    LatLng latLon = new LatLng(lat,lon);
                    Geocoder geocoder = new Geocoder(getApplicationContext());
                    try {
                        List<Address> addresses = geocoder.getFromLocation(lat, lon, 1);
                        String str = addresses.get(0).getLocality()+",";
                        str += addresses.get(0).getCountryName();
                        mMap.addMarker(new MarkerOptions().position(latLon).title(str));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLon, 10.2f));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onStatusChanged(String s, int i, Bundle bundle) {

                }

                @Override
                public void onProviderEnabled(String s) {

                }

                @Override
                public void onProviderDisabled(String s) {

                }
            });
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


    }
}