package hu.androidworkshop.persistence.dao

import android.arch.persistence.room.*
import hu.androidworkshop.persistence.entity.RecommendationEntity

@Dao
interface RecommendationDao {
    @Query("SELECT * FROM recommendations")
    fun getAll() : List<RecommendationEntity>

    @Query("SELECT * FROM recommendations WHERE recommendations.id = :arg0")
    fun getById(id: Int) : RecommendationEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun add(entity: RecommendationEntity)

    @Query("DELETE FROM recommendations")
    fun deleteAll()
}