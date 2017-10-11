package hu.androidworkshop.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.graphics.TypefaceCompat;
import android.support.v4.graphics.TypefaceCompatApi26Impl;
import android.support.v4.graphics.TypefaceCompatUtil;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import hu.androidworkshop.places.R;

public class NearbyActivity extends AppCompatActivity {

    public static Intent newIntent(Activity activity) {
        Intent intent = new Intent(activity, NearbyActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby);
        Toolbar toolbar = findViewById(R.id.app_toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.nearby_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.map_button) {
            startActivity(MapActivity.newIntent(this));
        }
        return super.onOptionsItemSelected(item);
    }
}
