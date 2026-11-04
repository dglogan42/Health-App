package nz.terawhiti.wheretogo.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import nz.terawhiti.wheretogo.data.Handout
import nz.terawhiti.wheretogo.ui.components.ScreenTitle
import nz.terawhiti.wheretogo.ui.theme.CardWhite
import nz.terawhiti.wheretogo.ui.theme.Emergency
import nz.terawhiti.wheretogo.ui.theme.EmergencySoft
import nz.terawhiti.wheretogo.ui.theme.Teal

@Composable
fun GuideScreen(
    padding: PaddingValues,
    worker: Boolean,
    answers: Map<String, String>,
    onAnswer: (String, String) -> Unit,
    onClear: () -> Unit,
) {
    val context = LocalContext.current
    var chosenKindness by remember { mutableStateOf(setOf<String>()) }
    Column(
        Modifier
            .fillMaxSize()
            .padding(padding)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        ScreenTitle(Handout.TITLE)
        Text(Handout.SUBTITLE, color = Teal)
        Text(Handout.INTRO)
        if (!worker) {
            Text(Handout.SOFT_LEAD_IN)
        }
        ConsiderationCard(
            title = if (worker) "Before you use this with someone" else "Before you write",
            points = if (worker) Handout.workerConsiderations else Handout.personConsiderations,
        )
        Button(onClick = { openHandout(context) }, modifier = Modifier.fillMaxWidth()) {
            Text("Open the original two-page PDF")
        }
        Text("The scan is the group handout saved with this project. Typing below is optional and stays on this phone.")
        Handout.questions.groupBy { it.section }.forEach { (section, questions) ->
            Text(
                section,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.semantics { heading() },
            )
            for (question in questions) {
                OutlinedTextField(
                    value = answers[question.id] ?: "",
                    onValueChange = { onAnswer(question.id, it) },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(question.prompt) },
                    minLines = 3,
                )
            }
        }
        OutlinedButton(onClick = onClear, modifier = Modifier.fillMaxWidth()) {
            Text("Clear answers on this phone")
        }
        HorizontalDivider()
        Text(Handout.SKILLS_OPENER)
        Text(
            Handout.SKILLS_LIMIT,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
        )
        for (skill in Handout.skills) {
            Card(colors = CardDefaults.cardColors(containerColor = CardWhite), modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(skill.title, fontWeight = FontWeight.Bold)
                    Text(skill.body)
                }
            }
        }
        Text(
            Handout.KINDNESS_TITLE,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.semantics { heading() },
        )
        Text(Handout.KINDNESS_BODY)
        for (item in Handout.kindnesses) {
            FilterChip(
                selected = item in chosenKindness,
                onClick = {
                    chosenKindness = if (item in chosenKindness) chosenKindness - item else chosenKindness + item
                },
                label = { Text(item) },
            )
        }
        Text("Choosing one stays on this screen. It does not place a call.")
        Spacer(Modifier.height(12.dp))
    }
}

@Composable
private fun ConsiderationCard(title: String, points: List<String>) {
    Card(colors = CardDefaults.cardColors(containerColor = EmergencySoft), modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Emergency)
            for (point in points) Text("• $point")
        }
    }
}
