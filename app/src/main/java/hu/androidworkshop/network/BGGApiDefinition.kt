package hu.androidworkshop.network

import hu.androidworkshop.persistence.entity.RecommendationEntity
import hu.androidworkshop.places.model.RecommendationModel
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface BGGApiDefinition {
    @GET("restaurants")
    fun getRecommendations() : Call<List<RecommendationEntity>>

    @POST("restaurants")
    fun addRestaurant(@Body recommendation : RecommendationEntity) : Call<RecommendationEntity>
}