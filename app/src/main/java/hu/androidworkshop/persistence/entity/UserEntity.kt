package hu.androidworkshop.persistence.entity

import com.google.gson.annotations.SerializedName
import hu.androidworkshop.places.model.UserModel

class UserEntity() {

    @SerializedName("first-name") var firstName: String = ""

    @SerializedName("last-name") var lastName: String? = ""

    constructor(firstName: String, lastName: String) : this()
    constructor(model: UserModel) : this(model.getFirstName(), model.getLastName())
}