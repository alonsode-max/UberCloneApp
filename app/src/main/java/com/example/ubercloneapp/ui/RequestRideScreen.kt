package com.example.ubercloneapp.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.example.ubercloneapp.viewmodel.RideViewModel

@Composable
fun RequestRideScreen(
    rideVm:     RideViewModel,
    onRideRequested: () -> Unit,
    onBack:     () -> Unit
) {
    val userLoc = rideVm.userLocation ?: LatLng(40.4168, -3.7038)
    val dest    = rideVm.destination

    val cameraPosition = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(userLoc, 14f)
    }

    Column(Modifier.fillMaxSize()) {

        Row(
            Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = onBack) { Text("← Volver") }
            Spacer(Modifier.weight(1f))
            Text("📍 Elige tu destino", fontWeight = FontWeight.Bold)
        }

        Box(Modifier.weight(1f)) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPosition,
                onMapClick = { latLng ->
                    rideVm.setDestination(latLng, "Destino seleccionado")
                }
            ) {
                Marker(
                    state = rememberMarkerState(position = userLoc),
                    title = "Tu ubicación",
                    snippet = "Punto de recogida"
                )

                dest?.let {
                    Marker(
                        state = rememberMarkerState(position = it),
                        title = "Destino",
                        snippet = rideVm.destinationName
                    )
                }
            }
        }

        // ── Panel inferior con precio y botón ──
        Surface(
            tonalElevation = 4.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(Modifier.padding(20.dp)) {
                if (dest != null) {
                    Text("Destino: ${rideVm.destinationName}")
                    Text("Precio estimado: ${rideVm.estimatePrice}€",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleLarge)

                    Spacer(Modifier.height(16.dp))

                    Button(
                        onClick = {
                            rideVm.requestRide()
                            onRideRequested()
                        },
                        modifier = Modifier.fillMaxWidth().height(56.dp)
                    ) {
                        Text("🚗 Pedir viaje",
                            style = MaterialTheme.typography.titleMedium)
                    }
                } else {
                    Text("Toca el mapa para elegir tu destino",
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}