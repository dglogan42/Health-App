package nz.terawhiti.wheretogo.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import nz.terawhiti.wheretogo.data.Directory
import nz.terawhiti.wheretogo.data.Handout
import nz.terawhiti.wheretogo.data.Service
import nz.terawhiti.wheretogo.ui.components.ServiceCard
import nz.terawhiti.wheretogo.ui.components.ShortcutCard
import nz.terawhiti.wheretogo.ui.theme.CardWhite
import nz.terawhiti.wheretogo.ui.theme.Teal

enum class SupportRole { PATIENT, WORKER }

@Composable
fun NowScreen(
    padding: PaddingValues,
    role: SupportRole,
    largeText: Boolean,
    waitingNotes: Int,
    onRole: (SupportRole) -> Unit,
    onLargeText: (Boolean) -> Unit,
    onOpenGuide: () -> Unit,
    onOpenLocate: () -> Unit,
    onOpenAfterHours: () -> Unit,
    onOpenTelehealth: () -> Unit,
    onOpenRural: () -> Unit,
    onOpenRelay: () -> Unit,
    onOpenBot: () -> Unit,
    onOpenService: (Service) -> Unit,
    onAbout: () -> Unit,
) {
    val context = LocalContext.current
    val firstport = Directory.named("Firstport")
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize().padding(padding),
    ) {
        item {
            Text(
                "Te Rawhiti",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.semantics { heading() },
            )
            Text("Where to go", style = MaterialTheme.typography.titleLarge, color = Teal)
            Text(
                "A phone copy of the clinic’s where-to-go list, for people using the service and for the people supporting them. It works without internet.",
                modifier = Modifier.padding(top = 8.dp),
            )
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = role == SupportRole.PATIENT,
                    onClick = { onRole(SupportRole.PATIENT) },
                    label = { Text("I need support") },
                )
                FilterChip(
                    selected = role == SupportRole.WORKER,
                    onClick = { onRole(SupportRole.WORKER) },
                    label = { Text("I am supporting") },
                )
            }
            TextButton(onClick = { onLargeText(!largeText) }) {
                Text(if (largeText) "Use smaller text" else "Use larger text")
            }
        }
        if (waitingNotes > 0) {
            item {
                Text(
                    if (waitingNotes == 1) {
                        "One note is waiting on this phone. Open Locate to share it yourself. Nothing sends on its own."
                    } else {
                        "$waitingNotes notes are waiting on this phone. Open Locate to share them yourself. Nothing sends on its own."
                    },
                )
            }
        }
        item {
            ShortcutCard(
                "Where I am",
                "Save a GPS fix on this phone for a handover. It does not call 111, and it works with no internet.",
                onOpenLocate,
            )
        }
        item {
            ShortcutCard(
                "After hours",
                "The clinic number on weekdays, and ${Directory.afterHours.phones.first().number} outside those hours. If someone is in danger, call 111.",
                onOpenAfterHours,
            )
        }
        item {
            ShortcutCard(
                "Talk by phone",
                "Phone lines already in this list. This app does not run the appointment.",
                onOpenTelehealth,
            )
        }
        item {
            ShortcutCard(
                "Rural and low signal",
                "Phone-first lines from this list, distance to the clinic, and what is not in the saved files.",
                onOpenRural,
            )
        }
        item {
            ShortcutCard(
                "Relay a message",
                "A prototype for passing a note by phone or radio. It does not contact Police. Danger is still 111.",
                onOpenRelay,
            )
        }
        item {
            ShortcutCard(
                "Bot telehandling",
                "Fixed steps on this phone. Off until you turn it on. Not a counsellor and not a doctor.",
                onOpenBot,
            )
        }
        item {
            Card(colors = CardDefaults.cardColors(containerColor = CardWhite), modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(Handout.TITLE, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Text(Handout.SUBTITLE)
                    Text(Handout.INTRO)
                    Text(if (role == SupportRole.WORKER) Handout.WORKER_HINT else Handout.PERSON_HINT)
                    Button(onClick = onOpenGuide) { Text("Open the handout") }
                }
            }
        }
        if (role == SupportRole.WORKER) {
            item {
                Card(colors = CardDefaults.cardColors(containerColor = CardWhite), modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("On shift", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        Text("${Directory.CLINIC}. ${Directory.ADDRESS}.")
                        Text(
                            "Weekdays 8am–5pm: ${Directory.clinic.phones.first().number}. Other times, for urgent mental health help: ${Directory.afterHours.phones.first().number}.",
                        )
                        Text("People may be referred by a GP, come from crisis services, or walk in.")
                        Text(
                            "Disability support that is not an emergency: ${firstport.phones.first().label} ${firstport.phones.first().number}, or text ${firstport.texts.first().number}.",
                        )
                        Button(onClick = { openHandout(context) }) { Text("Open the original PDF") }
                    }
                }
            }
        }
        item {
            Text(
                "Talk to someone",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.semantics { heading() },
            )
        }
        items(Directory.services.filter { it.urgent }, key = { it.name }) { service ->
            ServiceCard(service) { onOpenService(service) }
        }
        item {
            TextButton(onClick = onAbout) { Text("About this app and the saved pages") }
        }
    }
}
