package rm.mz.parcel.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import kotlinx.coroutines.launch
import rm.mz.parcel.data.model.Profile
import rm.mz.parcel.ui.viewmodel.ParcelViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommonScaffold(
    title: String,
    viewModel: ParcelViewModel,
    onSentParcelsClick: () -> Unit,
    onReceivedParcelsClick: () -> Unit,
    onProfileClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onHelpClick: () -> Unit,
    onLogout: () -> Unit,
    floatingActionButton: @Composable () -> Unit = {},
    content: @Composable (PaddingValues) -> Unit
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
                onLogoutClick = {
                    scope.launch {
                        drawerState.close()
                        onLogout()
                    }
                },
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
                    title = { Text(title) },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    }
                )
            },
            floatingActionButton = floatingActionButton
        ) { padding ->
            content(padding)
        }
    }
} 