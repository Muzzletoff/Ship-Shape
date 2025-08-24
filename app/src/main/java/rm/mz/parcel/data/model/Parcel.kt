package rm.mz.parcel.data.model

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint

data class Parcel(
    val id: String = "",
    val trackingNumber: String = "",
    val sourceAddress: String = "",
    val destinationAddress: String = "",
    val description: String = "",
    val status: ParcelStatus = ParcelStatus.PENDING,
    val createdAt: Timestamp = Timestamp.now(),
    val updatedAt: Timestamp = Timestamp.now(),
    val estimatedDeliveryTime: Timestamp? = null,
    val currentLocation: GeoPoint? = null,
    val senderId: String = "",
    val receiverId: String = "",
    val receiverEmail: String = "",
    val senderEmail: String = ""
)

enum class ParcelStatus {
    PENDING,
    PICKED_UP,
    IN_TRANSIT,
    DELIVERED,
    CANCELLED
} 