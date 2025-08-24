package rm.mz.parcel.data.repository.impl

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.GeoPoint
import kotlinx.coroutines.flow.Flow
import rm.mz.parcel.data.model.Parcel
import rm.mz.parcel.data.model.ParcelStatus
import rm.mz.parcel.data.repository.ParcelOperations

class ParcelOperationsImpl(
    private val parcelsCollection: CollectionReference,
    private val usersCollection: CollectionReference,
    private val currentUserId: String
) : ParcelOperations {
    
    override suspend fun createParcel(parcel: Parcel): Result<String> {
        TODO("Not yet implemented")
    }

    override suspend fun updateParcelLocation(parcelId: String, location: GeoPoint): Result<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun updateParcelStatus(parcelId: String, status: ParcelStatus): Result<Unit> {
        TODO("Not yet implemented")
    }

    override fun getParcelUpdates(parcelId: String): Flow<Parcel> {
        TODO("Not yet implemented")
    }

    override fun getSentParcels(userId: String): Flow<List<Parcel>> {
        TODO("Not yet implemented")
    }

    override fun getReceivedParcels(userId: String): Flow<List<Parcel>> {
        TODO("Not yet implemented")
    }
} 