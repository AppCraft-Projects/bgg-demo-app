package hu.androidworkshop.repository

import android.arch.lifecycle.LiveData
import android.util.Log
import hu.androidworkshop.network.BGGApiDefinition
import hu.androidworkshop.persistence.RecommendationDatabase
import hu.androidworkshop.persistence.entity.RecommendationEntity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

interface Repository<T, in IdType> {
    fun getAll() : List<T>
    fun getAllReactive() : LiveData<List<T>>
    fun getById(id: IdType) : T
    fun getByIdAsync(id: IdType, callback: (T?) -> Unit)
    fun add(item: T, callback:(T?) -> Unit)
    fun clear()
}

class RecommendationRepository(private val apiService: BGGApiDefinition, private val database: RecommendationDatabase) : Repository<RecommendationEntity, Int> {

    companion object {
        val TAG = RecommendationRepository::class.java
    }

    override fun getAll(): List<RecommendationEntity> = database.getRecommendationsDao().getAll()

    override fun getAllReactive(): LiveData<List<RecommendationEntity>> {
        apiService.getRecommendations().enqueue(object: Callback<List<RecommendationEntity>> {
            override fun onFailure(call: Call<List<RecommendationEntity>>?, t: Throwable?) {
                Log.e(TAG.toString(), "Cannot fetch recommendations from network.", t)
            }

            override fun onResponse(call: Call<List<RecommendationEntity>>?, response: Response<List<RecommendationEntity>>?) {
                val items = response?.body()
                Thread().run {
                    items?.forEach {
                        database.getRecommendationsDao().add(it)
                    }
                }
            }
        })
        return database.getRecommendationsDao().getAllReactive()
    }

    override fun getByIdAsync(id: Int, callback: (RecommendationEntity?) -> Unit) {
        val recommendation = database.getRecommendationsDao().getById(id)
        callback(recommendation)
    }

    override fun getById(id: Int): RecommendationEntity =
            database.getRecommendationsDao().getById(id)

    override fun add(item: RecommendationEntity, callback: (RecommendationEntity?) -> Unit) {
        apiService.addRestaurant(item).enqueue(object: Callback<RecommendationEntity>{
            override fun onFailure(call: Call<RecommendationEntity>?, t: Throwable?) {
                callback(null)
            }

            override fun onResponse(call: Call<RecommendationEntity>?, response: Response<RecommendationEntity>?) {
                val recommendationEntity = response?.body()!!
                database.getRecommendationsDao().add(recommendationEntity)
                callback(recommendationEntity)
            }

        })
    }

    override fun clear() {
        database.getRecommendationsDao().deleteAll()
    }
}
