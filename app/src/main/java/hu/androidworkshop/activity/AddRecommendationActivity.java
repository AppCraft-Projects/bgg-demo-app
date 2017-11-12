package hu.androidworkshop.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import hu.androidworkshop.GourmetApplication;
import hu.androidworkshop.network.AndroidMultiPartEntity;
import hu.androidworkshop.persistence.RecommendationDatabaseHelper;
import hu.androidworkshop.places.R;
import hu.androidworkshop.places.model.RecommendationModel;
import hu.androidworkshop.places.model.UserModel;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Response;

public class AddRecommendationActivity extends AppCompatActivity {

    private static final int PICK_PHOTO_CODE = 1046;
    private static final String TAG = AddRecommendationActivity.class.getSimpleName();
    public static final String NAVIGATION_FROM_NEARBY = "NAVIGATION_FROM_NEARBY";

    FloatingActionButton finishAddButton;
    ImageButton placePhoto;
    EditText placeName;
    EditText authorInfo;
    EditText description;
    TextView descriptionCharacterLimit;

    Uri imagePath;

    boolean hadImage;
    private RecommendationModel recommendationModel;
    private boolean fromNearby;

    public static Intent newIntent(Activity activity, boolean fromNearby) {
        Intent intent = new Intent(activity, AddRecommendationActivity.class);
        intent.putExtra(NAVIGATION_FROM_NEARBY, fromNearby);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_recommendation);
        placePhoto = findViewById(R.id.place_photo);
        placeName = findViewById(R.id.place_name);
        authorInfo = findViewById(R.id.author_info);
        description = findViewById(R.id.place_description);

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

        finishAddButton = findViewById(R.id.finish_add_button);
        finishAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AddRecommendationTask(
                        AddRecommendationActivity.this,
                        imagePath,
                        description.getText().toString(),
                        placeName.getText().toString(),
                        authorInfo.getText().toString()).execute();
            }
        });

        descriptionCharacterLimit = findViewById(R.id.description_character_limit);
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

        fromNearby = getIntent().getBooleanExtra(NAVIGATION_FROM_NEARBY, false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!hadImage) {
            placePhoto.setBackgroundColor(Color.GRAY);
        } else {
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imagePath);
            } catch (IOException e) {
                Log.e(TAG, e.getMessage(), e);
            }
            BitmapDrawable bitmapDrawable = new BitmapDrawable(getResources(), bitmap);
            placePhoto.setBackground(bitmapDrawable);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
            imagePath = data.getData();
            hadImage = true;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public final class AddRecommendationTask extends AsyncTask<Void, Void, RecommendationModel> {

        private final String TAG = EditRecommendationActivity.EditRecommendationTask.class.getSimpleName();

        private Uri imagePath;
        private String description;
        private String placeName;
        private String authorInfo;
        private RecommendationDatabaseHelper databaseHelper;
        private ProgressDialog progressDialog;
        private String imageUrl;

        public AddRecommendationTask(Context context, Uri imagePath, String description, String placeName, String authorInfo) {
            this.imagePath = imagePath;
            this.description = description;
            this.placeName = placeName;
            this.authorInfo = authorInfo;
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
            if (fromNearby) {
                Intent intent = new Intent(AddRecommendationActivity.this, NearbyActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            } else {
                Intent intent = new Intent(AddRecommendationActivity.this, MapActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        }

        @Override
        protected RecommendationModel doInBackground(Void... voids) {
            if (imagePath != null) {
                uploadImage();
            }
//            String resultString;
//            String inputLine;
            RecommendationModel model = null;
            try {
//                HttpURLConnection connection = (HttpURLConnection) new URL("http://192.168.0.8:8080/restaurants/").openConnection();
//                connection.setRequestProperty("Content-Type", "application/json");
//                connection.setRequestMethod("POST");
//                connection.setReadTimeout(15000);
//                connection.setConnectTimeout(15000);
//                connection.setDoInput(true);
//                connection.setDoOutput(true);
//
//                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(connection.getOutputStream());

                String firstName = null;
                String lastName = null;
                if (!TextUtils.isEmpty(authorInfo)) {
                    String[] parts = authorInfo.split(" ");
                    firstName = parts[0];
                    lastName = parts[1];
                }
                RecommendationModel modelToPost = new RecommendationModel()
                        .setName(placeName)
                        .setShortDescription(description)
                        .setLiked(false)
                        .setUser(new UserModel().setFirstName(firstName).setLastName(lastName));
//                JSONObject jsonObject = new JSONObject()
//                        .put("name", placeName)
//                        .put("short-desc", description)
//                        .put("liked", false)
//                        .put("user", new JSONObject().put("first-name", firstName).put("last-name", lastName));
                if (hadImage) {
                    modelToPost.setImageURL(imageUrl);
//                    jsonObject = jsonObject.put("image-url", imageUrl);
                }
//                outputStreamWriter.write(jsonObject.toString());
//                outputStreamWriter.flush();
//
//                //Create a new InputStreamReader
//                InputStreamReader streamReader;
//                int code = connection.getResponseCode();
//                if (code != HttpURLConnection.HTTP_OK) {
//                    streamReader = new InputStreamReader(connection.getErrorStream());
//                } else {
//                    streamReader = new InputStreamReader(connection.getInputStream());
//                }
//                //Create a new buffered reader and String Builder
//                BufferedReader reader = new BufferedReader(streamReader);
//                StringBuilder stringBuilder = new StringBuilder();
//                //Check if the line we are reading is not null
//                while ((inputLine = reader.readLine()) != null) {
//                    stringBuilder.append(inputLine);
//                }
//
//                //Close our InputStream and Buffered reader
//                reader.close();
//                streamReader.close();
//                //Set our result equal to our stringBuilder
//                resultString = stringBuilder.toString();
//
//                JSONObject resultObject = new JSONObject(resultString);
//                model = new RecommendationModel(resultObject);
                GourmetApplication gourmetApplication = (GourmetApplication) getApplication();
                Response<RecommendationModel> response = gourmetApplication.getApiClient().addRestaurant(modelToPost).execute();
                if (response.isSuccessful()) {
                    databaseHelper.addRecommendation(response.body());
                    model = response.body();
                }
            } catch (IOException e) {
                Log.e(TAG, e.getMessage(), e);
            }
            return model;
        }

        private void uploadImage() {
            String responseString = null;
//
//            HttpClient httpclient = new DefaultHttpClient();
//            HttpPost httppost = new HttpPost("http://192.168.0.8:8080/upload");
            String fileName = String.valueOf(System.currentTimeMillis()) + ".jpg";
            File file = new File(getCacheDir(), fileName);

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imagePath);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, new FileOutputStream(file));

//                AndroidMultiPartEntity entity = new AndroidMultiPartEntity(null);
                GourmetApplication gourmetApplication = (GourmetApplication) getApplication();
                RequestBody requestBody = MultipartBody.create(MediaType.parse("image/jpg"), file);
                Response<String> response = gourmetApplication.getApiClient().uploadImage(requestBody).execute();
                if (response.isSuccessful()) {
                    Log.d(TAG, "uploadImage: Yeah success");
                }

//                // Adding file data to http body
//                entity.addPart("image", new FileBody(file));
//
//                totalSize = entity.getContentLength();
//                httppost.setEntity(entity);
//
//                // Making server call
//                HttpResponse response = httpclient.execute(httppost);
//                HttpEntity r_entity = response.getEntity();
//
//                int statusCode = response.getStatusLine().getStatusCode();
//                if (statusCode == 200) {
//                    // Server response
//                    responseString = EntityUtils.toString(r_entity);
//                    Log.d(TAG, String.format("Image upload success.\nResponse:\n%s", responseString));
//                    imageUrl = "http://192.168.0.8:8080/static/" + fileName;
//                } else {
//                    responseString = "Error occurred! Http Status Code: "
//                            + statusCode;
//                    Log.e(TAG, String.format("Image upload error!\n%s", responseString));
//                }

            } catch (IOException e) {
                responseString = e.toString();
                Log.e(TAG, e.getMessage(), e);
                Log.e(TAG, responseString);
            }
        }
    }
}