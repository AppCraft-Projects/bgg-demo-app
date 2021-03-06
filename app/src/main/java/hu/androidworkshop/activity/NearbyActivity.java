package hu.androidworkshop.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
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

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import hu.androidworkshop.adapter.NearbyAdapter;
import hu.androidworkshop.persistence.RecommendationDatabaseHelper;
import hu.androidworkshop.places.R;
import hu.androidworkshop.places.model.RecommendationModel;

import static hu.androidworkshop.activity.RecommendationDetailActivity.RECOMMENDATION_ID_KEY_BUNDLE;

public class NearbyActivity extends AppCompatActivity {

    private static final String TAG = NearbyActivity.class.getClass().getSimpleName();

    private ListView listView;
    private FloatingActionButton addButton;
    private ArrayAdapter<RecommendationModel> adapter;
    private RecommendationDatabaseHelper databaseHelper;

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
        addButton = findViewById(R.id.add_recommendation_button);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(AddRecommendationActivity.newIntent(NearbyActivity.this, true));
            }
        });

        databaseHelper = RecommendationDatabaseHelper.getInstance(this);
        listView = findViewById(R.id.places_listview);

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
        new GetRecomendationTask(this).execute(null, null);
    }

    public void itemClicked(RecommendationModel recommendation, View view) {
        Intent intent = RecommendationDetailActivity.newIntent(this, true);
        intent.putExtra(RECOMMENDATION_ID_KEY_BUNDLE, recommendation.getId());
        ActivityCompat.startActivity(this, intent, null);
    }

    public class GetRecomendationTask extends AsyncTask<Void,Void,List<RecommendationModel>> {

        private ProgressDialog progressDialog;

        public GetRecomendationTask(AppCompatActivity appCompatActivity) {
            progressDialog = new ProgressDialog(appCompatActivity);
            progressDialog.setIndeterminate(true);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (!progressDialog.isShowing()) {
                progressDialog.show();
            }
        }

        @Override
        protected void onPostExecute(List<RecommendationModel> recommendationModels) {
            super.onPostExecute(recommendationModels);
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            NearbyActivity.this.renderItems();
        }

        @Override
        protected List<RecommendationModel> doInBackground(Void... voids) {
            List<RecommendationModel> result = new ArrayList<>();
            String resultString;
            String inputLine;
            try {
                HttpURLConnection connection = (HttpURLConnection) new URL("http://192.168.0.8:8080/restaurants").openConnection();
                connection.setRequestMethod("GET");
                connection.setReadTimeout(15000);
                connection.setConnectTimeout(15000);

                connection.connect();
                //Create a new InputStreamReader
                InputStreamReader streamReader = new
                        InputStreamReader(connection.getInputStream());
                //Create a new buffered reader and String Builder
                BufferedReader reader = new BufferedReader(streamReader);
                StringBuilder stringBuilder = new StringBuilder();
                //Check if the line we are reading is not null
                while((inputLine = reader.readLine()) != null){
                    stringBuilder.append(inputLine);
                }

                //Close our InputStream and Buffered reader
                reader.close();
                streamReader.close();
                //Set our result equal to our stringBuilder
                resultString = stringBuilder.toString();

                JSONArray resultArray = new JSONArray(resultString);
                for (int i = 0; i < resultArray.length(); i++) {
                    RecommendationModel model = new RecommendationModel(resultArray.getJSONObject(i));
                    databaseHelper.addRecommendation(model);
                }
            } catch (IOException | JSONException e) {
                Log.e(TAG, e.getMessage(), e);
            }
            return result;
        }
    }

    private void renderItems() {
        adapter.clear();
        adapter.addAll(databaseHelper.getRecommendations());
        adapter.notifyDataSetChanged();
    }
}
