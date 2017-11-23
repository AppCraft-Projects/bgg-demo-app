package hu.androidworkshop.persistence.entity

import android.arch.persistence.room.Embedded
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import hu.androidworkshop.places.model.RecommendationModel

@Entity(tableName = "recommendations")
class RecommendationEntity() {
    @PrimaryKey(autoGenerate = false)
    var id: Int? = null

    var name: String = ""

    @SerializedName("short-desc")
    var shortDescription: String = ""

    @SerializedName("image-url")
    var imageUrl: String? = null

    @Embedded var user: UserEntity = UserEntity()

    var liked: Boolean = false

    constructor(id: Int?, name: String?, shortDescription: String?, imageUrl: String?, user: UserEntity?, liked: Boolean) : this() {
        this.id = id
        this.name = name!!
        this.shortDescription = shortDescription!!
        this.imageUrl = imageUrl
        this.user = user!!
        this.liked = liked
    }

    constructor(responseModel: RecommendationModel) : this(responseModel.getId(), responseModel.getName()!!, responseModel.getShortDescription()!!, responseModel.getImageURL(), UserEntity(model = responseModel.getUser()!!), responseModel.getLiked()!!)
}
