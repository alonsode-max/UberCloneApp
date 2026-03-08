package com.example.ubercloneapp.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.ubercloneapp.ui.theme.UberCloneTheme
import com.example.ubercloneapp.viewmodel.AuthState
import com.example.ubercloneapp.viewmodel.AuthViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun LoginScreen(
    authVm:      AuthViewModel,
    onLoginOk:   () -> Unit,
    onGoRegister: () -> Unit
) {
    var email    by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val state = authVm.authState
    val context = LocalContext.current

    LaunchedEffect(state) {
        if (state is AuthState.Authenticated) onLoginOk()
    }
    Scaffold(Modifier.fillMaxSize().background(color = MaterialTheme.colorScheme.background)) {
        Column(
            Modifier.fillMaxSize().padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("🚗 UberClone",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary)

            Text("Inicia sesión para continuar",
                color = MaterialTheme.colorScheme.primary)

            Spacer(Modifier.height(32.dp))
            OutlinedButton(
                onClick = { authVm.signInWithGoogle(context) },
                enabled = state !is AuthState.Loading,
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                Text("🔵 Iniciar con Google")
            }

            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )

            if (state is AuthState.Error) {
                Text(state.msg, color = MaterialTheme.colorScheme.error)
                Spacer(Modifier.height(12.dp))
            }

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = { authVm.login(email.trim(), password) },
                enabled = state !is AuthState.Loading,
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                if (state is AuthState.Loading)
                    CircularProgressIndicator(Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp)
                else
                    Text("Iniciar Sesión")
            }

            Spacer(Modifier.height(16.dp))

            TextButton(onClick = onGoRegister) {
                Text("¿No tienes cuenta? Regístrate")
            }
        }
    }

}