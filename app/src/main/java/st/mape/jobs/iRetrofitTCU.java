package st.mape.jobs;


import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by Matheus Rodrigues on 29/09/2017.
 */

public interface iRetrofitTCU {

    //String ENDPOINT = "http://mobile-aceite.tcu.gov.br/mapa-da-saude";

    @GET("rest/emprego")
    Call<List<Posto>> callListPostoSINE(); // getPosto(@Path("codPosto") String codPosto

    @GET("rest/emprego/latitude/{latitude}/longitude/{long}/raio/{raio}")
    Call<List<Posto>> listPosto(@Path("latitude") String lat,@Path("long") String lon,@Path("raio") String raio);

}