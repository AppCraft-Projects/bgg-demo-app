package hu.androidworkshop;

import android.app.Application;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import hu.androidworkshop.json.JsonMapper;
import hu.androidworkshop.json.JsonMapperInterface;
import hu.androidworkshop.network.BGGApiDefinition;
import hu.androidworkshop.network.ImageUploader;
import hu.androidworkshop.network.ImageUploaderInterface;
import hu.androidworkshop.persistence.RecommendationDatabaseHelper;
import hu.androidworkshop.places.BuildConfig;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GourmetApplication extends Application {

    private BGGApiDefinition apiDefinition;
    private ImageUploaderInterface imageUploader;
    private JsonMapperInterface jsonMapper;

    @Override
    public void onCreate() {
        super.onCreate();

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .writeTimeout(60L, TimeUnit.SECONDS)
                .readTimeout(60L, TimeUnit.SECONDS)
                .connectTimeout(60L, TimeUnit.SECONDS)
                .build();
        Gson gson = new GsonBuilder().create();

        jsonMapper = new JsonMapper(gson);
        apiDefinition = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(okHttpClient)
                .baseUrl(BuildConfig.API_BASE_URL)
                .build()
                .create(BGGApiDefinition.class);
        imageUploader = new ImageUploader(okHttpClient, "image");
        RecommendationDatabaseHelper.getInstance(this).deleteAllPostsAndUsers();
    }

    public BGGApiDefinition getApiClient() {
        return apiDefinition;
    }

    public ImageUploaderInterface getImageUploader() {
        return imageUploader;
    }

    public JsonMapperInterface getJsonMapper() {
        return jsonMapper;
    }
}
