package st.mape.jobs;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.Callback;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by gustavo.soato on 01/11/2017.
 */

public class ControladorRetrofit implements Callback<List<Posto>> {

    public static final String BASE_URL = "http://mobile-aceite.tcu.gov.br/mapa-da-saude/";

    private List<Posto> listaPostos = new ArrayList<Posto>();

    public ControladorRetrofit(){
        start();
    }

    public void start() {
        Gson gson = new GsonBuilder().setLenient().create();
        Retrofit retrofit = new Retrofit.Builder().baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson)).build();

        iRetrofitTCU apiPosto = retrofit.create(iRetrofitTCU.class);

        Call<List<Posto>> call = apiPosto.listPostos();
        call.enqueue(this);
    }

    public void onResponse(Call<List<Posto>> call, Response<List<Posto>> response) {
        if(response.isSuccessful()){
            List<Posto> e = new ArrayList<Posto>();
            for(Posto posto : response.body()){
                e.add(posto);
            }

            setListaPosto(e);
            Log.e("","PASSOU!");
        }else {
            Log.e("", "NAO PASSOU");
        }
    }

    @Override
    public void onFailure(Call<List<Posto>> call, Throwable t) {
        Log.e("","Falou");
        Log.e("",t.getMessage());
    }

    public List<Posto> getListaPostos() {

        return listaPostos;
    }

    public void setListaPosto(List<Posto> listaPostos) {
        this.listaPostos = listaPostos;
    }

}
