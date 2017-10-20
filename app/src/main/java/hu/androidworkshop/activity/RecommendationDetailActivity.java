package hu.androidworkshop.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class RecommendationDetailActivity extends AppCompatActivity {

    private static final String TAG = RecommendationDetailActivity.class.getSimpleName();

    public static Intent newIntent(Activity activity) {
        Intent detailsIntent = new Intent(activity, RecommendationDetailActivity.class);
        return detailsIntent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int id = savedInstanceState.getInt(NearbyActivity.RECOMMENDATION_ID_KEY_BUNDLE);
        Log.d(TAG, String.format("Id is: %d", id));

        getActionBar().setDisplayHomeAsUpEnabled(true);
    }
}
