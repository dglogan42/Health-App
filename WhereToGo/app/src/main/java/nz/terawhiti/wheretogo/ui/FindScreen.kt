package nz.terawhiti.wheretogo.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import nz.terawhiti.wheretogo.data.Directory
import nz.terawhiti.wheretogo.data.Service
import nz.terawhiti.wheretogo.ui.components.ServiceCard

@Composable
fun FindScreen(
    padding: PaddingValues,
    query: String,
    category: String?,
    onQuery: (String) -> Unit,
    onCategory: (String?) -> Unit,
    onOpen: (Service) -> Unit,
) {
    val results = Directory.search(query, category)
    Column(Modifier.fillMaxSize().padding(padding)) {
        OutlinedTextField(
            value = query,
            onValueChange = onQuery,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
            label = { Text("Search names, places, or numbers") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        )
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            item {
                FilterChip(selected = category == null, onClick = { onCategory(null) }, label = { Text("All") })
            }
            items(Directory.categories) { name ->
                FilterChip(
                    selected = category == name,
                    onClick = { onCategory(if (category == name) null else name) },
                    label = { Text(name) },
                )
            }
        }
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            if (results.isEmpty()) {
                item {
                    Text(
                        "Nothing in this list matches. Try another word, or call the clinic on a weekday.",
                        style = MaterialTheme.typography.bodyLarge,
                    )
                }
            }
            items(results, key = { it.name }) { service ->
                ServiceCard(service) { onOpen(service) }
            }
        }
    }
}
