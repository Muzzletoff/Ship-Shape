package rm.mz.parcel.ui.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch
import rm.mz.parcel.ui.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val scope = rememberCoroutineScope()
    
    val notificationsEnabled by viewModel.notificationsEnabled.collectAsState(initial = true)
    val darkThemeEnabled by viewModel.darkThemeEnabled.collectAsState(initial = false)
    val locationTrackingEnabled by viewModel.locationTrackingEnabled.collectAsState(initial = true)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Notifications Setting
            SettingItem(
                title = "Notifications",
                description = "Enable push notifications",
                icon = Icons.Default.Notifications,
                checked = notificationsEnabled,
                onCheckedChange = { enabled ->
                    scope.launch {
                        viewModel.setNotificationsEnabled(enabled)
                    }
                }
            )

            // Dark Theme Setting
            SettingItem(
                title = "Dark Theme",
                description = "Enable dark theme",
                icon = Icons.Default.DarkMode,
                checked = darkThemeEnabled,
                onCheckedChange = { enabled ->
                    scope.launch {
                        viewModel.setDarkThemeEnabled(enabled)
                    }
                }
            )

            // Location Tracking Setting
            SettingItem(
                title = "Location Tracking",
                description = "Enable location tracking for parcels",
                icon = Icons.Default.LocationOn,
                checked = locationTrackingEnabled,
                onCheckedChange = { enabled ->
                    scope.launch {
                        viewModel.setLocationTrackingEnabled(enabled)
                    }
                }
            )

            // Version Info
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                ListItem(
                    headlineContent = { Text("App Version") },
                    supportingContent = { Text("1.0.0") },
                    leadingContent = {
                        Icon(Icons.Default.Info, contentDescription = null)
                    }
                )
            }
        }
    }
}

@Composable
private fun SettingItem(
    title: String,
    description: String,
    icon: ImageVector,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        ListItem(
            headlineContent = { Text(title) },
            supportingContent = { Text(description) },
            leadingContent = {
                Icon(icon, contentDescription = null)
            },
            trailingContent = {
                Switch(
                    checked = checked,
                    onCheckedChange = onCheckedChange
                )
            }
        )
    }
} 