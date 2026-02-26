package com.example.ubercloneapp.ui

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

import com.example.ubercloneapp.viewmodel.ProfileViewModel

@Composable
fun ProfileScreen(profileVm: ProfileViewModel, onBack: () -> Unit) {

    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { profileVm.uploadPhoto(it) }
    }

    Column(
        Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextButton(onClick = onBack) { Text("← Volver") }
        Spacer(Modifier.height(48.dp))

        if (profileVm.photoUrl != null) {
            AsyncImage(
                model = profileVm.photoUrl,
                contentDescription = "Foto de perfil",
                modifier = Modifier.size(120.dp).clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        } else {
            Surface(
                modifier = Modifier.size(120.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Text("👤",
                    modifier = Modifier.wrapContentSize(),
                    style = MaterialTheme.typography.headlineLarge)
            }
        }

        Spacer(Modifier.height(16.dp))

        OutlinedButton(
            onClick = { galleryLauncher.launch("image/*") },
            enabled = !profileVm.isUploading
        ) {
            if (profileVm.isUploading)
                CircularProgressIndicator(Modifier.size(16.dp))
            else
                Text("📷 Cambiar foto")
        }

        Spacer(Modifier.height(32.dp))

        // ── Datos del usuario ──
        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(24.dp)) {
                Text("Email", style = MaterialTheme.typography.labelMedium)
                Text(profileVm.email,
                    style = MaterialTheme.typography.bodyLarge)
                Spacer(Modifier.height(16.dp))
                Text("Total de viajes",
                    style = MaterialTheme.typography.labelMedium)
                Text("${profileVm.totalRides} viajes",
                    style = MaterialTheme.typography.headlineMedium)
            }
        }
    }
}