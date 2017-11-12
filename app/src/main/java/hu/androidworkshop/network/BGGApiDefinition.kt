package hu.androidworkshop.network

import hu.androidworkshop.places.model.RecommendationModel
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface BGGApiDefinition {
    @GET("restaurants")
    fun getRecommendations() : Call<List<RecommendationModel>>

    @GET("restaurants/{id}")
    fun getRecommendation(@Path("id") id: String) : Call<RecommendationModel>

    @POST("restaurants")
    fun addRestaurant(@Body recommendation : RecommendationModel) : Call<RecommendationModel>

    @Multipart
    @POST("upload")
    fun uploadImage(@Part("image") body: RequestBody) : Call<String>
}