package rm.mz.parcel.ui.viewmodel

import android.content.Context
import android.location.Geocoder
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.Timestamp
import com.google.firebase.messaging.Constants.TAG
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import rm.mz.parcel.data.model.LocationHistory
import rm.mz.parcel.data.model.Parcel
import rm.mz.parcel.data.model.ParcelStatus
import rm.mz.parcel.data.model.Profile
import rm.mz.parcel.data.model.ShareableLocation
import rm.mz.parcel.data.model.UserSearchResult
import rm.mz.parcel.data.repository.ParcelRepository
import rm.mz.parcel.data.service.DirectionsService

import javax.inject.Inject
import java.util.*

@HiltViewModel
class ParcelViewModel @Inject constructor(
    private val repository: ParcelRepository,
    private val directionsService: DirectionsService,
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _sentParcels = MutableStateFlow<List<Parcel>>(emptyList())
    val sentParcels: StateFlow<List<Parcel>> = _sentParcels

    private val _receivedParcels = MutableStateFlow<List<Parcel>>(emptyList())
    val receivedParcels: StateFlow<List<Parcel>> = _receivedParcels

    private val _currentParcel = MutableStateFlow<Parcel?>(null)
    val currentParcel: StateFlow<Parcel?> = _currentParcel

    private val _routePoints = MutableStateFlow<List<LatLng>>(emptyList())
    val routePoints: StateFlow<List<LatLng>> = _routePoints

    private val geocoder = Geocoder(context, Locale.getDefault())

    private val _locationHistory = MutableStateFlow<List<LocationHistory>>(emptyList())
    val locationHistory: StateFlow<List<LocationHistory>> = _locationHistory

    private val _sharedLocations = MutableStateFlow<List<ShareableLocation>>(emptyList())
    val sharedLocations: StateFlow<List<ShareableLocation>> = _sharedLocations

    private val _sharingState = MutableStateFlow<SharingState>(SharingState.Initial)
    val sharingState: StateFlow<SharingState> = _sharingState

    private val _createParcelState = MutableStateFlow<CreateParcelState>(CreateParcelState.Initial)
    val createParcelState: StateFlow<CreateParcelState> = _createParcelState

    private val _userSearchResults = MutableStateFlow<List<UserSearchResult>>(emptyList())
    val userSearchResults: StateFlow<List<UserSearchResult>> = _userSearchResults

    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching

    private val _profile = MutableStateFlow<Profile?>(null)
    val profile: StateFlow<Profile?> = _profile

    init {
        loadProfile()
        loadUserParcels()
        loadSharedLocations()
    }

    private fun loadProfile() {
        viewModelScope.launch {
            try {
                val userId = auth.currentUser?.uid ?: return@launch
                val doc = firestore.collection("users").document(userId).get().await()
                _profile.value = doc.toObject(Profile::class.java)?.copy(id = doc.id)
            } catch (e: Exception) {
                Log.e(TAG, "Error loading profile", e)
            }
        }
    }

    private fun loadUserParcels() {
        auth.currentUser?.uid?.let { userId ->
            viewModelScope.launch {
                repository.getSentParcels(userId)
                    .catch { e -> 
                        Log.e("ParcelViewModel", "Error loading sent parcels", e)
                    }
                    .collect { parcels ->
                        _sentParcels.value = parcels
                    }
            }

            viewModelScope.launch {
                repository.getReceivedParcels(userId)
                    .catch { e -> 
                        Log.e("ParcelViewModel", "Error loading received parcels", e)
                    }
                    .collect { parcels ->
                        _receivedParcels.value = parcels
                    }
            }
        }
    }

    private fun loadSharedLocations() {
        auth.currentUser?.uid?.let { userId ->
            viewModelScope.launch {
                repository.getSharedLocations(userId)
                    .catch { /* Handle error */ }
                    .collect { _sharedLocations.value = it }
            }
        }
    }

    fun createParcel(
        trackingNumber: String,
        sourceAddress: String,
        destinationAddress: String,
        description: String,
        receiverEmail: String,
        estimatedDays: Int
    ) {
        viewModelScope.launch {
            _createParcelState.value = CreateParcelState.Loading

            try {
                val currentTime = Timestamp.now()
                val estimatedDelivery = Calendar.getInstance().apply {
                    time = currentTime.toDate()
                    add(Calendar.DAY_OF_MONTH, estimatedDays)
                }.time

                val parcel = Parcel(
                    trackingNumber = trackingNumber,
                    sourceAddress = sourceAddress,
                    destinationAddress = destinationAddress,
                    description = description,
                    receiverEmail = receiverEmail,
                    estimatedDeliveryTime = Timestamp(estimatedDelivery)
                )

                repository.createParcel(parcel)
                    .onSuccess {
                        _createParcelState.value = CreateParcelState.Success
                    }
                    .onFailure { error ->
                        _createParcelState.value = CreateParcelState.Error(
                            error.message ?: "Failed to create parcel"
                        )
                    }
            } catch (e: Exception) {
                _createParcelState.value = CreateParcelState.Error(
                    e.message ?: "Failed to create parcel"
                )
            }
        }
    }

    fun updateParcelLocation(parcelId: String, location: GeoPoint) {
        viewModelScope.launch {
            repository.updateParcelLocation(parcelId, location)
        }
    }

    fun updateParcelStatus(parcelId: String, status: ParcelStatus) {
        viewModelScope.launch {
            repository.updateParcelStatus(parcelId, status)
        }
    }

    fun trackParcel(parcelId: String) {
        viewModelScope.launch {
            repository.getParcelUpdates(parcelId)
                .catch { /* Handle error */ }
                .collect { _currentParcel.value = it }
        }
    }

    fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }

    fun getSourceLatLng(address: String): LatLng? {
        return try {
            geocoder.getFromLocationName(address, 1)?.firstOrNull()?.let {
                LatLng(it.latitude, it.longitude)
            }
        } catch (e: Exception) {
            null
        }
    }

    fun getDestinationLatLng(address: String): LatLng? {
        return try {
            geocoder.getFromLocationName(address, 1)?.firstOrNull()?.let {
                LatLng(it.latitude, it.longitude)
            }
        } catch (e: Exception) {
            null
        }
    }

    fun calculateRoute(sourceAddress: String, destinationAddress: String) {
        viewModelScope.launch {
            val sourceLatLng = getSourceLatLng(sourceAddress)
            val destLatLng = getDestinationLatLng(destinationAddress)

            if (sourceLatLng != null && destLatLng != null) {
                directionsService.getDirections(sourceLatLng, destLatLng)
                    .onSuccess { points ->
                        _routePoints.value = points
                    }
                    .onFailure {
                        // Handle error
                    }
            }
        }
    }

    fun loadLocationHistory(parcelId: String) {
        viewModelScope.launch {
            repository.getLocationHistory(parcelId)
                .catch { /* Handle error */ }
                .collect { _locationHistory.value = it }
        }
    }

    fun updateLocation(
        parcelId: String,
        location: GeoPoint,
        status: ParcelStatus = ParcelStatus.IN_TRANSIT,
        description: String = ""
    ) {
        viewModelScope.launch {
            repository.updateParcelLocation(parcelId, location)
            repository.addLocationHistory(parcelId, location, status, description)
        }
    }

    fun shareLocation(parcelId: String, email: String, expirationHours: Int = 24) {
        viewModelScope.launch {
            _sharingState.value = SharingState.Loading
            repository.shareLocation(parcelId, email, expirationHours)
                .onSuccess {
                    _sharingState.value = SharingState.Success
                }
                .onFailure {
                    _sharingState.value = SharingState.Error(it.message ?: "Failed to share location")
                }
        }
    }

    fun stopSharing(shareId: String) {
        viewModelScope.launch {
            repository.stopSharing(shareId)
        }
    }

    fun getCurrentUserEmail(): String? {
        return auth.currentUser?.email
    }

    fun searchUsers(query: String) {
        if (query.length < 3) {
            _userSearchResults.value = emptyList()
            return
        }

        viewModelScope.launch {
            _isSearching.value = true
            repository.searchUsers(query)
                .onSuccess { users ->
                    _userSearchResults.value = users
                }
                .onFailure {
                    _userSearchResults.value = emptyList()
                }
            _isSearching.value = false
        }
    }
}

sealed class SharingState {
    object Initial : SharingState()
    object Loading : SharingState()
    object Success : SharingState()
    data class Error(val message: String) : SharingState()
}

sealed class CreateParcelState {
    object Initial : CreateParcelState()
    object Loading : CreateParcelState()
    object Success : CreateParcelState()
    data class Error(val message: String) : CreateParcelState()
} 