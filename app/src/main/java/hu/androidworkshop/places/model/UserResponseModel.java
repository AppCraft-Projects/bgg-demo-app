package hu.androidworkshop.places.model;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

class UserResponseModel {
    public static final String TAG = UserResponseModel.class.getSimpleName();

    private String firstName;

    private String lastName;

    public UserResponseModel(JSONObject jsonObject) {
        try {
            if (jsonObject.has("first-name")) {
                firstName = jsonObject.getString("first-name");
            }
            if (jsonObject.has("last-name")) {
                lastName = jsonObject.getString("last-name");
            }
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    public String getFirstName() {
        return firstName;
    }

    public UserResponseModel setFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public String getLastName() {
        return lastName;
    }

    public UserResponseModel setLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserResponseModel that = (UserResponseModel) o;

        if (!firstName.equals(that.firstName)) return false;
        return lastName.equals(that.lastName);
    }

    @Override
    public int hashCode() {
        int result = firstName.hashCode();
        result = 31 * result + lastName.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "UserResponseModel{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                '}';
    }
}
