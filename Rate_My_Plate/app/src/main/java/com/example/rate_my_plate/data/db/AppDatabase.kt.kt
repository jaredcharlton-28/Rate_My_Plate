package com.ratemyplate.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.ratemyplate.data.model.ReviewEntity

@Database(entities = [ReviewEntity::class], version = 1)
abstract class `AppDatabase.kt` : RoomDatabase() {
    abstract fun reviewDao(): ReviewDao

    companion object {
        @Volatile private var INSTANCE: `AppDatabase.kt`? = null

        fun getInstance(context: Context): `AppDatabase.kt` =
            INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    `AppDatabase.kt`::class.java,
                    "ratemyplate_db"
                ).build()
                INSTANCE = instance
                instance
            }
    }
}
