package com.trainerjourney.mypokemon

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun TrainerSection(
    trainers: List<Trainer>,
    selectedTrainer: Trainer?,
    onTrainerSelected: (Trainer) -> Unit,
    onSimulateBattle: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "Escolha um treinador para enfrentar",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(trainers) { trainer ->
                val isSelected = trainer == selectedTrainer
                OutlinedCard(
                    modifier = Modifier
                        .height(160.dp)
                        .width(240.dp),
                    colors = CardDefaults.outlinedCardColors(
                        containerColor = if (isSelected) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.SpaceBetween) {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(trainer.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Text(trainer.specialization, style = MaterialTheme.typography.bodyMedium)
                            Text("Dificuldade: ${trainer.difficulty}", style = MaterialTheme.typography.bodySmall)
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(onClick = { onTrainerSelected(trainer) }) {
                                Text(if (isSelected) "Selecionado" else "Desafiar")
                            }
                            if (isSelected) {
                                TextButton(onClick = onSimulateBattle) { Text("Simular batalha") }
                            }
                        }
                    }
                }
            }
        }
        selectedTrainer?.let {
            Text(
                text = "Você desafiou ${it.name}! Use encontros para registrar na Pokédex ou simule uma batalha.",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
