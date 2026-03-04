package com.example.ubercloneapp.data.local

import com.example.ubercloneapp.model.Ride
fun Ride.toEntity(): RideEntity=RideEntity(
    firestoreId = firestoreId,
    userId=userId,
    driverName=driverName,
    price = price,
    distanceKm=distanceKm,
    status = status,
    timestamp = System.currentTimeMillis()
)

fun RideEntity.toRide(): Ride = Ride(
    firestoreId = firestoreId,
    userId      = userId,
    driverName  = driverName,
    price       = price,
    distanceKm  = distanceKm,
    status      = status
)