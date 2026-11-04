package nz.terawhiti.wheretogo.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import nz.terawhiti.wheretogo.data.Directory
import nz.terawhiti.wheretogo.data.Service
import nz.terawhiti.wheretogo.ui.dial
import nz.terawhiti.wheretogo.ui.text
import nz.terawhiti.wheretogo.ui.theme.CardWhite
import nz.terawhiti.wheretogo.ui.theme.Emergency
import nz.terawhiti.wheretogo.ui.theme.EmergencySoft
import nz.terawhiti.wheretogo.ui.theme.Teal

@Composable
fun EmergencyStrip() {
    val context = LocalContext.current
    Column(
        Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            "If someone is in danger",
            color = Emergency,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.semantics { heading() },
        )
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            EmergencyButton("Call 111", Modifier.weight(1f)) {
                dial(context, Directory.emergency.phones.first())
            }
            EmergencyButton("Clinic", Modifier.weight(1f)) {
                dial(context, Directory.clinic.phones.first())
            }
            EmergencyButton("After hours", Modifier.weight(1f)) {
                dial(context, Directory.afterHours.phones.first())
            }
        }
    }
}

@Composable
private fun EmergencyButton(label: String, modifier: Modifier, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = modifier.heightIn(min = 52.dp),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Emergency, contentColor = Color.White),
    ) { Text(label) }
}

@Composable
fun ShortcutCard(title: String, body: String, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text(body)
        }
    }
}

@Composable
fun ServiceCard(service: Service, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(service.category, color = Teal, style = MaterialTheme.typography.labelLarge)
            Text(service.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            service.hours?.let { Text(it) }
            Text(service.summary, maxLines = 3)
        }
    }
}

@Composable
fun ServiceLine(service: Service, onOpen: () -> Unit) {
    val context = LocalContext.current
    Card(
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(service.category, color = Teal, style = MaterialTheme.typography.labelLarge)
            Text(service.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            service.hours?.let { Text(it, fontWeight = FontWeight.Medium) }
            Text(service.summary)
            for (phone in service.phones) {
                Button(onClick = { dial(context, phone) }, modifier = Modifier.fillMaxWidth()) {
                    Text("${phone.label} ${phone.number}")
                }
            }
            for (phone in service.texts) {
                OutlinedButton(onClick = { text(context, phone) }, modifier = Modifier.fillMaxWidth()) {
                    Text("Text ${phone.number}")
                }
            }
            TextButton(onClick = onOpen) { Text("Details") }
        }
    }
}

@Composable
fun PlaceholderCard(title: String, body: String) {
    Card(
        colors = CardDefaults.cardColors(containerColor = EmergencySoft),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(title, fontWeight = FontWeight.Bold, color = Emergency)
            Text(body)
        }
    }
}

@Composable
fun ScreenTitle(text: String) {
    Text(
        text,
        style = MaterialTheme.typography.headlineSmall,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.semantics { heading() },
    )
}
