package hu.androidworkshop.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

import hu.androidworkshop.network.AndroidMultiPartEntity;
import hu.androidworkshop.persistence.RecommendationDatabaseHelper;
import hu.androidworkshop.places.R;
import hu.androidworkshop.places.model.RecommendationModel;

public class AddRecommendationActivity extends AppCompatActivity {

    private static final int PICK_PHOTO_CODE = 1046;
    private static final String TAG = AddRecommendationActivity.class.getSimpleName();

    FloatingActionButton finishAddButton;
    ImageButton placePhoto;
    EditText placeName;
    EditText authorInfo;
    EditText description;
    TextView descriptionCharacterLimit;

    Uri imagePath;

    int id;

    boolean hadImage;
    private RecommendationModel recommendationModel;

    public static Intent newIntent(Activity activity) {
        Intent intent = new Intent(activity, AddRecommendationActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_recommendation);
        placePhoto = findViewById(R.id.place_photo);

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
                new AddRecommendationTask(AddRecommendationActivity.this, imagePath, description.getText().toString()).execute();
            }
        });

        descriptionCharacterLimit = findViewById(R.id.description_character_limit);
        description = findViewById(R.id.place_description);
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
        private RecommendationDatabaseHelper databaseHelper;
        private ProgressDialog progressDialog;
        private String imageUrl;

        public AddRecommendationTask(Context context, Uri imagePath, String description) {
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
            Intent navIntent = NavUtils.getParentActivityIntent(AddRecommendationActivity.this);
            AddRecommendationActivity.this.navigateUpToFromChild(AddRecommendationActivity.this, navIntent);
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
                HttpURLConnection connection = (HttpURLConnection) new URL("http://192.168.1.225:8080/restaurants/").openConnection();
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestMethod("POST");
                connection.setReadTimeout(15000);
                connection.setConnectTimeout(15000);
                connection.setDoInput(true);
                connection.setDoOutput(true);

                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(connection.getOutputStream());

                String firstName = null;
                String lastName = null;
                if (!TextUtils.isEmpty(authorInfo.toString())) {
                    String[] parts = authorInfo.toString().split(" ");
                    firstName = parts[0];
                    lastName = parts[1];
                }
                JSONObject jsonObject = new JSONObject()
                        .put("name", placeName.getText().toString())
                        .put("short-desc", description)
                        .put("liked", false)
                        .put("user", new JSONObject().put("first-name", firstName).put("last-name", lastName));
                if (hadImage) {
                    jsonObject = jsonObject.put("image-url", imageUrl);
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
            String responseString = null;

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("http://192.168.1.225:8080/upload");
            File file = new File(getCacheDir(), "image.jpg");

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imagePath);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, new FileOutputStream(file));
                long totalSize = bitmap.getByteCount();
                AndroidMultiPartEntity entity = new AndroidMultiPartEntity(null);

                // Adding file data to http body
                entity.addPart("image", new FileBody(file));

                totalSize = entity.getContentLength();
                httppost.setEntity(entity);

                // Making server call
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity r_entity = response.getEntity();

                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == 200) {
                    // Server response
                    responseString = EntityUtils.toString(r_entity);
                } else {
                    responseString = "Error occurred! Http Status Code: "
                            + statusCode;
                }

            } catch (IOException e) {
                responseString = e.toString();
                Log.e(TAG, e.getMessage(), e);
                Log.e(TAG, responseString);
            }
        }
    }

//    if (imagePath != null) {
//        uploadImage();
//    }
//    String resultString;
//    String inputLine;
//            try {
//        HttpURLConnection connection = (HttpURLConnection) new URL("http://192.168.1.178:8080/restaurants/" + recommendationModel.getId()).openConnection();
//        connection.setRequestProperty("Content-Type", "application/json");
//        connection.setRequestMethod("POST");
//        connection.setReadTimeout(15000);
//        connection.setConnectTimeout(15000);
//
//        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(connection.getOutputStream());
//        JSONObject jsonObject = new JSONObject()
//                .put("name", placeName.getText())
//                .put("short-desc", description)
//                .put("liked", false)
//                .put("user", new JSONObject().put("first-name", recommendationModel.getUser().getFirstName()).put("last-name", recommendationModel.getUser().getLastName()));
//        if (hadImage) {
//            jsonObject = jsonObject.put("image-url", recommendationModel.getImageURL());
//        }
//        outputStreamWriter.write(jsonObject.toString());
//        outputStreamWriter.flush();
//
//        //Create a new InputStreamReader
//        InputStreamReader streamReader = new
//                InputStreamReader(connection.getInputStream());
//        //Create a new buffered reader and String Builder
//        BufferedReader reader = new BufferedReader(streamReader);
//        StringBuilder stringBuilder = new StringBuilder();
//        //Check if the line we are reading is not null
//        while((inputLine = reader.readLine()) != null){
//            stringBuilder.append(inputLine);
//        }
//
//        //Close our InputStream and Buffered reader
//        reader.close();
//        streamReader.close();
//        //Set our result equal to our stringBuilder
//        resultString = stringBuilder.toString();
//
//        JSONObject resultObject = new JSONObject(resultString);
//        RecommendationModel model = new RecommendationModel(resultObject);
//        databaseHelper.addRecommendation(model);
//    } catch (IOException | JSONException e) {
//        Log.e(TAG, e.getMessage(), e);
//    }
//            return null;
}
