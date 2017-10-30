package hu.androidworkshop.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import hu.androidworkshop.places.R;
import hu.androidworkshop.places.model.RecommendationModel;

public class AddRecommendationActivity extends AppCompatActivity {

    public static Intent newIntent(Activity activity) {
        Intent intent = new Intent(activity, AddRecommendationActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_recommendation);
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
