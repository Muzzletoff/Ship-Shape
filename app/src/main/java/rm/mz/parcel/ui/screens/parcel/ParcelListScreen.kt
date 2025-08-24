package rm.mz.parcel.ui.screens.parcel

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import rm.mz.parcel.data.model.Parcel
import rm.mz.parcel.data.model.ParcelStatus
import rm.mz.parcel.ui.components.DrawerContent
import rm.mz.parcel.ui.components.StylishCard
import rm.mz.parcel.ui.components.StylishTopAppBar
import rm.mz.parcel.ui.theme.Info
import rm.mz.parcel.ui.theme.Secondary
import rm.mz.parcel.ui.theme.Success
import rm.mz.parcel.ui.theme.Warning
import rm.mz.parcel.ui.theme.Error
import rm.mz.parcel.ui.viewmodel.ParcelViewModel
import rm.mz.parcel.ui.viewmodel.SharingState
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParcelListScreen(
    onParcelClick: (String) -> Unit,
    onCreateParcelClick: () -> Unit,
    onLogout: () -> Unit,
    onProfileClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onHelpClick: () -> Unit,
    viewModel: ParcelViewModel = hiltViewModel()
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var showMenu by remember { mutableStateOf(false) }
    val sentParcels by viewModel.sentParcels.collectAsState(initial = emptyList())
    val receivedParcels by viewModel.receivedParcels.collectAsState(initial = emptyList())
    val profile by viewModel.profile.collectAsState(initial = null)

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerContent(
                currentUserEmail = viewModel.getCurrentUserEmail(),
                onProfileClick = {
                    scope.launch {
                        drawerState.close()
                        onProfileClick()
                    }
                },
                onSettingsClick = {
                    scope.launch {
                        drawerState.close()
                        onSettingsClick()
                    }
                },
                onHelpClick = {
                    scope.launch {
                        drawerState.close()
                        onHelpClick()
                    }
                },
                onLogoutClick = onLogout,
                onCloseDrawer = {
                    scope.launch { drawerState.close() }
                },
                onSentParcelsClick = { 
                    scope.launch { drawerState.close() }
                },
                onReceivedParcelsClick = {
                    scope.launch { drawerState.close() }
                },
                profile = profile
            )
        }
    ) {
        Scaffold(
            topBar = {
                StylishTopAppBar(
                    title = "My Parcels",
                    navigationIcon = Icons.Default.Menu,
                    onNavigationClick = {
                        scope.launch { drawerState.open() }
                    }
                )
            },
            floatingActionButton = {
                FloatingActionButton(onClick = onCreateParcelClick) {
                    Icon(Icons.Default.Add, contentDescription = "Create Parcel")
                }
            }
        ) { padding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                item {
                    Text(
                        "Sent Parcels",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(16.dp)
                    )
                }
                
                items(sentParcels) { parcel ->
                    ParcelItem(
                        parcel = parcel,
                        onClick = { onParcelClick(parcel.id) }
                    )
                }

                item {
                    Text(
                        "Received Parcels",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(16.dp)
                    )
                }
                
                items(receivedParcels) { parcel ->
                    ParcelItem(
                        parcel = parcel,
                        onClick = { onParcelClick(parcel.id) }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParcelItem(
    parcel: Parcel,
    onClick: () -> Unit
) {
    StylishCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Tracking: ${parcel.trackingNumber}",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                StatusChip(status = parcel.status)
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            AddressInfo(
                label = "From",
                address = parcel.sourceAddress,
                icon = Icons.Default.LocationOn
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            AddressInfo(
                label = "To",
                address = parcel.destinationAddress,
                icon = Icons.Default.Place
            )
        }
    }
}

@Composable
private fun StatusChip(status: ParcelStatus) {
    Surface(
        shape = MaterialTheme.shapes.small,
        color = when (status) {
            ParcelStatus.DELIVERED -> Success
            ParcelStatus.IN_TRANSIT -> Info
            ParcelStatus.PENDING -> Warning
            ParcelStatus.PICKED_UP -> Secondary
            ParcelStatus.CANCELLED -> Error
        }.copy(alpha = 0.2f),
        contentColor = when (status) {
            ParcelStatus.DELIVERED -> Success
            ParcelStatus.IN_TRANSIT -> Info
            ParcelStatus.PENDING -> Warning
            ParcelStatus.PICKED_UP -> Secondary
            ParcelStatus.CANCELLED -> Error
        }
    ) {
        Text(
            text = status.name,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
private fun AddressInfo(
    label: String,
    address: String,
    icon: ImageVector
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.width(4.dp))
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = address,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
} 