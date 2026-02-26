package com.example.ubercloneapp.viewmodel

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

sealed interface AuthState {
    data object Idle          : AuthState
    data object Loading       : AuthState
    data object Authenticated : AuthState
    data class  Error(val msg: String) : AuthState
}

class AuthViewModel @Inject constructor(
    private val auth: FirebaseAuth
):ViewModel() {

    var authState: AuthState by mutableStateOf(AuthState.Idle)
        private set

    val currentUser: FirebaseUser?
        get() = auth.currentUser

    val isLoggedIn: Boolean
        get() = auth.currentUser != null

    fun register(email: String, password: String) {
        authState = AuthState.Loading
        viewModelScope.launch {
            authState = try {
                auth.createUserWithEmailAndPassword(email, password).await()
                AuthState.Authenticated
            } catch (e: Exception) {
                AuthState.Error(e.localizedMessage ?: "Error al registrar")
            }
        }
    }

    fun login(email: String, password: String) {
        authState = AuthState.Loading
        viewModelScope.launch {
            authState = try {
                auth.signInWithEmailAndPassword(email, password).await()
                AuthState.Authenticated
            } catch (e: Exception) {
                AuthState.Error(e.localizedMessage ?: "Error al iniciar sesión")
            }
        }
    }

    companion object {
        private const val WEB_CLIENT_ID =
            "131023826531-dnovcp0sac05dbe3q660mtqnbkopvmks.apps.googleusercontent.com"
    }

    fun signInWithGoogle(context: Context) {
        authState = AuthState.Loading

        viewModelScope.launch {
            try {
                val credentialManager = CredentialManager.create(context)

                val googleIdOption = GetGoogleIdOption.Builder()
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId(WEB_CLIENT_ID)
                    .build()

                val request = GetCredentialRequest.Builder()
                    .addCredentialOption(googleIdOption)
                    .build()

                val result = credentialManager.getCredential(
                    request = request,
                    context = context
                )
                val credential = result.credential

                if (credential is CustomCredential &&
                    credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
                ) {
                    // ⑥ Parsear el token
                    val googleIdToken = GoogleIdTokenCredential
                        .createFrom(credential.data)
                        .idToken
                    val firebaseCredential = GoogleAuthProvider
                        .getCredential(googleIdToken, null)
                    auth.signInWithCredential(firebaseCredential).await()

                    authState = AuthState.Authenticated

                } else {
                    authState = AuthState.Error("Tipo de credencial inesperado")
                }

            } catch (e: Exception) {
                authState = AuthState.Error(
                    e.localizedMessage ?: "Error al iniciar con Google"
                )
            }
        }
    }

    fun logout() {
        auth.signOut()
        authState = AuthState.Idle
    }

    fun clearError() { authState = AuthState.Idle }
}