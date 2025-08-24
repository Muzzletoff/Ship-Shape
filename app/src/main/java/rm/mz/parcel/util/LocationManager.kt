package rm.mz.parcel.util

import android.content.Context
import android.location.Location
import com.google.android.gms.location.LocationServices
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import javax.inject.Singleton
import rm.mz.parcel.ui.viewmodel.SettingsViewModel
import kotlin.coroutines.resume
import androidx.lifecycle.ViewModelProvider
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.activity.ComponentActivity
import rm.mz.parcel.ui.viewmodel.SettingsViewModelFactory

@Singleton
class LocationManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val dataStore: DataStore<Preferences>
) {
    private val settingsViewModel by lazy {
        val factory = SettingsViewModelFactory(dataStore)
        ViewModelProvider(context as ComponentActivity, factory)[SettingsViewModel::class.java]
    }
    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    fun getCurrentLocation(): Flow<Location?> = settingsViewModel.locationTrackingEnabled
        .flatMapLatest { enabled ->
            callbackFlow {
                if (enabled) {
                    try {
                        fusedLocationClient.lastLocation
                            .addOnSuccessListener { location ->
                                trySend(location)
                            }
                            .addOnFailureListener {
                                trySend(null)
                            }
                    } catch (e: SecurityException) {
                        trySend(null)
                    }
                } else {
                    trySend(null)
                }
                awaitClose()
            }
        }

    // Alternative method using suspendCancellableCoroutine if you need a one-time location
    private suspend fun getLastLocation(): Location? = suspendCancellableCoroutine { continuation ->
        try {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    continuation.resume(location)
                }
                .addOnFailureListener {
                    continuation.resume(null)
                }
        } catch (e: SecurityException) {
            continuation.resume(null)
        }
    }
} 