package hu.androidworkshop.places.model

import com.google.gson.annotations.SerializedName

class UserModel {
    @SerializedName("first-name")
    private var firstName: String? = null

    @SerializedName("last-name")
    private var lastName: String? = null

    fun setFirstName(firstName: String) : UserModel {
        this.firstName = firstName
        return this
    }

    fun getFirstName() : String? {
        return firstName
    }

    fun setLastName(lastName: String) : UserModel {
        this.lastName = lastName
        return this
    }

    fun getLastName() : String? {
        return lastName
    }

    override fun toString(): String {
        return "UserModel(firstName=$firstName, lastName=$lastName)"
    }


}
