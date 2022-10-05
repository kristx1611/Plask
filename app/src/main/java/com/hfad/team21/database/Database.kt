package com.hfad.team21.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

// You need to increase the version number every time you change the database
@Database(
    entities = [
        BeachEntity::class,
        FavoriteEntity::class,
        BuoyEntity::class,
        WeatherDataEntity::class,
        DistanceEntity::class,
        BeachInfoEntity::class
    ],
    version = 12, exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract val appDatabaseDao: AppDatabaseDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            synchronized(this) {
                var instance = INSTANCE

                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        "beach_database"
                    )
                        .fallbackToDestructiveMigration()
                        .createFromAsset("beaches.db")
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}
