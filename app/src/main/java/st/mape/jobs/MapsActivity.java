package st.mape.jobs;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.http.AndroidHttpClient;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static st.mape.jobs.ControladorRetrofit.BASE_URL;
import static st.mape.jobs.MyAdapter.postos;
import static st.mape.jobs.MyAdapter.tracaOrigem;


public class MapsActivity extends FragmentActivity /*SupportMapFragment*/ implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleMap mMap;

    private LocationManager locationManager;



    private iRetrofitTCU apiPostos;

    private ControladorRetrofit controllerRetrofit = new ControladorRetrofit();

    private List<String> listEstab = new ArrayList<String>();

    private List<Posto> listEst = new ArrayList<Posto>();

    public LatLng tracaRotaLocalOrigem, tracaRotaLocalDestino, tracaRotaLocalOrigemVerifica;

    private List<LatLng> listaRota;

    private long distancia;

    private Polyline polyline;



    private Marker meuMarker;

    private boolean jaEntrouNoLastLocation = false;



    private Location mLastLocation;

    private GoogleApiClient mGoogleApiClient;

    public final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;

    public static final int MAP_PERMISSION_ACCESS_FINE_LOCATION = 9999;



    @Override

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.tela_mapa);



        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);



        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

        // TEMOS QUE CRIAR UM NOVO MAPA!!!!

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()

                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);



        // Primeiramente precisamos checar a disponibilidade da play services

        if (checkPlayServices()) {

            // Bildando o cliente da google api

            buildGoogleApiClient();

        }

    }

    @Override

    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;



        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);



        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, MAP_PERMISSION_ACCESS_FINE_LOCATION);

        } else {

            getLastLocation();

            getLocation();

            displayLocation();

        }

        mMap.setMyLocationEnabled(false);

        mMap.setBuildingsEnabled(true);



        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {

            @Override

            public boolean onMarkerClick(Marker marker) {



                try{

                    if(tracaRotaLocalOrigem != null && marker.getPosition() != null){

                        getRoute(tracaRotaLocalOrigem,marker.getPosition());

                    }

                }catch (Exception e){



                }

                return false;

            }

        });

    }



    public void getLastLocation() {

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ) {

            Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            LatLng me = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());

            if(jaEntrouNoLastLocation == false) {

                tracaRotaLocalOrigem = me;

                if(meuMarker != null){

                    meuMarker.remove();

                }

                meuMarker = mMap.addMarker(new MarkerOptions().position(me).title("Eu estava aqui quando o anrdoid me localizou pela última vez!!!").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));

                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(me, (float) 14.5));

                jaEntrouNoLastLocation = true;

            }



            Call<List<Posto>> callLatLong = apiPostos.listPosto(String.valueOf(me.latitude), String.valueOf(me.longitude),"500");

            callLatLong.enqueue(new Callback<List<Posto>>() {

                @Override

                public void onResponse(Call<List<Posto>> call, Response<List<Posto>> response) {

                    if(response.isSuccessful()){

                        for(Posto posto : response.body()){

                            listEstab.add(posto.getNome());

                            listEst.add(posto);

                            Float lat = posto.getLatitude();

                            Float longi = posto.getLongitude();

                            String nomeDoEstabelecimento = posto.getNome();

                            mMap.addMarker(new MarkerOptions().position(new LatLng(lat,longi)).title(nomeDoEstabelecimento).icon(BitmapDescriptorFactory.fromResource( R.mipmap.ic_launcher_round)));

                        }

                        Log.e("","PASSOU222!");

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

                    tracaRotaLocalOrigem = me;

                    tracaRotaLocalOrigemVerifica = me;

                    //mMap.clear();

                    if(meuMarker != null){

                        meuMarker.remove();

                    }

                    meuMarker = mMap.addMarker(new MarkerOptions().position(me).title("Estou Aqui!!!").icon( BitmapDescriptorFactory.defaultMarker( BitmapDescriptorFactory.HUE_ORANGE )));

                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(me, (float) 14.5));



                    Call<List<Posto>> callLatLong = apiPostos.listPosto(String.valueOf(me.latitude), String.valueOf(me.longitude),"500");

                    callLatLong.enqueue(new Callback<List<Posto>>() {

                        @Override

                        public void onResponse(Call<List<Posto>> call, Response<List<Posto>> response) {

                            if(response.isSuccessful()){

                                for(Posto posto : response.body()){

                                    listEstab.add(posto.getNome());

                                    listEst.add(posto);

                                    Float lat = posto.getLatitude();

                                    Float longi = posto.getLongitude();

                                    String nomeDoEstabelecimento = posto.getNome();

                                    mMap.addMarker(new MarkerOptions().position(new LatLng(lat,longi)).title(nomeDoEstabelecimento).icon(BitmapDescriptorFactory.fromResource( R.mipmap.ic_launcher_round)));

                                }

                                Log.e("","PASSOU222!");

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

            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 40000, 0, locationListener);

        }

    }



    @Override

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        switch (requestCode) {

            case MAP_PERMISSION_ACCESS_FINE_LOCATION: {

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



    @Override

    protected void onStart() {

        super.onStart();

        if (mGoogleApiClient != null) {

            mGoogleApiClient.connect();

        }

        Gson gson = new GsonBuilder().setLenient().create();

        Retrofit retrofit = new Retrofit.Builder().baseUrl(BASE_URL)

                .addConverterFactory(GsonConverterFactory.create(gson)).build();



        apiPostos = retrofit.create(iRetrofitTCU.class);



        Call<List<Posto>> call = apiPostos.listPostos();

        call.enqueue(new Callback<List<Posto>>() {

            @Override

            public void onResponse(Call<List<Posto>> call, Response<List<Posto>> response) {

                if(response.isSuccessful()){

                    for(Posto posto : response.body()){

                        listEstab.add(posto.getNome());

                        listEst.add(posto);

                        Float lat = posto.getLatitude();

                        Float longi = posto.getLongitude();

                        String nomeDoEstabelecimento = posto.getNome();

                        mMap.addMarker(new MarkerOptions().position(new LatLng(lat,longi)).title(nomeDoEstabelecimento).icon(BitmapDescriptorFactory.fromResource( R.mipmap.ic_launcher_round)));

                    }

                    Log.e("","PASSOU!");

                }else {

                    Log.e("", "NAO PASSOU");

                }

            }



            @Override

            public void onFailure(Call<List<Posto>> call, Throwable t) {



            }

        });



    }



    /* ***************************************** ROTA ***************************************** */



    @Override

    protected void onResume() {

        super.onResume();

        checkPlayServices();

        try{

            LatLng tracaDestino = new LatLng(Double.parseDouble(String.valueOf(postos.getLatitude())),Double.parseDouble(String.valueOf(postos.getLongitude())));

            if(tracaOrigem != null && tracaDestino != null){

                getRoute(tracaOrigem,tracaDestino);

            }

        }catch (Exception e){



        }

    }



    public void getRoute(final LatLng origin, final LatLng destination){

        new Thread(){

            public void run(){



                /*String url= "http://maps.googleapis.com/maps/api/directions/json?origin="

                        + origin +"&destination="

                        + destination +"&sensor=false";*/

                String url= "http://maps.googleapis.com/maps/api/directions/json?origin="

                        + origin.latitude+","+origin.longitude+"&destination="

                        + destination.latitude+","+destination.longitude+"&sensor=false";





                HttpResponse response;

                HttpGet request;

                AndroidHttpClient client = AndroidHttpClient.newInstance("route");



                request = new HttpGet(url);

                try {

                    response = client.execute(request);

                    final String answer = EntityUtils.toString(response.getEntity());



                    runOnUiThread(new Runnable(){

                        public void run(){

                            try {

                                //Log.i("Script", answer);

                                listaRota = buildJSONRoute(answer);

                                drawRoute();

                            }

                            catch(JSONException e) {

                                e.printStackTrace();

                            }

                        }

                    });



                }

                catch(IOException e) {

                    e.printStackTrace();

                }

            }

        }.start();

    }



    // PARSER JSON

    public List<LatLng> buildJSONRoute(String json) throws JSONException{

        JSONObject result = new JSONObject(json);

        JSONArray routes = result.getJSONArray("routes");



        distancia = routes.getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONObject("distance").getInt("value");



        JSONArray steps = routes.getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONArray("steps");

        List<LatLng> lines = new ArrayList<LatLng>();



        for(int i=0; i < steps.length(); i++) {

            Log.i("Script", "STEP: LAT: "+steps.getJSONObject(i).getJSONObject("start_location").getDouble("lat")+" | LNG: "+steps.getJSONObject(i).getJSONObject("start_location").getDouble("lng"));





            String polyline = steps.getJSONObject(i).getJSONObject("polyline").getString("points");



            for(LatLng p : decodePolyline(polyline)) {

                lines.add(p);

            }



            Log.i("Script", "STEP: LAT: "+steps.getJSONObject(i).getJSONObject("end_location").getDouble("lat")+" | LNG: "+steps.getJSONObject(i).getJSONObject("end_location").getDouble("lng"));

        }



        return(lines);

    }



    // DECODE POLYLINE

    private List<LatLng> decodePolyline(String encoded) {



        List<LatLng> listPoints = new ArrayList<LatLng>();

        int index = 0, len = encoded.length();

        int lat = 0, lng = 0;



        while (index < len) {

            int b, shift = 0, result = 0;

            do {

                b = encoded.charAt(index++) - 63;

                result |= (b & 0x1f) << shift;

                shift += 5;

            } while (b >= 0x20);

            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));

            lat += dlat;



            shift = 0;

            result = 0;

            do {

                b = encoded.charAt(index++) - 63;

                result |= (b & 0x1f) << shift;

                shift += 5;

            } while (b >= 0x20);

            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));

            lng += dlng;



            LatLng p = new LatLng((((double) lat / 1E5)), (((double) lng / 1E5)));

            Log.i("Script", "POL: LAT: "+p.latitude+" | LNG: "+p.longitude);

            listPoints.add(p);

        }

        return listPoints;

    }



    public void drawRoute(){

        PolylineOptions po;



        if(polyline == null){

            po = new PolylineOptions();



            for(int i = 0, tam = listaRota.size(); i < tam; i++){

                po.add(listaRota.get(i));

            }



            po.color(Color.BLUE).width(4);

            polyline = mMap.addPolyline(po);

        }

        else{

            polyline.setPoints(listaRota);

        }

    }



    /* ******************Localização com a LocationServices API******************************** */



    protected synchronized void buildGoogleApiClient() {

        // Cria um cliente da google API

        mGoogleApiClient = new GoogleApiClient.Builder(this)

                .addConnectionCallbacks(this)

                .addOnConnectionFailedListener(this)

                .addApi(LocationServices.API).build();

    }

    private boolean checkPlayServices() {

        int resultCode = GooglePlayServicesUtil

                .isGooglePlayServicesAvailable(this);

        if (resultCode != ConnectionResult.SUCCESS) {

            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {

                GooglePlayServicesUtil.getErrorDialog(resultCode, this,

                        PLAY_SERVICES_RESOLUTION_REQUEST).show();

            } else {

                Toast.makeText(getApplicationContext(),

                        "Não foi possível achar a google play services", Toast.LENGTH_LONG)

                        .show();

                finish();

            }

            return false;

        }

        return true;

    }



    private void displayLocation() {

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, MAP_PERMISSION_ACCESS_FINE_LOCATION);

            return;

        }

        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (mLastLocation != null) {

            //Minha localização

            double latitude = mLastLocation.getLatitude();

            double longitude = mLastLocation.getLongitude();

            LatLng me = new LatLng(latitude, longitude);



            tracaRotaLocalOrigem = me;

            tracaRotaLocalOrigemVerifica = me;

            if(meuMarker != null){

                meuMarker.remove();

            }

            meuMarker = mMap.addMarker(new MarkerOptions().position(me).title("Oh você aqui!!!").icon( BitmapDescriptorFactory.defaultMarker( BitmapDescriptorFactory.HUE_ORANGE )));

            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(me, (float) 14.5));



            Log.e("Resposta","ESTA NO IF");

        } else {

            Log.e("RESPOSTA","ESTÁ NO ELSE");

        }

    }





    @Override

    public void onConnectionFailed(ConnectionResult result) {

        Log.i("", "Connection failed:  "

                + result.getErrorCode());

    }

    @Override

    public void onConnected(Bundle arg0) {

        // Once connected with google api, get the location

        displayLocation();

    }

    @Override

    public void onConnectionSuspended(int arg0) {

        mGoogleApiClient.connect();

    }



    @Override

    public void onBackPressed() {

        super.onBackPressed();

    }

}