package com.example.ubercloneapp.ui

import android.Manifest
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

import com.example.ubercloneapp.viewmodel.RideViewModel

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun HomeMapScreen(
    rideVm: RideViewModel,
    onRequestRide: () -> Unit,
    onHistory:()->Unit,
    onLogout:()->Unit,
    onProfile:()->Unit
) {
    val context=LocalContext.current

    val locationPermission=rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)

    LaunchedEffect(locationPermission.status.isGranted) {
        if(locationPermission.status.isGranted){
            val fusedClient=LocationServices.getFusedLocationProviderClient(context)
            try {
                val location=fusedClient.lastLocation.addOnSuccessListener { loc ->
                    loc?.let{
                        rideVm.updateUserLocation(
                            LatLng(it.latitude,it.longitude)
                        )
                    }
                }
            }catch(e: SecurityException){

            }
        }
    }

    val userLoc=rideVm.userLocation
    val defaultLocation=LatLng(40.4168, -3.7038)

    val cameraPosition=rememberCameraPositionState {
        position= CameraPosition.fromLatLngZoom(
            userLoc?:defaultLocation,15f
        )
    }

    LaunchedEffect(userLoc) {
        userLoc?.let{
            cameraPosition.animate(
                com.google.android.gms.maps.CameraUpdateFactory.newLatLngZoom(it,15f)
            )
        }
    }

    Box(Modifier.fillMaxSize()){
        if(locationPermission.status.isGranted){
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPosition,
                properties = MapProperties(
                    isMyLocationEnabled = true
                ),
                uiSettings = MapUiSettings(
                    zoomControlsEnabled = true,
                    myLocationButtonEnabled = true
                )
            ){
                userLoc?.let{
                    Marker(state=rememberMarkerState(position=it),title="Tú estás aquí")
                }
            }
            Column(
                Modifier
                    .align(Alignment.BottomCenter)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = onRequestRide,
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text("¿A dónde vamos?", style = MaterialTheme.typography.titleMedium)
                }

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedButton(onClick = onHistory) {
                        Text("📋 Historial")
                    }
                    OutlinedButton(onClick = onProfile) {
                        Text("Perfil")
                    }
                    OutlinedButton(onClick = onLogout) {
                        Text("🚪 Salir")
                    }
                }
            }
        }else{
            Column(
                Modifier.fillMaxSize().padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("🗺️ Necesitamos tu ubicación",
                    style = MaterialTheme.typography.headlineSmall)
                Spacer(Modifier.height(16.dp))
                Text("Para mostrarte el mapa y encontrar conductores cerca de ti.")
                Spacer(Modifier.height(24.dp))
                Button(onClick = {
                    locationPermission.launchPermissionRequest()
                }) {
                    Text("Conceder permiso de ubicación")
                }
            }
        }
    }
}
