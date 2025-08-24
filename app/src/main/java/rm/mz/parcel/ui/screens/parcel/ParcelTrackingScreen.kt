package rm.mz.parcel.ui.screens.parcel

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import rm.mz.parcel.data.model.ParcelStatus
import rm.mz.parcel.ui.viewmodel.ParcelViewModel
import rm.mz.parcel.ui.components.*
import rm.mz.parcel.data.model.ParcelMarkerItem
import androidx.compose.ui.graphics.Color
import com.google.maps.android.clustering.ClusterManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParcelTrackingScreen(
    parcelId: String,
    onNavigateBack: () -> Unit,
    viewModel: ParcelViewModel = hiltViewModel()
) {
    val parcel by viewModel.currentParcel.collectAsState()
    val routePoints by viewModel.routePoints.collectAsState()
    val locationHistory by viewModel.locationHistory.collectAsState()
    
    val clusterManager = remember { mutableStateOf<ClusterManager<ParcelMarkerItem>?>(null) }
    var showInfoWindow by remember { mutableStateOf(false) }
    var selectedMarker by remember { mutableStateOf<ParcelMarkerItem?>(null) }

    val context = LocalContext.current

    LaunchedEffect(parcelId) {
        viewModel.trackParcel(parcelId)
        viewModel.loadLocationHistory(parcelId)
    }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(-25.9682, 32.5729), 12f)
    }

    val uiSettings by remember {
        mutableStateOf(
            MapUiSettings(
                zoomControlsEnabled = true,
                myLocationButtonEnabled = true
            )
        )
    }

    val properties by remember {
        mutableStateOf(
            MapProperties(
                isMyLocationEnabled = true,
                mapType = MapType.NORMAL
            )
        )
    }

    var map by remember { mutableStateOf<GoogleMap?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Track Parcel") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                properties = properties,
                uiSettings = uiSettings
            ) {
                MapEffect(key1 = Unit) { googleMap ->
                    clusterManager.value = ClusterManager<ParcelMarkerItem>(context, googleMap).apply {
                        renderer = MarkerClusterRenderer(context, googleMap, this)
                        setOnClusterItemClickListener { item ->
                            selectedMarker = item
                            showInfoWindow = true
                            true
                        }
                    }
                }

                if (routePoints.isNotEmpty()) {
                    Polyline(
                        points = routePoints,
                        color = Color(MaterialTheme.colorScheme.primary.toArgb()),
                        pattern = listOf(MapPatterns.DOT, MapPatterns.GAP_20),
                        width = 10f
                    )
                }

                if (showInfoWindow && selectedMarker != null) {
                    CustomMarkerInfoWindow(
                        title = selectedMarker!!.title,
                        snippet = selectedMarker!!.snippet,
                        onInfoWindowClick = {
                            showInfoWindow = false
                        }
                    )
                }
            }

            // Status Card
            parcel?.let { currentParcel ->
                Card(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(16.dp)
                        .fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = when (currentParcel.status) {
                            ParcelStatus.DELIVERED -> MaterialTheme.colorScheme.primaryContainer
                            ParcelStatus.IN_TRANSIT -> MaterialTheme.colorScheme.tertiaryContainer
                            ParcelStatus.PENDING -> MaterialTheme.colorScheme.surfaceVariant
                            ParcelStatus.PICKED_UP -> MaterialTheme.colorScheme.secondaryContainer
                            ParcelStatus.CANCELLED -> MaterialTheme.colorScheme.errorContainer
                        }
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Tracking: ${currentParcel.trackingNumber}",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Status: ${currentParcel.status}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "From: ${currentParcel.sourceAddress}",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            text = "To: ${currentParcel.destinationAddress}",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            if (parcel == null) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(48.dp)
                )
            }

            LocationHistorySection(
                history = locationHistory,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
                    .heightIn(max = 200.dp)
            )
        }
    }
}