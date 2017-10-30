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

import hu.androidworkshop.adapter.NearbyAdapter;
import hu.androidworkshop.cache.ImageCache;
import hu.androidworkshop.persistence.RecommendationDatabaseHelper;
import hu.androidworkshop.places.R;
import hu.androidworkshop.places.model.RecommendationModel;

public class RecommendationDetailActivity extends AppCompatActivity {

    private static final String TAG = RecommendationDetailActivity.class.getSimpleName();

    private RecommendationModel recommendationModel;

    private TextView placeName;

    private TextView authorInfo;

    private ImageView placePhoto;

    private TextView description;

    private FloatingActionButton editButton;

    private int id;

    public static Intent newIntent(Activity activity) {
        return new Intent(activity, RecommendationDetailActivity.class);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommendation_detail);


        id = getIntent().getIntExtra(NearbyActivity.RECOMMENDATION_ID_KEY_BUNDLE, -1);
        recommendationModel = RecommendationDatabaseHelper.getInstance(this).getRecommendationById(id);

        placeName = findViewById(R.id.place_name);
        placeName.setText(recommendationModel.getName());
        authorInfo = findViewById(R.id.author_info);
        editButton = findViewById(R.id.edit_button);
        String authorInfoText = recommendationModel.getUser().getFirstName() + " " + recommendationModel.getUser().getLastName();
        authorInfo.setText(authorInfoText);

        placePhoto = findViewById(R.id.place_photo);
        if (!ImageCache.getInstance().contains(recommendationModel.getImageURL())) {
            new NearbyAdapter.DownloadImageTask(placePhoto).execute(recommendationModel.getImageURL());
        } else {
            placePhoto.setImageBitmap(ImageCache.getInstance().get(recommendationModel.getImageURL()));
        }

        description = findViewById(R.id.place_description);
        description.setText(recommendationModel.getShortDescription());

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = EditRecommendationActivity.newIntent(RecommendationDetailActivity.this);
                intent.putExtra(NearbyActivity.RECOMMENDATION_ID_KEY_BUNDLE, id);
                ActivityCompat.startActivity(RecommendationDetailActivity.this, intent, null);
            }
        });
    }

    @Override
    public void onBackPressed() {
        ActivityCompat.finishAfterTransition(this);
    }
}
