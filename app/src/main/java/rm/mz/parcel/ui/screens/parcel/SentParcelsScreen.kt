package rm.mz.parcel.ui.screens.parcel

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import rm.mz.parcel.ui.components.CommonScaffold
import rm.mz.parcel.ui.viewmodel.ParcelViewModel

@Composable
fun SentParcelsScreen(
    onNavigateBack: () -> Unit,
    onParcelClick: (String) -> Unit,
    onProfileClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onHelpClick: () -> Unit,
    onLogout: () -> Unit,
    viewModel: ParcelViewModel = hiltViewModel()
) {
    val sentParcels by viewModel.sentParcels.collectAsState(initial = emptyList())

    CommonScaffold(
        title = "Sent Parcels",
        viewModel = viewModel,
        onSentParcelsClick = { /* Already on sent parcels */ },
        onReceivedParcelsClick = onNavigateBack,
        onProfileClick = onProfileClick,
        onSettingsClick = onSettingsClick,
        onHelpClick = onHelpClick,
        onLogout = onLogout
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            items(sentParcels) { parcel ->
                ParcelItem(
                    parcel = parcel,
                    onClick = { onParcelClick(parcel.id) }
                )
            }
        }
    }
} 