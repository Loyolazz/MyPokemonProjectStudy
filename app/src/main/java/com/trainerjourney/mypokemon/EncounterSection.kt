package com.trainerjourney.mypokemon

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EncounterSection(
    encounterState: EncounterState,
    onRegionChange: (String) -> Unit,
    onGenerate: () -> Unit,
    onCapture: (String) -> Unit
) {
    val regions = listOf("Kanto", "Johto", "Hoenn", "Sinnoh", "Unova", "Kalos", "Galar", "Paldea")
    var expanded by remember { mutableStateOf(false) }
    val selectedRegion = encounterState.region

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "🌍 Encontros aleatórios",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = "Gera um Pokémon aleatório por região, registra o encontro na Pokédex e permite tentar captura imediata.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Box {
            androidx.compose.material3.OutlinedTextField(
                value = selectedRegion,
                onValueChange = {},
                readOnly = true,
                label = { Text("Região") },
                trailingIcon = { Text(if (expanded) "▲" else "▼") },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded },
                colors = OutlinedTextFieldDefaults.colors()
            )
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                regions.forEach { region ->
                    DropdownMenuItem(
                        text = { Text(region) },
                        onClick = {
                            onRegionChange(region)
                            expanded = false
                        }
                    )
                }
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(onClick = onGenerate, enabled = !encounterState.isLoading) {
                if (encounterState.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.height(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text("Gerar encontro")
            }
            encounterState.current?.let {
                TextButton(onClick = { onCapture(it.name) }) { Text("Capturar ${it.name}") }
            }
        }

        encounterState.error?.let {
            Text(text = it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        }

        encounterState.current?.let { encounter ->
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.12f)),
                border = CardDefaults.outlinedCardBorder(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Encontro #${encounter.entryNumber} - ${encounter.name}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text("Região: ${encounter.region}", style = MaterialTheme.typography.bodySmall)
                    Text("Habitat: ${encounter.habitat ?: "Desconhecido"}", style = MaterialTheme.typography.bodySmall)
                    Text("Taxa base de captura: ${encounter.captureRate}", style = MaterialTheme.typography.bodySmall)
                    if (encounter.legendary) {
                        AssistChip(onClick = {}, label = { Text("Lendário") })
                    }
                }
            }
        }
    }
}
