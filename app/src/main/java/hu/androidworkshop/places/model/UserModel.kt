package hu.androidworkshop.places.model

import com.google.gson.annotations.SerializedName

class UserModel {
    @SerializedName("first-name")
    private var firstName: String = ""

    @SerializedName("last-name")
    private var lastName: String = ""

    fun setFirstName(firstName: String) : UserModel {
        this.firstName = firstName
        return this
    }

    fun getFirstName() : String = firstName

    fun setLastName(lastName: String) : UserModel {
        this.lastName = lastName
        return this
    }

    fun getLastName() : String = lastName

    override fun toString(): String = "UserModel(firstName=$firstName, lastName=$lastName)"


}
