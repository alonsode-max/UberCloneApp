package com.example.ubercloneapp.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ubercloneapp.viewmodel.RideViewModel
import com.example.ubercloneapp.navigation.AppNavigation

@Composable
fun RideHistoryScreen(
    rideVm: RideViewModel,
    onBack:  () -> Unit,
    onRideClick:(String)->Unit
) {
    LaunchedEffect(Unit) {
        rideVm.loadRideHistory()
    }

    Column(Modifier.fillMaxSize()) {
        Row(Modifier.fillMaxWidth().padding(16.dp)) {
            TextButton(onClick = onBack) { Text("← Volver") }
            Spacer(Modifier.weight(1f))
            Text("📋 Mis Viajes", fontWeight = FontWeight.Bold)
        }

        if (rideVm.rideHistory.isEmpty()) {
            Box(Modifier.fillMaxSize(),
                contentAlignment = androidx.compose.ui.Alignment.Center) {
                Text("No tienes viajes todavía",
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(rideVm.rideHistory) { ride ->
                    Card(Modifier.fillMaxWidth().clickable{onRideClick(ride.firestoreId)})
                    {
                        Column(Modifier.padding(16.dp)) {
                            Row(Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(ride.date, fontWeight = FontWeight.Bold)
                                Text("${ride.price}€",
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary)
                            }
                            Spacer(Modifier.height(4.dp))
                            Text("📍 ${ride.originName} → ${ride.destName}")
                            Text("🚗 ${ride.driverName} · ${ride.durationMins} min",
                                color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }
    }
}