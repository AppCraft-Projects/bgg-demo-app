package hu.androidworkshop.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import hu.androidworkshop.GourmetApplication;
import hu.androidworkshop.json.JsonMapperInterface;
import hu.androidworkshop.persistence.RecommendationDatabaseHelper;
import hu.androidworkshop.places.R;
import hu.androidworkshop.places.model.Coordinate;
import hu.androidworkshop.places.model.CoordinatesModel;
import hu.androidworkshop.places.model.RecommendationModel;

import static hu.androidworkshop.activity.RecommendationDetailActivity.RECOMMENDATION_ID_KEY_BUNDLE;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = MapActivity.class.getSimpleName();
    private LatLngBounds latLngBounds;
    private JsonMapperInterface jsonMapper;

    public static Intent newIntent(Activity activity) {
        Intent intent = new Intent(activity, MapActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        return intent;
    }

    private GoogleMap map;
    private List<RecommendationModel> models;
    private List<LatLng> coordinates;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        setTitle(R.string.title_activity_map);
        GourmetApplication application = (GourmetApplication) getApplication();
        jsonMapper = application.getJsonMapper();
        models = RecommendationDatabaseHelper.getInstance(this).getRecommendations();
        findViewById(R.id.add_recommendation_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(AddRecommendationActivity.newIntent(MapActivity.this, false));
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.map_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.list_button) {
            startActivity(NearbyActivity.newIntent(this));
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.getUiSettings().setZoomGesturesEnabled(true);
        map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                Intent intent = new Intent(MapActivity.this, RecommendationDetailActivity.class);
                intent.putExtra(RECOMMENDATION_ID_KEY_BUNDLE, Integer.valueOf(marker.getSnippet()));
                startActivity(intent);
            }
        });
        map.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                coordinates = new ArrayList<>();
                InputStream inputStream = null;
                try {
                    inputStream = getResources().openRawResource(R.raw.coords);
                    StringBuilder stringBuilder = new StringBuilder();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                    String str;
                    while ((str = bufferedReader.readLine()) != null) {
                        stringBuilder.append(str).append("\n");
                    }
                    CoordinatesModel model = jsonMapper.deserialize(stringBuilder.toString(), CoordinatesModel.class);
                    for(Coordinate coordinate : model.getCoordinates()) {
                        coordinates.add(new LatLng(coordinate.getLat(), coordinate.getLon()));
                    }
                } catch (IOException e) {
                    Log.e(TAG, "Error while reading coordinates file", e);
                } finally {
                    try {
                        if (inputStream != null) {
                            inputStream.close();
                        }
                    } catch (IOException e) {
                        Log.e(TAG, "Error while closing the coordinates file :D", e);
                    }
                }
                Drawable markerIcon = getDrawable(R.drawable.ic_room_black_24dp);

                markerIcon.setTint(getResources().getColor(R.color.colorPrimary));
                Canvas canvas = new Canvas();
                Bitmap bitmap = Bitmap.createBitmap(markerIcon.getIntrinsicWidth(), markerIcon.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
                canvas.setBitmap(bitmap);
                markerIcon.setBounds(0, 0, markerIcon.getIntrinsicWidth(), markerIcon.getIntrinsicHeight());
                markerIcon.draw(canvas);
                BitmapDescriptor marker = BitmapDescriptorFactory.fromBitmap(bitmap);
                LatLngBounds.Builder builder = LatLngBounds.builder();
                LatLng latLngTmp;
                RecommendationModel modelTmp;
                for (int i = 0; i < models.size(); i++) {
                    latLngTmp = coordinates.get(i);
                    modelTmp = models.get(i);

                    MarkerOptions markerOptions = new MarkerOptions()
                            .position(latLngTmp)
                            .title(modelTmp.getName())
                            .icon(marker)
                            .draggable(false)
                            .snippet(String.valueOf(modelTmp.getId()))
                            .visible(true);
                    builder.include(latLngTmp);
                    map.addMarker(markerOptions);
                }
                latLngBounds = builder.build();
                map.animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 15));
            }
        });
    }
}
