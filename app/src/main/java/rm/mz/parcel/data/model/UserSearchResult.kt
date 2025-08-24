package rm.mz.parcel.data.model

data class UserSearchResult(
    val id: String,
    val email: String,
    val displayName: String? = null,
    val isExactMatch: Boolean = false
) 