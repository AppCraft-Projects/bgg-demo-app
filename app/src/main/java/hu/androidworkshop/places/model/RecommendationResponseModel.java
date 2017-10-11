package hu.androidworkshop.places.model;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class RecommendationResponseModel {

    public static final String TAG = RecommendationResponseModel.class.getSimpleName();

    private String id;

    private String name;

    private String shortDescription;

    private String imageURL;

    private UserResponseModel user;

    private Boolean liked;

    public String getId() {
        return id;
    }

    public RecommendationResponseModel setId(String id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public RecommendationResponseModel setName(String name) {
        this.name = name;
        return this;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public RecommendationResponseModel setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
        return this;
    }

    public String getImageURL() {
        return imageURL;
    }

    public RecommendationResponseModel setImageURL(String imageURL) {
        this.imageURL = imageURL;
        return this;
    }

    public UserResponseModel getUser() {
        return user;
    }

    public RecommendationResponseModel setUser(UserResponseModel user) {
        this.user = user;
        return this;
    }

    public Boolean getLiked() {
        return liked;
    }

    public RecommendationResponseModel setLiked(Boolean liked) {
        this.liked = liked;
        return this;
    }

    public RecommendationResponseModel(JSONObject jsonObject) {
        try {
            if (jsonObject.has("id")) {
                id = jsonObject.getString("id");
            }
            if (jsonObject.has("name")) {
                name = jsonObject.getString("name");
            }
            if (jsonObject.has("short-desc")) {
                shortDescription = jsonObject.getString("short-desc");
            }
            if (jsonObject.has("image-url")) {
                imageURL = jsonObject.getString("image-url");
            }
            if (jsonObject.has("user")) {
                user = new UserResponseModel(jsonObject.getJSONObject("user"));
            }
            if (jsonObject.has("liked")) {
                liked = jsonObject.getBoolean("liked");
            }
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RecommendationResponseModel that = (RecommendationResponseModel) o;

        if (!id.equals(that.id)) return false;
        if (!name.equals(that.name)) return false;
        if (!shortDescription.equals(that.shortDescription)) return false;
        if (!imageURL.equals(that.imageURL)) return false;
        if (!user.equals(that.user)) return false;
        return liked.equals(that.liked);
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + name.hashCode();
        result = 31 * result + shortDescription.hashCode();
        result = 31 * result + imageURL.hashCode();
        result = 31 * result + user.hashCode();
        result = 31 * result + liked.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "RecommendationResponseModel{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", shortDescription='" + shortDescription + '\'' +
                ", imageURL='" + imageURL + '\'' +
                ", user=" + user.toString() +
                ", liked=" + liked +
                '}';
    }
}
