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
import nz.terawhiti.wheretogo.location.Geo
import nz.terawhiti.wheretogo.location.StoredFix
import nz.terawhiti.wheretogo.rural.RuralPathways
import nz.terawhiti.wheretogo.ui.components.PlaceholderCard
import nz.terawhiti.wheretogo.ui.components.ScreenTitle
import nz.terawhiti.wheretogo.ui.components.ServiceLine

@Composable
fun RuralScreen(
    padding: PaddingValues,
    fix: StoredFix?,
    onBack: () -> Unit,
    onOpenLocate: () -> Unit,
    onOpenService: (Service) -> Unit,
) {
    val distance = fix?.let {
        val metres = Geo.distanceMetres(it.latitude, it.longitude, Directory.LAT, Directory.LON)
        val toward = Geo.compass(Geo.bearingDegrees(it.latitude, it.longitude, Directory.LAT, Directory.LON))
        "From the location saved on this phone, ${Directory.CLINIC} is about ${Geo.formatDistance(metres)} to the $toward."
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
        ScreenTitle(RuralPathways.TITLE)
        PlaceholderCard("Call 111 if someone is in danger", RuralPathways.INTRO)
        Text(RuralPathways.OFFLINE)
        if (distance != null) {
            Text(distance, fontWeight = FontWeight.Medium)
        } else {
            Text("No location is saved, so distance to the clinic is not shown.")
        }
        TextButton(onClick = onOpenLocate) { Text("Open Where I am") }
        Text("Starlink is only a way to restore internet. It is not a health service. The connection check is on Where I am.")
        for (service in RuralPathways.phoneFirst()) {
            ServiceLine(service) { onOpenService(service) }
        }
        for (gap in RuralPathways.gaps) {
            PlaceholderCard(gap.title, gap.body)
        }
    }
}
