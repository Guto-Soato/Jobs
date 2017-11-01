package st.mape.jobs;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;

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

public class MapsActivity extends SupportMapFragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LocationManager locManager;

    private iRetrofitTCU apiEmprego;

    private ControladorRetrofit control = new ControladorRetrofit();
    private List<String> listPosto = new ArrayList<String>();
    private List<Posto> listPostos = new ArrayList<Posto>();

    public LatLng tracaRotaOrigem, tracaRotaDestino, tracaRotaVerifica;
    private List<LatLng> listaRota;
    private long distancia;
    private Polyline polyline;

    public static final int MAP_PERMISSION_ACCESS_FINE_LOCATION = 9999;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        locManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, MAP_PERMISSION_ACCESS_FINE_LOCATION);
        } else {
            getLastLocation();
            getLocation();
        }

        mMap.setMyLocationEnabled(false);
        mMap.setBuildingsEnabled(true);

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                try{
                    if(tracaRotaOrigem != null && marker.getPosition() != null){
                        getRoute(tracaRotaOrigem,marker.getPosition());
                    }
                }catch (Exception e){

                }
                return false;
            }
        });
    }

    public void getLastLocation() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ) {
            Location lastKnownLocation = locManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            LatLng me = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
            tracaRotaOrigem = me;
            MarkerOptions eu = new MarkerOptions().position(me).title("Eu estava aqui quando o anrdoid me localizou pela última vez!!!").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
            if(tracaRotaVerifica != null){
                //mMap.addMarker(eu.visible(false));
                mMap.clear();
            }else{
                mMap.clear();
                mMap.addMarker(eu.visible(true));
            }

            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(me, (float) 14.5));

            Call<List<Posto>> callLatLong = apiEmprego.listPosto(String.valueOf(me.latitude), String.valueOf(me.longitude),"500");
            callLatLong.enqueue(new Callback<List<Posto>>() {
                @Override
                public void onResponse(Call<List<Posto>> call, Response<List<Posto>> response) {
                    if(response.isSuccessful()){
                        for(Posto postos : response.body()){
                            listPosto.add(postos.getNome());
                            listPostos.add(postos);
                            Float lat = postos.getLatitude();
                            Float longi = postos.getLongitude();
                            String nomeDoEstabelecimento = postos.getNome();
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
                    tracaRotaOrigem = me;
                    tracaRotaVerifica= me;
                    //mMap.clear();
                    mMap.addMarker(new MarkerOptions().position(me).title("Estou Aqui!!!").icon( BitmapDescriptorFactory.defaultMarker( BitmapDescriptorFactory.HUE_ORANGE )));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(me, (float) 14.5));

                    Call<List<Posto>> callLatLong = apiEmprego.listPosto(String.valueOf(me.latitude), String.valueOf(me.longitude),"500");
                    callLatLong.enqueue(new Callback<List<Posto>>() {
                        @Override
                        public void onResponse(Call<List<Posto>> call, Response<List<Posto>> response) {
                            if(response.isSuccessful()){
                                for(Posto postos : response.body()){
                                    listPosto.add(postos.getNome());
                                    listPostos.add(postos);
                                    Float lat = postos.getLatitude();
                                    Float longi = postos.getLongitude();
                                    String nomeDoEstabelecimento = postos.getNome();
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
            locManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 40000, 0, locationListener);
        }
    }

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
        /* Add a marker in IESB and move the camera
        LatLng iesb = new LatLng(-15.834930, -47.912832);
        mMap.addMarker(new MarkerOptions().position(iesb).title("Marker in IESB"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(iesb));*/

    public void onStart() {
        super.onStart();
        Gson gson = new GsonBuilder().setLenient().create();
        Retrofit retrofit = new Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create(gson)).build();
        apiEmprego = retrofit.create(iRetrofitTCU.class);

        Call<List<Posto>> call = apiEmprego.listPostos();
        call.enqueue(new Callback<List<Posto>>() {
            @Override
            public void onResponse(Call<List<Posto>> call, Response<List<Posto>> response) {
                if (response.isSuccessful()) {
                    for (Posto posto : response.body()){
                        listPosto.add(posto.getNome());
                        listPostos.add(posto);
                        Float lat = posto.getLatitude();
                        Float lon = posto.getLongitude();
                        String nome = posto.getNome();
                        mMap.addMarker(new MarkerOptions().position(new LatLng(lat,lon)).title(nome).icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher_round)));
                    }
                    Log.e("","Passou!");
                } else {
                    Log.e("","Não Passou!");
                }
            }

            @Override
            public void onFailure(Call<List<Posto>> call, Throwable t) {

            }
        });
    }

    protected void onResume() {
        super.onResume();
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
}
