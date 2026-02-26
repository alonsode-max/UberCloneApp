package com.example.ubercloneapp.viewmodel

import android.app.NotificationManager
import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.app.NotificationCompat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ubercloneapp.R
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import kotlinx.coroutines.tasks.await

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

import com.google.android.gms.maps.model.LatLng

import com.example.ubercloneapp.model.Ride
import javax.inject.Inject

sealed interface RideState {
    data object Idle           : RideState

    data object Searching      : RideState

    data class DriverAssigned(
        val driverName: String,
        val etaMinutes: Int
    ) : RideState

    data object InProgress     : RideState

    data object Completed      : RideState
}

class RideViewModel @Inject constructor(

    private val auth:FirebaseAuth,
    private val db : FirebaseFirestore

): ViewModel() {


    var rideState: RideState by mutableStateOf(RideState.Idle)
        private set

    var userLocation: LatLng? by mutableStateOf(null)
        private set

    var destination: LatLng? by mutableStateOf(null)
        private set

    var destinationName: String by mutableStateOf("")
        private set

    var estimatePrice: Double by mutableStateOf(0.0)
        private set

    var driverName: String by mutableStateOf("")
        private set

    var rideHistory: List<Ride> by mutableStateOf(emptyList())
        private set

    var resultMsg: String by mutableStateOf("")
        private set

    fun updateUserLocation(latLng: LatLng){
        userLocation=latLng
    }

    fun setDestination(latLng: LatLng, name: String){
        destination=latLng
        destinationName=name

        val origin=userLocation?:return
        val distKm=haversineDistance(origin,latLng)
        estimatePrice=((2.5+distKm*1.2)*100).toInt()/100.0
    }

    fun requestRide(){
        if(destination==null||userLocation==null)return
        rideState=RideState.Searching
        viewModelScope.launch {
            delay(3000)

            val drivers=listOf("Carlos M.","Ana R.","Pedro L.","Maria G.")

            driverName=drivers.random()

            val eta=(3..12).random()

            rideState=RideState.DriverAssigned(driverName,eta)

            delay(2000)
            rideState=RideState.InProgress

            val tripDuration=(8..25).random()
            delay((tripDuration*400).toLong())

            saveRideToFirestore(tripDuration)
            rideState= RideState.Completed
        }
    }

    fun sendLocalNotification(context: Context, title: String, body: String) {
        val channelId = "uber_rides"
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE)
                as NotificationManager

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .build()

        manager.notify(System.currentTimeMillis().toInt(), notification)
    }

    private fun saveRideToFirestore(durationMins: Int){
        val user=auth.currentUser?:return
        val origin=userLocation?:return
        val dest=destination?:return

        val ride=Ride(
            userId=user.uid,
            originLat=origin.latitude,
            originLng=origin.longitude,
            originName="Mi ubicación",
            destLat      = dest.latitude,
            destLng      = dest.longitude,
            destName     = destinationName,
            driverName   = driverName,
            price        = estimatePrice,
            status       = "completed",
            date         = java.time.LocalDate.now().toString(),
            durationMins = durationMins
        )
        viewModelScope.launch {
            try {
                db.collection("rides").add(ride).await()
                resultMsg = "✅ Viaje guardado"
            } catch (e: Exception) {
                resultMsg = "❌ Error: ${e.localizedMessage}"
            }
        }
    }

    fun loadRideHistory(){
        val user=auth.currentUser?:return

        viewModelScope.launch {
            try {
                val snapshot = db.collection("rides")
                    .whereEqualTo("userId", user.uid)
                    .get()
                    .await()
                rideHistory =
                    snapshot.documents.mapNotNull { doc -> doc.toObject(Ride::class.java) }
            }catch (_:Exception){
                rideHistory=emptyList()
            }
        }
    }

    fun resetRide(){
        rideState = RideState.Idle
        destination = null
        destinationName = ""
        estimatePrice = 0.0
        driverName = ""
        resultMsg = ""
    }

    private fun haversineDistance(a: LatLng, b: LatLng): Double {
        val R = 6371.0
        val dLat = Math.toRadians(b.latitude - a.latitude)
        val dLng = Math.toRadians(b.longitude - a.longitude)
        val sinLat = Math.sin(dLat / 2)
        val sinLng = Math.sin(dLng / 2)
        val h = sinLat * sinLat +
                Math.cos(Math.toRadians(a.latitude)) *
                Math.cos(Math.toRadians(b.latitude)) *
                sinLng * sinLng
        return 2 * R * Math.asin(Math.sqrt(h))
    }
}