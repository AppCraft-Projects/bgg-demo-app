package hu.androidworkshop.repository

import hu.androidworkshop.network.BGGApiDefinition
import hu.androidworkshop.persistence.RecommendationDatabaseHelper
import hu.androidworkshop.places.model.RecommendationModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

interface Repository<T, in IdType> {
    fun getAll(callback: (List<T>?) -> Unit)
    fun getById(id: IdType, callback: (T?) -> Unit)
    fun add(item: T, callback:(T?) -> Unit)
}

class RecommendationRepository(private val apiService: BGGApiDefinition, private val databaseHelper: RecommendationDatabaseHelper) : Repository<RecommendationModel, Int> {
    override fun getById(id: Int, callback: (RecommendationModel?) -> Unit) {
        val recommendation = databaseHelper.getRecommendationById(id)
        callback(recommendation)
    }

    override fun add(item: RecommendationModel, callback: (RecommendationModel?) -> Unit) {
        apiService.addRestaurant(item).enqueue(object: Callback<RecommendationModel>{
            override fun onFailure(call: Call<RecommendationModel>?, t: Throwable?) {
                callback(null)
            }

            override fun onResponse(call: Call<RecommendationModel>?, response: Response<RecommendationModel>?) {
                databaseHelper.addRecommendation(response?.body())
                callback(response?.body())
            }

        })
    }

    override fun getAll(callback: (List<RecommendationModel>?) -> Unit) {
        apiService.getRecommendations().enqueue(object: Callback<List<RecommendationModel>>{
            override fun onFailure(call: Call<List<RecommendationModel>>?, t: Throwable?) {
                callback(null)
            }

            override fun onResponse(call: Call<List<RecommendationModel>>?, response: Response<List<RecommendationModel>>?) {
                val items = response?.body()
                for (item in items!!) {
                    databaseHelper.addRecommendation(item)
                }
                callback(items)
            }

        })
    }

}
