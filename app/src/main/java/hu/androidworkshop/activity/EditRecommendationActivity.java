package hu.androidworkshop.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import hu.androidworkshop.adapter.NearbyAdapter;
import hu.androidworkshop.persistence.RecommendationDatabaseHelper;
import hu.androidworkshop.places.R;
import hu.androidworkshop.places.model.RecommendationModel;

public class EditRecommendationActivity extends AppCompatActivity {


    private static final int PICK_PHOTO_CODE = 1046;

    FloatingActionButton finishEditButton;
    ImageView placePhoto;
    TextView placeName;
    TextView authorInfo;
    TextView descriptionCharacterLimit;
    EditText description;

    Uri imagePath;

    int id;

    boolean hadImage;
    private RecommendationModel recommendationModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_recommendation);
        id = getIntent().getIntExtra(NearbyActivity.RECOMMENDATION_ID_KEY_BUNDLE, -1);

        recommendationModel = RecommendationDatabaseHelper.getInstance(this).getRecommendationById(id);

        placeName = findViewById(R.id.place_name);
        placeName.setText(recommendationModel.getName());
        authorInfo = findViewById(R.id.author_info);

        placePhoto = findViewById(R.id.place_photo);
        description = findViewById(R.id.place_description);

        String authorInfoText = recommendationModel.getUser().getFirstName() + " " + recommendationModel.getUser().getLastName();
        authorInfo.setText(authorInfoText);

        if (recommendationModel.getImageURL() != null) {
            hadImage = true;
            new NearbyAdapter.DownloadImageTask(placePhoto).execute(recommendationModel.getImageURL());
        }

        description.setText(recommendationModel.getShortDescription());
        descriptionCharacterLimit = findViewById(R.id.description_character_limit);
        descriptionCharacterLimit.setText(getString(R.string.description_char_limit_format, recommendationModel.getShortDescription().length()));
        description.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                descriptionCharacterLimit.setText(getString(R.string.description_char_limit_format, s.toString().length()));
            }
        });

        placePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(intent, PICK_PHOTO_CODE);
                }
            }
        });

        finishEditButton = findViewById(R.id.finish_edit_button);
        finishEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new EditRecommendationTask(EditRecommendationActivity.this, imagePath, description.getText().toString()).execute();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
            imagePath = data.getData();
            hadImage = true;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public static Intent newIntent(Activity activity) {
        Intent intent = new Intent(activity, EditRecommendationActivity.class);
        return intent;
    }

    public final class EditRecommendationTask extends AsyncTask<Void, Void, RecommendationModel> {

        private final String TAG = EditRecommendationTask.class.getSimpleName();

        private Uri imagePath;
        private String description;
        private RecommendationDatabaseHelper databaseHelper;
        private ProgressDialog progressDialog;

        public EditRecommendationTask(Context context, Uri imagePath, String description) {
            this.imagePath = imagePath;
            this.description = description;
            databaseHelper = RecommendationDatabaseHelper.getInstance(context);
            progressDialog = new ProgressDialog(context);
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
        protected void onPostExecute(RecommendationModel recommendationModel) {
            super.onPostExecute(recommendationModel);
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            Intent navIntent = NavUtils.getParentActivityIntent(EditRecommendationActivity.this);
            navIntent.putExtra(NearbyActivity.RECOMMENDATION_ID_KEY_BUNDLE, recommendationModel.getId());
            EditRecommendationActivity.this.navigateUpToFromChild(EditRecommendationActivity.this, navIntent);
        }

        @Override
        protected RecommendationModel doInBackground(Void... voids) {
            if (imagePath != null) {
                uploadImage();
            }
            String resultString;
            String inputLine;
            RecommendationModel model = null;
            try {
                HttpURLConnection connection = (HttpURLConnection) new URL("http://192.168.1.225:8080/restaurants/" + recommendationModel.getId()).openConnection();
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestMethod("POST");
                connection.setReadTimeout(15000);
                connection.setConnectTimeout(15000);
                connection.setDoInput(true);
                connection.setDoOutput(true);

                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(connection.getOutputStream());
                JSONObject jsonObject = new JSONObject()
                        .put("name", placeName.getText())
                        .put("short-desc", description)
                        .put("liked", false)
                        .put("user", new JSONObject().put("first-name", recommendationModel.getUser().getFirstName()).put("last-name", recommendationModel.getUser().getLastName()));
                if (hadImage) {
                    jsonObject = jsonObject.put("image-url", recommendationModel.getImageURL());
                }
                outputStreamWriter.write(jsonObject.toString());
                outputStreamWriter.flush();

                //Create a new InputStreamReader
                InputStreamReader streamReader;
                int code = connection.getResponseCode();
                if (code != HttpURLConnection.HTTP_OK) {
                    streamReader = new InputStreamReader(connection.getErrorStream());
                } else {
                    streamReader = new InputStreamReader(connection.getInputStream());
                }
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

                JSONObject resultObject = new JSONObject(resultString);
                model = new RecommendationModel(resultObject);
                databaseHelper.addRecommendation(model);
            } catch (IOException | JSONException e) {
                Log.e(TAG, e.getMessage(), e);
            }
            return model;
        }

        private void uploadImage() {

        }
    }
}
