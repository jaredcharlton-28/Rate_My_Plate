package com.example.rate_my_plate.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface BusinessDao {

    @Query("SELECT * FROM businesses")
    suspend fun getAll(): List<BusinessEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(businesses: List<BusinessEntity>)

    @Query("DELETE FROM businesses")
    suspend fun clearAll()
}
