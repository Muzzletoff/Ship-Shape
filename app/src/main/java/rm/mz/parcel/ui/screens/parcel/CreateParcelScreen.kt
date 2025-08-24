package rm.mz.parcel.ui.screens.parcel

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.firebase.Timestamp
import rm.mz.parcel.data.model.Parcel
import rm.mz.parcel.data.model.ParcelStatus
import rm.mz.parcel.ui.components.UserSearchField
import rm.mz.parcel.ui.viewmodel.CreateParcelState
import rm.mz.parcel.ui.viewmodel.ParcelViewModel
import rm.mz.parcel.util.EmailValidator
import rm.mz.parcel.util.TrackingNumberGenerator
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateParcelScreen(
    onNavigateBack: () -> Unit,
    onParcelCreated: () -> Unit,
    viewModel: ParcelViewModel = hiltViewModel()
) {
    var trackingNumber by remember { 
        mutableStateOf(TrackingNumberGenerator.generate()) 
    }
    var sourceAddress by remember { mutableStateOf("") }
    var destinationAddress by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var estimatedDays by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var receiverEmail by remember { mutableStateOf("") }
    var trackingNumberError by remember { mutableStateOf<String?>(null) }
    var sourceAddressError by remember { mutableStateOf<String?>(null) }
    var destinationAddressError by remember { mutableStateOf<String?>(null) }
    var descriptionError by remember { mutableStateOf<String?>(null) }
    var estimatedDaysError by remember { mutableStateOf<String?>(null) }
    var receiverEmailError by remember { mutableStateOf<String?>(null) }
    val createParcelState by viewModel.createParcelState.collectAsState()
    val searchResults by viewModel.userSearchResults.collectAsState()
    val isSearching by viewModel.isSearching.collectAsState()
    var showResultDialog by remember { mutableStateOf(false) }
    var resultMessage by remember { mutableStateOf<String?>(null) }
    var isSuccess by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        // Generate tracking number when screen is created
        trackingNumber = TrackingNumberGenerator.generate()
    }

    // Validate email on change
    LaunchedEffect(receiverEmail) {
        receiverEmailError = EmailValidator.getErrorMessage(receiverEmail)
    }

    // Validate fields on change
    LaunchedEffect(trackingNumber, sourceAddress, destinationAddress, description, estimatedDays, receiverEmail) {
        trackingNumberError = ValidationMessages.getFieldError("Tracking number", trackingNumber)
        sourceAddressError = ValidationMessages.getFieldError("Source address", sourceAddress)
        destinationAddressError = ValidationMessages.getFieldError("Destination address", destinationAddress)
        descriptionError = ValidationMessages.getFieldError("Description", description)
        estimatedDaysError = ValidationMessages.getEstimatedDaysError(estimatedDays)
        receiverEmailError = EmailValidator.getErrorMessage(receiverEmail)
    }

    // Handle parcel creation result
    LaunchedEffect(createParcelState) {
        when (createParcelState) {
            is CreateParcelState.Success -> {
                isSuccess = true
                resultMessage = "Parcel created successfully!"
                showResultDialog = true
            }
            is CreateParcelState.Error -> {
                isSuccess = false
                resultMessage = (createParcelState as CreateParcelState.Error).message
                showResultDialog = true
            }
            else -> {}
        }
    }

    // Result Dialog
    if (showResultDialog) {
        AlertDialog(
            onDismissRequest = { /* Do nothing, force user to choose */ },
            title = { Text(if (isSuccess) "Success" else "Error") },
            text = { Text(resultMessage ?: "Unknown result") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showResultDialog = false
                        if (isSuccess) {
                            onParcelCreated()
                        }
                    }
                ) {
                    Text(if (isSuccess) "OK" else "Try Again")
                }
            },
            dismissButton = if (!isSuccess) {
                {
                    TextButton(onClick = { showResultDialog = false }) {
                        Text("Cancel")
                    }
                }
            } else null
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create Parcel") },
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
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = trackingNumber,
                onValueChange = { /* Read only */ },
                label = { Text("Tracking Number") },
                modifier = Modifier.fillMaxWidth(),
                enabled = false,
                colors = OutlinedTextFieldDefaults.colors(
                    disabledTextColor = MaterialTheme.colorScheme.onSurface,
                    disabledBorderColor = MaterialTheme.colorScheme.outline,
                    disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )

            OutlinedTextField(
                value = sourceAddress,
                onValueChange = { sourceAddress = it },
                label = { Text("Source Address") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                isError = sourceAddressError != null,
                supportingText = {
                    if (sourceAddressError != null) {
                        Text(
                            text = sourceAddressError!!,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            )

            OutlinedTextField(
                value = destinationAddress,
                onValueChange = { destinationAddress = it },
                label = { Text("Destination Address") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                isError = destinationAddressError != null,
                supportingText = {
                    if (destinationAddressError != null) {
                        Text(
                            text = destinationAddressError!!,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                isError = descriptionError != null,
                supportingText = {
                    if (descriptionError != null) {
                        Text(
                            text = descriptionError!!,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            )

            OutlinedTextField(
                value = estimatedDays,
                onValueChange = { 
                    if (it.isEmpty() || it.toIntOrNull() != null) {
                        estimatedDays = it 
                    }
                },
                label = { Text("Estimated Days for Delivery") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
                isError = estimatedDaysError != null,
                supportingText = {
                    if (estimatedDaysError != null) {
                        Text(
                            text = estimatedDaysError!!,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            )

            UserSearchField(
                value = receiverEmail,
                onValueChange = { email ->
                    receiverEmail = email
                    if (email.length >= 3) {
                        viewModel.searchUsers(email)
                    }
                },
                searchResults = searchResults,
                onResultSelected = { result ->
                    receiverEmail = result.email
                },
                isLoading = isSearching,
                error = receiverEmailError,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    if (validateInputs(
                        trackingNumber = trackingNumber,
                        sourceAddress = sourceAddress,
                        destinationAddress = destinationAddress,
                        description = description,
                        estimatedDays = estimatedDays,
                        receiverEmail = receiverEmail
                    )) {
                        viewModel.createParcel(
                            trackingNumber = trackingNumber,
                            sourceAddress = sourceAddress,
                            destinationAddress = destinationAddress,
                            description = description,
                            receiverEmail = receiverEmail,
                            estimatedDays = estimatedDays.toIntOrNull() ?: 1
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading && receiverEmailError == null
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Create Parcel")
                }
            }

            errorMessage?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            if (createParcelState is CreateParcelState.Error) {
                Text(
                    text = (createParcelState as CreateParcelState.Error).message,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}

private fun validateInputs(
    trackingNumber: String,
    sourceAddress: String,
    destinationAddress: String,
    description: String,
    estimatedDays: String,
    receiverEmail: String
): Boolean {
    return when {
        trackingNumber.isBlank() -> false
        sourceAddress.isBlank() -> false
        destinationAddress.isBlank() -> false
        description.isBlank() -> false
        estimatedDays.isBlank() || estimatedDays.toIntOrNull() == null -> false
        !EmailValidator.isValid(receiverEmail) -> false
        else -> true
    }
} 