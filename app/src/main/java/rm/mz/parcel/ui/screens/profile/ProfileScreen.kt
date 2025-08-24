package rm.mz.parcel.ui.screens.profile

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import rm.mz.parcel.ui.components.CommonScaffold
import rm.mz.parcel.ui.viewmodel.ParcelViewModel
import rm.mz.parcel.ui.viewmodel.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onNavigateBack: () -> Unit,
    onSentParcelsClick: () -> Unit,
    onReceivedParcelsClick: () -> Unit,
    onProfileClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onHelpClick: () -> Unit,
    onLogout: () -> Unit,
    profileViewModel: ProfileViewModel = hiltViewModel(),
    parcelViewModel: ParcelViewModel = hiltViewModel()
) {
    val profile by profileViewModel.profile.collectAsState(initial = null)
    val isLoading by profileViewModel.isLoading.collectAsState()
    val error by profileViewModel.error.collectAsState()
    var isEditing by remember { mutableStateOf(false) }
    
    var displayName by remember(profile) { 
        mutableStateOf(profile?.displayName ?: "") 
    }
    var gender by remember(profile) { 
        mutableStateOf(profile?.gender ?: "") 
    }

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { profileViewModel.uploadProfilePicture(it) }
    }

    error?.let {
        val context = LocalContext.current
        LaunchedEffect(it) {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            profileViewModel.clearError()
        }
    }

    CommonScaffold(
        title = "Profile",
        viewModel = parcelViewModel,
        onSentParcelsClick = onSentParcelsClick,
        onReceivedParcelsClick = onReceivedParcelsClick,
        onProfileClick = onProfileClick,
        onSettingsClick = onSettingsClick,
        onHelpClick = onHelpClick,
        onLogout = onLogout
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Profile Picture
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                ) {
                    if (profile?.photoUrl != null) {
                        AsyncImage(
                            model = profile?.photoUrl,
                            contentDescription = "Profile Picture",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Default Profile Picture",
                            modifier = Modifier.fillMaxSize(),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    
                    if (isEditing) {
                        IconButton(
                            onClick = { imagePicker.launch("image/*") },
                            modifier = Modifier.align(Alignment.BottomEnd)
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = "Change Picture")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Email (non-editable)
                OutlinedTextField(
                    value = profile?.email ?: "",
                    onValueChange = { },
                    label = { Text("Email") },
                    enabled = false,
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Display Name
                OutlinedTextField(
                    value = displayName,
                    onValueChange = { if (isEditing) displayName = it },
                    label = { Text("Display Name") },
                    enabled = isEditing,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Gender
                OutlinedTextField(
                    value = gender,
                    onValueChange = { if (isEditing) gender = it },
                    label = { Text("Gender") },
                    enabled = isEditing,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        if (isEditing) {
                            profileViewModel.updateProfile(displayName, gender)
                        }
                        isEditing = !isEditing
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (isEditing) "Save" else "Edit Profile")
                }
            }

            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
} 