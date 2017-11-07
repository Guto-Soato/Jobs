package st.mape.jobs;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

import static st.mape.jobs.MapsActivity.MAP_PERMISSION_ACCESS_FINE_LOCATION;
/**
 * Created by Matheus Rodrigues on 29/09/2017.
 *
 * O adaptador é responsável por receber a fonte de dados do projeto, atraves do objeto List<>
 */

    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> implements OnMapReadyCallback {

        private List<Posto> values;
        public Context contextListaPosto;

        public static Posto postos;
        public static LatLng tracaOrigem;
        public static boolean verificaFinalizar = false;
        private LocationManager locManager;

        // Fornece uma referência para as views de cada item de dados
        // Complex data items may need more than one view per item, and
        // Fornece acesso a todas as visualizações para um item de dados pelo view holder
        public static class ViewHolder extends RecyclerView.ViewHolder {
            // each data item is just a string in this case
            public TextView txtHeader;
            public TextView txtFooter;
            public View layout;
            public Button btnIr;

            public ViewHolder(View v) {
                super(v);
                layout = v;
                txtHeader = (TextView) v.findViewById(R.id.firstLine);
                txtFooter = (TextView) v.findViewById(R.id.secondLine);
            }
        }

        public void add(int position, Posto item) {
            values.add(position, item);
            notifyItemInserted(position);
        }

        public MyAdapter (Context contextListaPosto, List<Posto> myDataset) {
            this.contextListaPosto = contextListaPosto;
            values = myDataset;
        }

        // Provide a suitable constructor (depends on the kind of dataset)
        public MyAdapter(List<Posto> myDataset) {
            values = myDataset;
        }

        // Create new views (invoked by the layout manager)
        @Override
        public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            // create a new view
            LayoutInflater inflater = LayoutInflater.from(parent.getContext()); // cria uma referencia da classe inflater
            View v = inflater.inflate(R.layout.row_layout, parent, false); // cria uma referencia da view(que seria o layout de cada linha)
            // set the view's size, margins, paddings and layout parameters

            ViewHolder vh = new ViewHolder(v); // cria o obj do tipo ViewHolder
            return vh;
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            // - get element from your dataset at this position
            // - replace the contents of the view with that element
            locManager = (LocationManager) contextListaPosto.getSystemService(Context.LOCATION_SERVICE);

            if (ContextCompat.checkSelfPermission(contextListaPosto, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions((Activity) contextListaPosto, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, MAP_PERMISSION_ACCESS_FINE_LOCATION);
            } else {
                getLastLocation();
                getLocation();
            }
            final Posto posto = values.get(position);
            holder.txtHeader.setText(posto.getNome());
            holder.txtHeader.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent abreMapaComRota = new Intent(contextListaPosto, MapsActivity.class);
                    postos = values.get(position);
                    verificaFinalizar = true;
                    contextListaPosto.startActivity(abreMapaComRota);

                }

            });

            holder.txtFooter.setText("Footer: " + posto.getCodPosto());
        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return values.size();
        }

        public void getLastLocation () {
            if (ContextCompat.checkSelfPermission(contextListaPosto, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ) {
                Location lastKnownLocation = locManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                LatLng me = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
                tracaOrigem = me;
            }
        }

    public void getLocation() {
        if (ContextCompat.checkSelfPermission(contextListaPosto, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ) {
            LocationListener locationListener = new LocationListener() {
                public void onLocationChanged(Location location) {
                    LatLng me = new LatLng(location.getLatitude(), location.getLongitude());
                    tracaOrigem = me;
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

        locManager = (LocationManager) contextListaPosto.getSystemService(Context.LOCATION_SERVICE);

        if (ContextCompat.checkSelfPermission(contextListaPosto, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) contextListaPosto, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, MAP_PERMISSION_ACCESS_FINE_LOCATION);
        } else {
            getLastLocation();
            getLocation();
        }
    }

}


