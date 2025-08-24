package rm.mz.parcel.data.repository

import kotlinx.coroutines.flow.Flow
import rm.mz.parcel.data.model.ShareableLocation
import rm.mz.parcel.data.model.ShareOptions

interface SharingOperations {
    suspend fun shareLocation(parcelId: String, sharedWith: String, expirationHours: Int): Result<String>
    suspend fun shareParcelWithMultiple(parcelId: String, recipients: List<String>, options: ShareOptions): Result<List<String>>
    suspend fun stopSharing(shareId: String): Result<Unit>
    fun getSharedLocations(userId: String): Flow<List<ShareableLocation>>
} 