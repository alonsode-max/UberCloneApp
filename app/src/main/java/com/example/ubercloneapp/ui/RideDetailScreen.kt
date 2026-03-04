package com.example.ubercloneapp.ui

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ubercloneapp.model.Ride
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

@Composable
fun RideDetailScreen(rideId: String?) {
    var ride      by remember { mutableStateOf<Ride?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var error     by remember { mutableStateOf<String?>(null) }
    val context   = LocalContext.current

    LaunchedEffect(rideId) {
        if (rideId == null) {
            error = "ID de viaje no válido"
            isLoading = false
            return@LaunchedEffect
        }
        try {
            val doc = FirebaseFirestore.getInstance()
                .collection("rides")
                .document(rideId)
                .get()
                .await()

            ride = doc.toObject(Ride::class.java)
            if (ride == null) error = "Viaje no encontrado"
        } catch (e: Exception) {
            error = e.localizedMessage ?: "Error al cargar el viaje"
        } finally {
            isLoading = false
        }
    }

    Column(
        Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("📋 Detalle del viaje",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(24.dp))

        when {
            isLoading -> CircularProgressIndicator()
            error != null -> Text(error!!, color = MaterialTheme.colorScheme.error)
            ride != null -> {
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(20.dp)) {
                        Text("🚗 Conductor: ${ride!!.driverName}",
                            style = MaterialTheme.typography.titleMedium)
                        Spacer(Modifier.height(8.dp))
                        Text("📍 Distancia: ${"%.1f".format(ride!!.distanceKm)} km")
                        Text("💰 Precio: ${"%.2f".format(ride!!.price)} €")
                        Text("📊 Estado: ${ride!!.status}")
                    }
                }
                Spacer(Modifier.height(24.dp))
                OutlinedButton(
                    onClick = { shareRideLink(context, rideId!!) }
                ) { Text("📤 Compartir viaje") }
            }
        }
    }
}

fun shareRideLink(context: Context, rideId: String) {
    val link = "uberclone://ride/$rideId"
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, "¡Mira mi viaje! $link")
    }
    context.startActivity(Intent.createChooser(intent, "Compartir viaje"))
}