package rm.mz.parcel.ui.viewmodel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import rm.mz.parcel.data.model.Profile
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage
) : ViewModel() {

    private val _profile = MutableStateFlow<Profile?>(null)
    val profile: StateFlow<Profile?> = _profile

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    init {
        loadProfile()
    }

    private fun loadProfile() {
        viewModelScope.launch {
            try {
                val userId = auth.currentUser?.uid ?: return@launch
                val doc = firestore.collection("users").document(userId).get().await()
                _profile.value = doc.toObject(Profile::class.java)?.copy(id = doc.id) ?: createDefaultProfile(userId)
            } catch (e: Exception) {
                _error.value = "Error loading profile: ${e.message}"
                Log.e("ProfileViewModel", "Error loading profile", e)
            }
        }
    }

    private suspend fun createDefaultProfile(userId: String): Profile {
        val defaultProfile = Profile(
            id = userId,
            email = auth.currentUser?.email ?: "",
            displayName = auth.currentUser?.displayName ?: "",
            gender = ""
        )
        firestore.collection("users").document(userId).set(defaultProfile).await()
        return defaultProfile
    }

    fun updateProfile(displayName: String, gender: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val userId = auth.currentUser?.uid ?: return@launch
                
                val updates = mapOf(
                    "displayName" to displayName,
                    "gender" to gender
                )
                
                firestore.collection("users").document(userId)
                    .update(updates)
                    .await()
                
                loadProfile()
                _error.value = null
            } catch (e: Exception) {
                _error.value = "Error updating profile: ${e.message}"
                Log.e("ProfileViewModel", "Error updating profile", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun uploadProfilePicture(uri: Uri) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val userId = auth.currentUser?.uid ?: return@launch
                val storageRef = storage.reference.child("profile_pictures/$userId.jpg")
                
                // Upload the file
                storageRef.putFile(uri).await()
                
                // Get the download URL
                val downloadUrl = storageRef.downloadUrl.await().toString()
                
                // Update Firestore with the new photo URL
                firestore.collection("users").document(userId)
                    .update("photoUrl", downloadUrl)
                    .await()
                
                loadProfile()
                _error.value = null
            } catch (e: Exception) {
                _error.value = "Error uploading profile picture: ${e.message}"
                Log.e("ProfileViewModel", "Error uploading profile picture", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearError() {
        _error.value = null
    }
} 