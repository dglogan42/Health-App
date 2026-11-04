package nz.terawhiti.wheretogo.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import nz.terawhiti.wheretogo.data.Directory
import nz.terawhiti.wheretogo.data.Service
import nz.terawhiti.wheretogo.telehealth.TelehealthCopy
import nz.terawhiti.wheretogo.ui.components.PlaceholderCard
import nz.terawhiti.wheretogo.ui.components.ScreenTitle
import nz.terawhiti.wheretogo.ui.components.ServiceLine

@Composable
fun TelehealthScreen(
    padding: PaddingValues,
    onBack: () -> Unit,
    onOpenAfterHours: () -> Unit,
    onOpenService: (Service) -> Unit,
) {
    val talk = Directory.services.filter {
        it.category == Directory.TALK && (it.phones.isNotEmpty() || it.texts.isNotEmpty())
    }
    val clinicLines = listOf(Directory.clinic, Directory.afterHours)
    val listed = (talk + clinicLines).map { it.name }.toSet()
    val others = Directory.phonePathways().filter { it.name !in listed }
    Column(
        Modifier
            .fillMaxSize()
            .padding(padding)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        TextButton(onClick = onBack) { Text("Back") }
        ScreenTitle(TelehealthCopy.TITLE)
        PlaceholderCard("Not a consultation", TelehealthCopy.BOUNDARY)
        Text(TelehealthCopy.OFFLINE)
        TextButton(onClick = onOpenAfterHours) { Text("After hours") }
        Text("Talk now", fontWeight = FontWeight.Bold)
        for (service in talk) {
            ServiceLine(service) { onOpenService(service) }
        }
        Text("Clinic", fontWeight = FontWeight.Bold)
        for (service in clinicLines) {
            ServiceLine(service) { onOpenService(service) }
        }
        PlaceholderCard("Not in the clinic materials — video", TelehealthCopy.VIDEO_GAP)
        if (others.isNotEmpty()) {
            Text("Other numbers already in the list", fontWeight = FontWeight.Bold)
            for (service in others) {
                ServiceLine(service) { onOpenService(service) }
            }
        }
    }
}
