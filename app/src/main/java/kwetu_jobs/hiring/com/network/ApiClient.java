package kwetu_jobs.hiring.com.network;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

    public class ApiClient {
        private static final String BASE_URL = "https://resume-parser-845078321603.us-central1.run.app/"; // Use your API URL
        private static Retrofit retrofit = null;

        public static Retrofit getClient() {
            if (retrofit == null) {
                retrofit = new Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
            }
            return retrofit;
        }

}
