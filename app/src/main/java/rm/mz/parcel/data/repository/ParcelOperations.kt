package rm.mz.parcel.data.repository

import com.google.firebase.firestore.GeoPoint
import kotlinx.coroutines.flow.Flow
import rm.mz.parcel.data.model.Parcel
import rm.mz.parcel.data.model.ParcelStatus

interface ParcelOperations {
    suspend fun createParcel(parcel: Parcel): Result<String>
    suspend fun updateParcelLocation(parcelId: String, location: GeoPoint): Result<Unit>
    suspend fun updateParcelStatus(parcelId: String, status: ParcelStatus): Result<Unit>
    fun getParcelUpdates(parcelId: String): Flow<Parcel>
    fun getSentParcels(userId: String): Flow<List<Parcel>>
    fun getReceivedParcels(userId: String): Flow<List<Parcel>>
} 