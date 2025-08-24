package rm.mz.parcel.data.service

import com.google.android.gms.maps.model.LatLng
import com.google.maps.DirectionsApi
import com.google.maps.GeoApiContext
import com.google.maps.model.DirectionsResult
import com.google.maps.model.TravelMode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DirectionsService @Inject constructor(
    private val geoApiContext: GeoApiContext
) {
    suspend fun getDirections(
        origin: LatLng,
        destination: LatLng
    ): Result<List<LatLng>> = withContext(Dispatchers.IO) {
        try {
            val result = DirectionsApi.newRequest(geoApiContext)
                .mode(TravelMode.DRIVING)
                .origin(com.google.maps.model.LatLng(origin.latitude, origin.longitude))
                .destination(com.google.maps.model.LatLng(destination.latitude, destination.longitude))
                .await()

            Result.success(result.decodePath())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun DirectionsResult.decodePath(): List<LatLng> {
        return routes.firstOrNull()?.overviewPolyline?.decodePath()?.map { 
            LatLng(it.lat, it.lng) 
        } ?: emptyList()
    }
} 