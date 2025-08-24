package rm.mz.parcel.data.repository

import com.google.firebase.firestore.GeoPoint
import kotlinx.coroutines.flow.Flow
import rm.mz.parcel.data.model.LocationHistory
import rm.mz.parcel.data.model.ParcelStatus

interface LocationHistoryOperations {
    suspend fun addLocationHistory(
        parcelId: String,
        location: GeoPoint,
        status: ParcelStatus,
        description: String
    ): Result<String>
    fun getLocationHistory(parcelId: String): Flow<List<LocationHistory>>
} 