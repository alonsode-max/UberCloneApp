package com.example.ubercloneapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [RideEntity::class],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun rideDao(): RideDao
}