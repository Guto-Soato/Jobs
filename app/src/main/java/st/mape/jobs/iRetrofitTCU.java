package st.mape.jobs;


import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by Matheus Rodrigues on 29/09/2017.
 */

public interface iRetrofitTCU {

    //String ENDPOINT = "http://mobile-aceite.tcu.gov.br/mapa-da-saude";

    @GET("rest/emprego")
    Call<List<Posto>> callListPostoSINE(); // getPosto(@Path("codPosto") String codPosto



}
