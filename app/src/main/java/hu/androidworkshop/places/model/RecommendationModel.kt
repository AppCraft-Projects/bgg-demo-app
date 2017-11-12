package hu.androidworkshop.places.model

import com.google.gson.annotations.SerializedName

class RecommendationModel {

    private var id: Int? = null

    private var name: String? = null

    @SerializedName("short-desc")
    private var shortDescription: String? = null

    @SerializedName("image-url")
    private var imageURL: String? = null

    private var user: UserModel? = null

    private var liked: Boolean? = null

    fun getId(): Int? {
        return id
    }

    fun setId(id: Int?): RecommendationModel {
        this.id = id
        return this
    }

    fun getName(): String? {
        return name
    }

    fun setName(name: String): RecommendationModel {
        this.name = name
        return this
    }

    fun getShortDescription(): String? {
        return shortDescription
    }

    fun setShortDescription(shortDescription: String): RecommendationModel {
        this.shortDescription = shortDescription
        return this
    }

    fun getImageURL(): String? {
        return imageURL
    }

    fun setImageURL(imageURL: String): RecommendationModel {
        this.imageURL = imageURL
        return this
    }

    fun getUser(): UserModel? {
        return user
    }

    fun setUser(user: UserModel): RecommendationModel {
        this.user = user
        return this
    }

    fun getLiked(): Boolean? {
        return liked
    }

    fun setLiked(liked: Boolean?): RecommendationModel {
        this.liked = liked
        return this
    }

    override fun toString(): String {
        return "RecommendationModel(id=$id, name=$name, shortDescription=$shortDescription, imageURL=$imageURL, user=${user?.toString()}, liked=$liked)"
    }


}
