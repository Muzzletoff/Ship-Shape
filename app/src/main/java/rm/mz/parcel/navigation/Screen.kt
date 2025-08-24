package rm.mz.parcel.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Login : Screen("login")
    object Register : Screen("register")
    object ParcelList : Screen("parcel_list")
    object ParcelDetails : Screen("parcel_details/{parcelId}") {
        fun createRoute(parcelId: String) = "parcel_details/$parcelId"
    }
    object ParcelTracking : Screen("parcel_tracking/{parcelId}") {
        fun createRoute(parcelId: String) = "parcel_tracking/$parcelId"
    }
    object CreateParcel : Screen("create_parcel")
    object Profile : Screen("profile")
    object Settings : Screen("settings")
    object Help : Screen("help")
    object Home : Screen("home")
    object SentParcels : Screen("sent_parcels")
    object ReceivedParcels : Screen("received_parcels")
} 