package rm.mz.parcel.ui.screens.parcel

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import rm.mz.parcel.data.model.ParcelStatus
import rm.mz.parcel.ui.components.ShareLocationDialog
import rm.mz.parcel.ui.viewmodel.ParcelViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParcelDetailsScreen(
    parcelId: String,
    onNavigateBack: () -> Unit,
    onTrackParcelClick: (String) -> Unit,
    viewModel: ParcelViewModel = hiltViewModel()
) {
    var showShareDialog by remember { mutableStateOf(false) }
    val parcel by viewModel.currentParcel.collectAsState()
    val sharingState by viewModel.sharingState.collectAsState()
    
    LaunchedEffect(parcelId) {
        viewModel.trackParcel(parcelId)
    }

    if (showShareDialog) {
        ShareLocationDialog(
            onDismiss = { showShareDialog = false },
            onShare = { email, hours ->
                parcel?.id?.let { parcelId ->
                    viewModel.shareLocation(parcelId, email, hours)
                }
            },
            sharingState = sharingState
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Parcel Details") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        if (parcel == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
            ) {
                // Tracking Number
                Text(
                    text = "Tracking Number",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = parcel?.trackingNumber ?: "",
                    style = MaterialTheme.typography.bodyLarge
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Status
                ParcelStatusCard(status = parcel?.status ?: ParcelStatus.PENDING)
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Addresses
                AddressSection(
                    sourceAddress = parcel?.sourceAddress ?: "",
                    destinationAddress = parcel?.destinationAddress ?: ""
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Dates
                DatesSection(
                    createdAt = parcel?.createdAt?.toDate(),
                    estimatedDelivery = parcel?.estimatedDeliveryTime?.toDate()
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Track Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { onTrackParcelClick(parcelId) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.LocationOn, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Track")
                    }

                    Button(
                        onClick = { showShareDialog = true },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Share")
                    }
                }
            }
        }
    }
}

@Composable
private fun ParcelStatusCard(status: ParcelStatus) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = when (status) {
                ParcelStatus.DELIVERED -> MaterialTheme.colorScheme.primaryContainer
                ParcelStatus.IN_TRANSIT -> MaterialTheme.colorScheme.tertiaryContainer
                ParcelStatus.PENDING -> MaterialTheme.colorScheme.surfaceVariant
                ParcelStatus.PICKED_UP -> MaterialTheme.colorScheme.secondaryContainer
                ParcelStatus.CANCELLED -> MaterialTheme.colorScheme.errorContainer
            }
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Status",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = status.name,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
private fun AddressSection(sourceAddress: String, destinationAddress: String) {
    Column {
        Text(
            text = "From",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = sourceAddress,
            style = MaterialTheme.typography.bodyLarge
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "To",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = destinationAddress,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
private fun DatesSection(createdAt: Date?, estimatedDelivery: Date?) {
    val dateFormat = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
    
    Column {
        createdAt?.let {
            Text(
                text = "Created",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = dateFormat.format(it),
                style = MaterialTheme.typography.bodyLarge
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        estimatedDelivery?.let {
            Text(
                text = "Estimated Delivery",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = dateFormat.format(it),
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
} 