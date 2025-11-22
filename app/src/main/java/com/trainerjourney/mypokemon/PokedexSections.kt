package com.trainerjourney.mypokemon

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

@Composable
fun PokedexEntryCard(entry: PokedexEntryState) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        border = CardDefaults.outlinedCardBorder(),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (entry.spriteUrl != null) {
                    AsyncImage(
                        model = entry.spriteUrl,
                        contentDescription = entry.name,
                        modifier = Modifier.height(72.dp)
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text("#${entry.entryNumber} ${entry.name}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(if (entry.captured) "Capturado" else "Visto", style = MaterialTheme.typography.bodySmall)
                }
                if (entry.legendary) {
                    AssistChip(onClick = {}, label = { Text("Lendário") })
                }
            }
            if (entry.types.isNotEmpty()) Text("Tipos: ${entry.types.joinToString()}", style = MaterialTheme.typography.bodySmall)
            if (entry.abilities.isNotEmpty()) Text("Habilidades: ${entry.abilities.joinToString()}", style = MaterialTheme.typography.bodySmall)
            if (entry.height != null && entry.weight != null) {
                Text("Altura: ${entry.height} | Peso: ${entry.weight}", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
fun PokedexSection(entries: List<PokedexEntryState>) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "📓 Pokédex viva",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = "Cada batalha registra nome e número; capturas salvam todos os detalhes, sprite e habilidades.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        entries.forEach { entry ->
            PokedexEntryCard(entry)
            Divider()
        }
    }
}

@Composable
fun BattleLogSection(log: List<BattleRecord>) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("🏆 Histórico de batalhas", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        log.forEach { record ->
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Treinador: ${record.trainer}", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                        Text("Oponente: ${record.opponent}", style = MaterialTheme.typography.bodySmall)
                    }
                    AssistChip(
                        onClick = {},
                        label = { Text(if (record.win) "Vitória" else "Derrota") },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = if (record.win) MaterialTheme.colorScheme.tertiaryContainer else MaterialTheme.colorScheme.errorContainer
                        )
                    )
                }
            }
        }
    }
}
