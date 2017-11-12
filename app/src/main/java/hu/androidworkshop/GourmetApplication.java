package hu.androidworkshop;

import android.app.Application;

import java.util.concurrent.TimeUnit;

import hu.androidworkshop.network.BGGApiDefinition;
import hu.androidworkshop.network.ImageUploader;
import hu.androidworkshop.persistence.RecommendationDatabaseHelper;
import hu.androidworkshop.places.BuildConfig;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GourmetApplication extends Application {

    private BGGApiDefinition apiDefinition;
    private ImageUploader imageUploader;

    @Override
    public void onCreate() {
        super.onCreate();

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .writeTimeout(60L, TimeUnit.SECONDS)
                .readTimeout(60L, TimeUnit.SECONDS)
                .connectTimeout(60L, TimeUnit.SECONDS)
                .build();
        apiDefinition = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .baseUrl(BuildConfig.API_BASE_URL)
                .build()
                .create(BGGApiDefinition.class);
        imageUploader = new ImageUploader(okHttpClient);
        RecommendationDatabaseHelper.getInstance(this).deleteAllPostsAndUsers();
    }

    public BGGApiDefinition getApiClient() {
        return apiDefinition;
    }

    public ImageUploader getImageUploader() {
        return imageUploader;
    }
}
