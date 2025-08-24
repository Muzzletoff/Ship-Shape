package rm.mz.parcel.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint

data class ShareableLocation(
    val id: String = "",
    val parcelId: String = "",
    val sharedBy: String = "",
    val sharedWith: String = "",
    val expiresAt: Timestamp? = null,
    val location: GeoPoint = GeoPoint(0.0, 0.0),
    val trackingNumber: String = "",
    val createdAt: Timestamp = Timestamp.now(),
    val isActive: Boolean = true,
    val permissions: Set<SharePermission> = setOf(SharePermission.VIEW),
    val notifyOnUpdates: Boolean = true
) 