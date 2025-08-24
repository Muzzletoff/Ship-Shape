package rm.mz.parcel.data.model

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem

data class ParcelMarkerItem(
    private val position: LatLng,
    private val markerTitle: String,
    private val markerSnippet: String,
    val type: MarkerType
) : ClusterItem {
    override fun getPosition(): LatLng = position
    override fun getTitle(): String = markerTitle
    override fun getSnippet(): String = markerSnippet
    override fun getZIndex(): Float = 1f
}

enum class MarkerType {
    SOURCE,
    DESTINATION,
    CURRENT
} 