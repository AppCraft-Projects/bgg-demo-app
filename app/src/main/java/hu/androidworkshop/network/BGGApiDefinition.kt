package hu.androidworkshop.network

import hu.androidworkshop.places.model.RecommendationModel
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface BGGApiDefinition {
    @GET("restaurants")
    fun getRecommendations() : Call<List<RecommendationModel>>

    @POST("restaurants")
    fun addRestaurant(@Body recommendation : RecommendationModel) : Call<RecommendationModel>
}