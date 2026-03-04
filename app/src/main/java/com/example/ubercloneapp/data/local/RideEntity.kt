package com.example.ubercloneapp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "rides")
data class RideEntity(
    @PrimaryKey
    val firestoreId: String,
    val userId: String,
    val driverName: String,
    val price: Double,
    val distanceKm: Double,
    val status: String,
    val timestamp: Long=System.currentTimeMillis()
)