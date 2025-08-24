package rm.mz.parcel.data.model

data class ShareOptions(
    val duration: ShareDuration = ShareDuration.ONE_DAY,
    val permissions: Set<SharePermission> = setOf(SharePermission.VIEW),
    val notifyOnUpdates: Boolean = true
)

enum class ShareDuration(val hours: Int) {
    ONE_HOUR(1),
    TWELVE_HOURS(12),
    ONE_DAY(24),
    THREE_DAYS(72),
    ONE_WEEK(168)
}

enum class SharePermission {
    VIEW,
    UPDATE_STATUS,
    UPDATE_LOCATION,
    SHARE_WITH_OTHERS
} 