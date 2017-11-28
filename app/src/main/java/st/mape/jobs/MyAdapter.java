package st.mape.jobs;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;
/**
 * Created by Matheus Rodrigues on 29/09/2017.
 *
 * O adaptador é responsável por receber a fonte de dados do projeto, atraves do objeto List<>
 */

    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

        private List<Posto> values;
        public Context contextListaPosto;

        public static Posto postos;
        public static boolean verificaFinalizar = false;

        // Fornece uma referência para as views de cada item de dados
        // Complex data items may need more than one view per item, and
        // Fornece acesso a todas as visualizações para um item de dados pelo view holder
        public class ViewHolder extends RecyclerView.ViewHolder {
            // each data item is just a string in this case
            public TextView txtHeader;
            public TextView txtFooter;
            public View layout;

            public ViewHolder(View v) {
                super(v);
                layout = v;
                txtHeader = (TextView) v.findViewById(R.id.firstLine);
                txtFooter = (TextView) v.findViewById(R.id.secondLine);
            }
        }

        public MyAdapter (Context contextListaPosto, List<Posto> myDataset) {
            this.contextListaPosto = contextListaPosto;
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

            holder.txtFooter.setText("Código: " + posto.getCodPosto());
            holder.txtFooter.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent abreMapaComRota = new Intent(contextListaPosto, MapsActivity.class);
                    postos = values.get(position);
                    verificaFinalizar = true;
                    contextListaPosto.startActivity(abreMapaComRota);
                }
            });
        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return values.size();
        }

}


