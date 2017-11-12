package hu.androidworkshop.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.List;

import hu.androidworkshop.GourmetApplication;
import hu.androidworkshop.adapter.NearbyAdapter;
import hu.androidworkshop.persistence.RecommendationDatabaseHelper;
import hu.androidworkshop.places.R;
import hu.androidworkshop.places.model.RecommendationModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static hu.androidworkshop.activity.RecommendationDetailActivity.RECOMMENDATION_ID_KEY_BUNDLE;

public class NearbyActivity extends AppCompatActivity {

    private static final String TAG = NearbyActivity.class.getClass().getSimpleName();

    private ArrayAdapter<RecommendationModel> adapter;
    private RecommendationDatabaseHelper databaseHelper;
    private ProgressDialog progressDialog;
    private GourmetApplication application;

    public static Intent newIntent(Activity activity) {
        Intent intent = new Intent(activity, NearbyActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby);
        setTitle(R.string.nearby_title);
        application = (GourmetApplication) getApplication();
        progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        FloatingActionButton addButton = findViewById(R.id.add_recommendation_button);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(AddRecommendationActivity.newIntent(NearbyActivity.this, true));
            }
        });

        databaseHelper = RecommendationDatabaseHelper.getInstance(this);
        ListView listView = findViewById(R.id.places_listview);

        adapter = new NearbyAdapter(this);


        List<RecommendationModel> models = databaseHelper.getRecommendations();
        adapter.addAll(models);
        adapter.notifyDataSetChanged();

        listView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.nearby_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.map_button) {
            startActivity(MapActivity.newIntent(this));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchRecommendations();
    }

    private void fetchRecommendations() {
        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }

        application.getApiClient().getRecommendations().enqueue(new Callback<List<RecommendationModel>>() {
            @Override
            public void onResponse(Call<List<RecommendationModel>> call, Response<List<RecommendationModel>> response) {
                if (response.isSuccessful()) {
                    for(RecommendationModel model : response.body()) {
                        databaseHelper.addRecommendation(model);
                    }
                }
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                renderItems();
            }

            @Override
            public void onFailure(Call<List<RecommendationModel>> call, Throwable t) {
                Log.e(TAG, "Error while fetching recommendations", t);
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            }
        });
    }

    public void itemClicked(RecommendationModel recommendation, View view) {
        Intent intent = RecommendationDetailActivity.newIntent(this, true);
        intent.putExtra(RECOMMENDATION_ID_KEY_BUNDLE, recommendation.getId());
        ActivityCompat.startActivity(this, intent, null);
    }

    private void renderItems() {
        adapter.clear();
        adapter.addAll(databaseHelper.getRecommendations());
        adapter.notifyDataSetChanged();
    }
}
