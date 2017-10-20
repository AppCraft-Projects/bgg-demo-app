package hu.androidworkshop;

import android.app.Application;

import hu.androidworkshop.persistence.RecommendationDatabaseHelper;

public class GourmetApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        RecommendationDatabaseHelper.getInstance(this).deleteAllPostsAndUsers();
    }
}
