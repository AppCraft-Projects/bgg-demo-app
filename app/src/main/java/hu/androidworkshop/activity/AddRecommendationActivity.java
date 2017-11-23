package hu.androidworkshop.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
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
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import hu.androidworkshop.GourmetApplication;
import hu.androidworkshop.persistence.entity.RecommendationEntity;
import hu.androidworkshop.places.BuildConfig;
import hu.androidworkshop.places.R;
import hu.androidworkshop.places.model.RecommendationModel;
import hu.androidworkshop.places.model.UserModel;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;

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
    private ProgressDialog progressDialog;

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
                uploadRecommendation();
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

    protected void uploadRecommendation() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }
        if (imagePath != null) {
            final String fileName = String.valueOf(System.currentTimeMillis()) + ".jpg";
            File file = new File(getCacheDir(), fileName);

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imagePath);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, new FileOutputStream(file));
            } catch (IOException e) {
                Log.e(TAG, e.getMessage(), e);
            }

            GourmetApplication gourmetApplication = (GourmetApplication) getApplication();
            gourmetApplication.getImageUploader().uploadImage(file, BuildConfig.API_BASE_URL + "upload", new okhttp3.Callback() {
                @Override
                public void onFailure(okhttp3.Call call, IOException e) {
                    Log.e(TAG, e.getMessage(), e);
                }

                @Override
                public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                    if (!response.isSuccessful()) {
                        String string = String.format("Error while uploading image.\n Status code: %d", response.code());
                        Log.e(TAG, string);
                        if (progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                        navigateBack();
                    } else {
                        String imageUrl = String.format("%sstatic/%s", BuildConfig.API_BASE_URL, fileName);
                        uploadRecommendation(imageUrl);
                    }
                }
            });
        } else {
            uploadRecommendation(null);
        }
    }

    private void uploadRecommendation(String imageUrl) {
        String firstName = null;
        String lastName = null;
        String authorInfoString = authorInfo.getText().toString();
        if (!TextUtils.isEmpty(authorInfoString)) {
            String[] parts = authorInfoString.split(" ");
            firstName = parts[0];
            lastName = parts[1];
        }
        RecommendationModel modelToPost = new RecommendationModel()
                .setName(placeName.getText().toString())
                .setShortDescription(description.getText().toString())
                .setLiked(false)
                .setUser(new UserModel().setFirstName(firstName).setLastName(lastName));
        if (hadImage && imageUrl != null) {
            modelToPost.setImageURL(imageUrl);
        }
        RecommendationEntity entity = new RecommendationEntity(recommendationModel);
        GourmetApplication gourmetApplication = (GourmetApplication) getApplication();
        gourmetApplication.getRecommendationsRepository().add(entity, new Function1<RecommendationEntity, Unit>() {
            @Override
            public Unit invoke(RecommendationEntity recommendationEntity) {
                if (recommendationEntity != null) {
                    Toast.makeText(AddRecommendationActivity.this, R.string.successful_action, Toast.LENGTH_SHORT).show();
                }
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                navigateBack();
                return null;
            }
        });
    }

    private void navigateBack() {
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