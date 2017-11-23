package hu.androidworkshop;

import android.app.Application;
import android.arch.persistence.room.Room;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import hu.androidworkshop.json.JsonMapper;
import hu.androidworkshop.json.JsonMapperInterface;
import hu.androidworkshop.network.BGGApiDefinition;
import hu.androidworkshop.network.ImageUploader;
import hu.androidworkshop.network.ImageUploaderInterface;
import hu.androidworkshop.persistence.RecommendationDatabase;
import hu.androidworkshop.persistence.entity.RecommendationEntity;
import hu.androidworkshop.places.BuildConfig;
import hu.androidworkshop.repository.RecommendationRepository;
import hu.androidworkshop.repository.Repository;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GourmetApplication extends Application {

    private BGGApiDefinition apiDefinition;

    private Repository<RecommendationEntity, Integer> recommendationsRepository;
    private ImageUploaderInterface imageUploader;
    private JsonMapperInterface jsonMapper;
    private RecommendationDatabase database;

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
        database = Room.databaseBuilder(this, RecommendationDatabase.class, RecommendationDatabase.getDATABASE_NAME())
                .allowMainThreadQueries()
                .build();
        recommendationsRepository = new RecommendationRepository(apiDefinition, database);
        recommendationsRepository.clear();
    }

    public ImageUploaderInterface getImageUploader() {
        return imageUploader;
    }

    public JsonMapperInterface getJsonMapper() {
        return jsonMapper;
    }

    public Repository<RecommendationEntity,Integer> getRecommendationsRepository() {
        return recommendationsRepository;
    }
}
