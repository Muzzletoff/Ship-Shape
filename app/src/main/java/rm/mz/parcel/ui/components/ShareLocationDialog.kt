package rm.mz.parcel.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import rm.mz.parcel.ui.viewmodel.SharingState

@Composable
fun ShareLocationDialog(
    onDismiss: () -> Unit,
    onShare: (email: String, hours: Int) -> Unit,
    sharingState: SharingState
) {
    var email by remember { mutableStateOf("") }
    var hours by remember { mutableStateOf("24") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Share Location") },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Recipient Email") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = hours,
                    onValueChange = { if (it.isEmpty() || it.toIntOrNull() != null) hours = it },
                    label = { Text("Share Duration (hours)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                if (sharingState is SharingState.Error) {
                    Text(
                        text = sharingState.message,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onShare(email, hours.toIntOrNull() ?: 24) },
                enabled = email.isNotBlank() && hours.isNotBlank() && 
                         sharingState !is SharingState.Loading
            ) {
                if (sharingState is SharingState.Loading) {
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