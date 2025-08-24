package rm.mz.parcel.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint

data class LocationHistory(
    val id: String = "",
    val parcelId: String = "",
    val location: GeoPoint = GeoPoint(0.0, 0.0),
    val timestamp: Timestamp = Timestamp.now(),
    val status: ParcelStatus = ParcelStatus.PENDING,
    val description: String = ""
) 