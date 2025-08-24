package rm.mz.parcel.ui.components

import android.content.Context
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.view.DefaultClusterRenderer
import rm.mz.parcel.R
import rm.mz.parcel.data.model.ParcelMarkerItem
import rm.mz.parcel.data.model.MarkerType

class MarkerClusterRenderer(
    context: Context,
    map: GoogleMap,
    clusterManager: ClusterManager<ParcelMarkerItem>
) : DefaultClusterRenderer<ParcelMarkerItem>(context, map, clusterManager) {

    override fun onBeforeClusterItemRendered(item: ParcelMarkerItem, markerOptions: MarkerOptions) {
        markerOptions.apply {
            icon(
                when (item.type) {
                    MarkerType.SOURCE -> BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_source)
                    MarkerType.DESTINATION -> BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_destination)
                    MarkerType.CURRENT -> BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_current)
                }
            )
            title(item.title)
            snippet(item.snippet)
        }
    }
} 