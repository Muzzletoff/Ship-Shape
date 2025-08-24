package rm.mz.parcel.ui.screens.help

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpScreen(
    onNavigateBack: () -> Unit
) {
    val faqItems = remember {
        listOf(
            FaqItem(
                question = "How do I track my parcel?",
                answer = "You can track your parcel by clicking on any parcel in your list and then selecting 'Track Parcel'. This will show you the current location and status of your parcel."
            ),
            FaqItem(
                question = "How do I share parcel location?",
                answer = "Open the parcel details and click the share button. Enter the email address of the person you want to share with and select the duration."
            ),
            FaqItem(
                question = "What do the different statuses mean?",
                answer = "PENDING: Awaiting pickup\nPICKED_UP: Collected from sender\nIN_TRANSIT: On the way\nDELIVERED: Successfully delivered\nCANCELLED: Delivery cancelled"
            )
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Help & FAQ") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            items(faqItems) { faq ->
                FaqItem(faq)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun FaqItem(faq: FaqItem) {
    var expanded by remember { mutableStateOf(false) }

    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        onClick = { expanded = !expanded }
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = faq.question,
                    style = MaterialTheme.typography.titleMedium
                )
                Icon(
                    Icons.Default.ExpandMore,
                    contentDescription = if (expanded) "Collapse" else "Expand"
                )
            }
            if (expanded) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = faq.answer,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

data class FaqItem(
    val question: String,
    val answer: String
) 