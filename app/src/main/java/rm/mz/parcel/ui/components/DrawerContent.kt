package rm.mz.parcel.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import rm.mz.parcel.data.model.Profile

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DrawerContent(
    currentUserEmail: String?,
    onProfileClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onHelpClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onCloseDrawer: () -> Unit,
    onSentParcelsClick: () -> Unit,
    onReceivedParcelsClick: () -> Unit,
    profile: Profile?
) {
    var showLogoutDialog by remember { mutableStateOf(false) }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Confirm Logout") },
            text = { Text("Are you sure you want to logout?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showLogoutDialog = false
                        onCloseDrawer()
                        onLogoutClick()
                    }
                ) {
                    Text("Logout")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .width(300.dp)
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp)
    ) {
        // Profile Section
        ProfileSection(
            email = currentUserEmail ?: "",
            photoUrl = profile?.photoUrl,
            onProfileClick = {
                onCloseDrawer()
                onProfileClick()
            }
        )

        Spacer(modifier = Modifier.height(16.dp))
        Divider()
        Spacer(modifier = Modifier.height(16.dp))

        // Menu Items
        NavigationDrawerItem(
            icon = { Icon(Icons.Default.Send, contentDescription = null) },
            label = { Text("Sent Parcels") },
            selected = false,
            onClick = {
                onCloseDrawer()
                onSentParcelsClick()
            }
        )

        NavigationDrawerItem(
            icon = { Icon(Icons.Default.Inbox, contentDescription = null) },
            label = { Text("Received Parcels") },
            selected = false,
            onClick = {
                onCloseDrawer()
                onReceivedParcelsClick()
            }
        )

        NavigationDrawerItem(
            icon = { Icon(Icons.Default.Settings, contentDescription = null) },
            label = { Text("Settings") },
            selected = false,
            onClick = {
                onCloseDrawer()
                onSettingsClick()
            }
        )

        NavigationDrawerItem(
            icon = { Icon(Icons.Default.Help, contentDescription = null) },
            label = { Text("Help") },
            selected = false,
            onClick = {
                onCloseDrawer()
                onHelpClick()
            }
        )

        Spacer(modifier = Modifier.weight(1f))
        Divider()
        Spacer(modifier = Modifier.height(16.dp))

        // Logout Button
        NavigationDrawerItem(
            icon = { Icon(Icons.Default.ExitToApp, contentDescription = null) },
            label = { Text("Logout") },
            selected = false,
            colors = NavigationDrawerItemDefaults.colors(
                unselectedIconColor = MaterialTheme.colorScheme.error,
                unselectedTextColor = MaterialTheme.colorScheme.error
            ),
            onClick = { showLogoutDialog = true }
        )
    }
}

@Composable
private fun ProfileSection(
    email: String,
    photoUrl: String?,
    onProfileClick: () -> Unit
) {
    NavigationDrawerItem(
        icon = {
            if (photoUrl != null) {
                AsyncImage(
                    model = photoUrl,
                    contentDescription = "Profile Picture",
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            } else {
                Icon(Icons.Default.AccountCircle, contentDescription = null)
            }
        },
        label = { Text(email) },
        selected = false,
        onClick = onProfileClick
    )
} 