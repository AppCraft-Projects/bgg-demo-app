package hu.androidworkshop.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import hu.androidworkshop.places.R;

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
}
