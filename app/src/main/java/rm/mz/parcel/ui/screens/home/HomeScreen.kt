package rm.mz.parcel.ui.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch
import rm.mz.parcel.R
import rm.mz.parcel.ui.components.DrawerContent
import rm.mz.parcel.ui.viewmodel.ParcelViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onSentParcelsClick: () -> Unit,
    onReceivedParcelsClick: () -> Unit,
    onCreateParcelClick: () -> Unit,
    onProfileClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onHelpClick: () -> Unit,
    onLogout: () -> Unit,
    viewModel: ParcelViewModel = hiltViewModel()
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
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
                    scope.launch {
                        drawerState.close()
                        onSentParcelsClick()
                    }
                },
                onReceivedParcelsClick = {
                    scope.launch {
                        drawerState.close()
                        onReceivedParcelsClick()
                    }
                },
                profile = profile
            )
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Parcel Service") },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
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
                    .padding(padding),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Welcome Banner
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .height(200.dp)
                    ) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            Image(
                                painter = painterResource(R.drawable.delivery_banner),
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                            Surface(
                                modifier = Modifier.fillMaxSize(),
                                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .padding(16.dp)
                                        .fillMaxSize(),
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        "Welcome to Parcel Service",
                                        style = MaterialTheme.typography.headlineMedium
                                    )
                                    Text(
                                        "Fast, Secure, and Reliable Delivery",
                                        style = MaterialTheme.typography.bodyLarge,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }
                }

                // Quick Actions
                item {
                    Text(
                        "Quick Actions",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(16.dp)
                    )
                }

                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        QuickActionButton(
                            icon = Icons.Default.Send,
                            text = "Send Parcel",
                            onClick = onCreateParcelClick
                        )
                        QuickActionButton(
                            icon = Icons.Default.Inbox,
                            text = "My Parcels",
                            onClick = onSentParcelsClick
                        )
                    }
                }

                // Services Section
                item {
                    Text(
                        "Our Services",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(16.dp)
                    )
                }

                items(services) { service ->
                    ServiceCard(service = service)
                }
            }
        }
    }
}

@Composable
private fun QuickActionButton(
    icon: ImageVector,
    text: String,
    onClick: () -> Unit
) {
    ElevatedCard(
        onClick = onClick,
        modifier = Modifier
            .width(150.dp)
            .padding(8.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(text, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
private fun ServiceCard(service: Service) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = service.icon,
                contentDescription = null,
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = service.title,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = service.description,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

private data class Service(
    val icon: ImageVector,
    val title: String,
    val description: String
)

private val services = listOf(
    Service(
        Icons.Default.LocalShipping,
        "Express Delivery",
        "Same-day delivery for urgent packages"
    ),
    Service(
        Icons.Default.Security,
        "Secure Transport",
        "End-to-end package tracking and security"
    ),
    Service(
        Icons.Default.Public,
        "International Shipping",
        "Worldwide delivery services"
    ),
    Service(
        Icons.Default.Inventory,
        "Warehousing",
        "Safe storage for your packages"
    )
) 