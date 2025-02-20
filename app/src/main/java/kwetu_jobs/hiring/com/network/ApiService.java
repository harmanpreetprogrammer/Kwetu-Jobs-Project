package kwetu_jobs.hiring.com.network;

    import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

    public interface ApiService {
        @POST("/ner")  // Endpoint for Named Entity Recognition
        Call<NERResponse> getNER(@Body TextRequest text);

}
