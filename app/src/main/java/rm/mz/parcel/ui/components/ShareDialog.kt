package rm.mz.parcel.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import rm.mz.parcel.data.model.ShareDuration
import rm.mz.parcel.data.model.ShareOptions
import rm.mz.parcel.data.model.SharePermission


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShareDialog(
    onDismiss: () -> Unit,
    onShare: (List<String>, ShareOptions) -> Unit,
    isLoading: Boolean = false
) {
    var selectedEmails by remember { mutableStateOf(listOf<String>()) }
    var selectedDuration by remember { mutableStateOf(ShareDuration.ONE_DAY) }
    var selectedPermissions by remember { mutableStateOf(setOf(SharePermission.VIEW)) }
    var notifyOnUpdates by remember { mutableStateOf(true) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Share Parcel") },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Email Selection
                UserSearchField(
                    value = "",
                    onValueChange = { /* Handle search */ },
                    searchResults = emptyList(),
                    onResultSelected = { result ->
                        selectedEmails = selectedEmails + result.email
                    },
                    isLoading = false,
                    error = null,
                    modifier = Modifier.fillMaxWidth()
                )

                // Selected Users
                selectedEmails.forEach { email ->
                    AssistChip(
                        onClick = { selectedEmails = selectedEmails - email },
                        label = { Text(email) },
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Remove"
                            )
                        }
                    )
                }

                // Duration Selection
                Text("Share Duration", style = MaterialTheme.typography.titleSmall)
                ShareDuration.values().forEach { duration ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedDuration == duration,
                            onClick = { selectedDuration = duration }
                        )
                        Text(duration.name.replace("_", " "))
                    }
                }

                // Permissions
                Text("Permissions", style = MaterialTheme.typography.titleSmall)
                SharePermission.values().forEach { permission ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = permission in selectedPermissions,
                            onCheckedChange = { checked ->
                                selectedPermissions = if (checked) {
                                    selectedPermissions + permission
                                } else {
                                    selectedPermissions - permission
                                }
                            }
                        )
                        Text(permission.name.replace("_", " "))
                    }
                }

                // Notifications
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Notify on Updates")
                    Switch(
                        checked = notifyOnUpdates,
                        onCheckedChange = { notifyOnUpdates = it }
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onShare(
                        selectedEmails,
                        ShareOptions(
                            duration = selectedDuration,
                            permissions = selectedPermissions,
                            notifyOnUpdates = notifyOnUpdates
                        )
                    )
                },
                enabled = selectedEmails.isNotEmpty() && !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Share")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
} 