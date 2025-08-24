package rm.mz.parcel.data.repository

import com.google.firebase.firestore.GeoPoint
import kotlinx.coroutines.flow.Flow
import rm.mz.parcel.data.model.LocationHistory
import rm.mz.parcel.data.model.Parcel
import rm.mz.parcel.data.model.ParcelStatus
import rm.mz.parcel.data.model.ShareableLocation
import rm.mz.parcel.data.model.UserSearchResult
import rm.mz.parcel.data.model.ShareOptions

interface ParcelRepository {
    suspend fun createParcel(parcel: Parcel): Result<String>
    suspend fun updateParcelLocation(parcelId: String, location: GeoPoint): Result<Unit>
    suspend fun updateParcelStatus(parcelId: String, status: ParcelStatus): Result<Unit>
    fun getParcelUpdates(parcelId: String): Flow<Parcel>
    fun getSentParcels(userId: String): Flow<List<Parcel>>
    fun getReceivedParcels(userId: String): Flow<List<Parcel>>
    suspend fun addLocationHistory(parcelId: String, location: GeoPoint, status: ParcelStatus, description: String): Result<String>
    fun getLocationHistory(parcelId: String): Flow<List<LocationHistory>>
    suspend fun shareLocation(parcelId: String, sharedWith: String, expirationHours: Int = 24): Result<String>
    suspend fun stopSharing(shareId: String): Result<Unit>
    fun getSharedLocations(userId: String): Flow<List<ShareableLocation>>
    suspend fun findUserByEmail(email: String): Result<String>
    suspend fun getUserEmail(userId: String): Result<String>
    suspend fun searchUsers(query: String): Result<List<UserSearchResult>>
    suspend fun updateFcmToken(token: String): Result<Unit>
    suspend fun sendParcelNotification(
        userId: String,
        title: String,
        body: String,
        parcelId: String
    ): Result<Unit>
    suspend fun shareParcelWithMultiple(
        parcelId: String,
        recipients: List<String>,
        options: ShareOptions
    ): Result<List<String>>
} 