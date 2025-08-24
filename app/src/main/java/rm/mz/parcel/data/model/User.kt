package rm.mz.parcel.data.model

data class User(
    val id: String = "",
    val email: String = "",
    val displayName: String? = null,
    val fcmToken: String? = null
) 