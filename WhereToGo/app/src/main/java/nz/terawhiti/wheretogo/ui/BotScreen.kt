package nz.terawhiti.wheretogo.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import nz.terawhiti.wheretogo.bot.BotTelehandling
import nz.terawhiti.wheretogo.data.Directory
import nz.terawhiti.wheretogo.location.LocationSummary
import nz.terawhiti.wheretogo.location.StoredFix
import nz.terawhiti.wheretogo.ui.components.PlaceholderCard
import nz.terawhiti.wheretogo.ui.components.ScreenTitle
import java.time.ZoneId

@Composable
fun BotScreen(
    padding: PaddingValues,
    enabled: Boolean,
    fix: StoredFix?,
    onEnabled: (Boolean) -> Unit,
    onBack: () -> Unit,
    onFind: () -> Unit,
    onAfterHours: () -> Unit,
    onPrepare: (String, String) -> Unit,
) {
    val context = LocalContext.current
    var includeLocation by rememberSaveable { mutableStateOf(false) }
    var chosenId by rememberSaveable { mutableStateOf("") }
    val locationText = if (includeLocation) {
        fix?.let { LocationSummary.plainText(it, ZoneId.systemDefault()) }
    } else {
        null
    }
    Column(
        Modifier
            .fillMaxSize()
            .padding(padding)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        TextButton(onClick = onBack) { Text("Back") }
        ScreenTitle(BotTelehandling.TITLE)
        PlaceholderCard("Not medical advice", BotTelehandling.DISCLAIMER)
        Text(BotTelehandling.RED_FLAG, fontWeight = FontWeight.Bold)
        Text(BotTelehandling.ENABLE)
        if (!enabled) {
            Button(onClick = { onEnabled(true) }, modifier = Modifier.fillMaxWidth()) {
                Text("Turn on bot telehandling on this phone")
            }
        } else {
            TextButton(onClick = { onEnabled(false) }) { Text("Turn off bot telehandling") }
            Text("Chips only. There is no chat, and there is no dose or diagnosis.")
            for (chip in BotTelehandling.chips) {
                Button(
                    onClick = {
                        chosenId = chip.id
                        when (chip.id) {
                            "111" -> dial(context, Directory.emergency.phones.first())
                            "clinic" -> dial(context, Directory.clinic.phones.first())
                            "after_hours" -> onAfterHours()
                            "find" -> onFind()
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                ) { Text(chip.label) }
            }
            if (chosenId == "1737") {
                Button(
                    onClick = { dial(context, Directory.needToTalk.phones.first()) },
                    modifier = Modifier.fillMaxWidth(),
                ) { Text("Call 1737") }
                OutlinedButton(
                    onClick = { text(context, Directory.needToTalk.texts.first()) },
                    modifier = Modifier.fillMaxWidth(),
                ) { Text("Text 1737") }
            }
            FilterChip(
                selected = includeLocation,
                onClick = { includeLocation = !includeLocation },
                label = { Text("Include the saved location in a handoff note") },
            )
            if (includeLocation && fix == null) {
                Text("No location is saved. The note will not add one.")
            }
            OutlinedButton(
                onClick = {
                    val label = BotTelehandling.chips.firstOrNull { it.id == chosenId }?.label ?: "none"
                    onPrepare(BotTelehandling.TITLE, BotTelehandling.handoff(label, locationText))
                },
                modifier = Modifier.fillMaxWidth(),
            ) { Text("Prepare a handoff note") }
            Text("The note is the fixed safety line and the chip you chose. Handout answers are not included. Nothing is sent until you share it from Where I am.")
        }
    }
}
