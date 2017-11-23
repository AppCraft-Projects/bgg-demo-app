package hu.androidworkshop.persistence

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import hu.androidworkshop.persistence.dao.RecommendationDao
import hu.androidworkshop.persistence.entity.RecommendationEntity

@Database(entities = arrayOf(RecommendationEntity::class), version = 1)
abstract class RecommendationDatabase : RoomDatabase() {

    abstract fun getRecommendationsDao(): RecommendationDao

    companion object {
        @JvmStatic val DATABASE_NAME = "recommendations"
    }
}