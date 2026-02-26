package com.example.ubercloneapp.viewmodel

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ProfileViewModel : ViewModel(){
    private val auth=FirebaseAuth.getInstance()
    private val db=FirebaseFirestore.getInstance()
    private val storage=FirebaseStorage.getInstance()

    var photoUrl by mutableStateOf<String?>(null)
        private set

    var totalRides by mutableStateOf(0)
        private set

    var isUploading by mutableStateOf(false)
        private set

    val email: String
        get() = auth.currentUser?.email ?: "Sin email"

    init { loadProfile() }

    private fun loadProfile() {
        val uid = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            val rides = db.collection("rides")
                .whereEqualTo("userId", uid)
                .get().await()
            totalRides = rides.size()

            try {
                val ref = storage.reference.child("profile_photos/$uid.jpg")
                photoUrl = ref.downloadUrl.await().toString()
            } catch (_: Exception) {
            }
        }
    }
    fun uploadPhoto(imageUri: Uri) {
        val uid = auth.currentUser?.uid ?: return
        isUploading = true

        viewModelScope.launch {
            try {
                val ref = storage.reference.child("profile_photos/$uid.jpg")
                ref.putFile(imageUri).await()
                photoUrl = ref.downloadUrl.await().toString()

            } catch (e: Exception) {
                Log.e("Profile", "Error al subir foto", e)
            } finally {
                isUploading = false
            }
        }
    }
}