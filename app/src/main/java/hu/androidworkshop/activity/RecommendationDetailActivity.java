package hu.androidworkshop.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import hu.androidworkshop.persistence.RecommendationDatabaseHelper;
import hu.androidworkshop.places.R;
import hu.androidworkshop.places.model.RecommendationModel;

import static hu.androidworkshop.activity.AddRecommendationActivity.NAVIGATION_FROM_NEARBY;

public class RecommendationDetailActivity extends AppCompatActivity {

    private static final String TAG = RecommendationDetailActivity.class.getSimpleName();

    public static final String RECOMMENDATION_ID_KEY_BUNDLE = "RECOMMENDATION_ID_KEY_BUNDLE";

    private RecommendationModel recommendationModel;

    private TextView placeName;

    private TextView authorInfo;

    private ImageView placePhoto;

    private TextView description;

    private FloatingActionButton editButton;

    private int id;
    private boolean fromNearby;

    public static Intent newIntent(Activity activity, boolean fromNearby) {
        Intent intent = new Intent(activity, RecommendationDetailActivity.class);
        intent.putExtra(AddRecommendationActivity.NAVIGATION_FROM_NEARBY, fromNearby);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommendation_detail);


        fromNearby = getIntent().getBooleanExtra(NAVIGATION_FROM_NEARBY, false);
        id = getIntent().getIntExtra(RECOMMENDATION_ID_KEY_BUNDLE, -1);
        recommendationModel = RecommendationDatabaseHelper.getInstance(this).getRecommendationById(id);

        placeName = findViewById(R.id.place_name);
        placeName.setText(recommendationModel.getName());
        authorInfo = findViewById(R.id.author_info);
        editButton = findViewById(R.id.edit_button);
        String authorInfoText = recommendationModel.getUser().getFirstName() + " " + recommendationModel.getUser().getLastName();
        authorInfo.setText(authorInfoText);

        placePhoto = findViewById(R.id.place_photo);
        Picasso.with(this)
                .load(recommendationModel.getImageURL())
                .placeholder(R.drawable.food)
                .error(R.drawable.food)
                .into(placePhoto);

        description = findViewById(R.id.place_description);
        description.setText(recommendationModel.getShortDescription());

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = EditRecommendationActivity.newIntent(RecommendationDetailActivity.this);
                intent.putExtra(RECOMMENDATION_ID_KEY_BUNDLE, id);
                ActivityCompat.startActivity(RecommendationDetailActivity.this, intent, null);
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (fromNearby) {
            Intent intent = new Intent(this, NearbyActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        } else {
            Intent intent = new Intent(this, MapActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }
}
