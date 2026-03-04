package com.example.ubercloneapp.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface RideDao{
    @Insert(onConflict= OnConflictStrategy.REPLACE)
    suspend fun insertAll(rides: List<RideEntity>)

    @Query("SELECT * FROM rides WHERE userId= :uid ORDER BY timestamp DESC")
    fun getRidesByUser(uid:String): Flow<List<RideEntity>>

    @Query("DELETE from rides")
    suspend fun clearAll()
}