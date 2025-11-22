package com.trainerjourney.mypokemon

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenu
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberUpdatedState
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
fun CaptureSection(
    captureState: CaptureState,
    onRegionChange: (String) -> Unit,
    onPokemonNameChange: (String) -> Unit,
    onCapture: () -> Unit
) {
    val regions = listOf("Kanto", "Johto", "Hoenn", "Sinnoh", "Unova", "Kalos", "Galar", "Paldea")
    var expanded by remember { mutableStateOf(false) }
    val selectedRegion by rememberUpdatedState(newValue = captureState.region)

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "🎯 Capturar Pokémon por nome",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = "Usa a PokéAPI para checar se é lendário, calcular chance e registrar na Pokédex com detalhes completos ao capturar.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            androidx.compose.material3.OutlinedTextField(
                value = selectedRegion,
                onValueChange = {},
                readOnly = true,
                label = { Text("Região") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth(),
                colors = TextFieldDefaults.outlinedTextFieldColors()
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                regions.forEach { region ->
                    androidx.compose.material3.DropdownMenuItem(
                        text = { Text(region) },
                        onClick = {
                            onRegionChange(region)
                            expanded = false
                        }
                    )
                }
            }
        }
        androidx.compose.material3.OutlinedTextField(
            value = captureState.pokemonName,
            onValueChange = onPokemonNameChange,
            label = { Text("Nome do Pokémon") },
            modifier = Modifier.fillMaxWidth()
        )
        Button(
            onClick = onCapture,
            enabled = !captureState.isLoading,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (captureState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.height(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(if (captureState.isLoading) "Buscando..." else "Tentar capturar")
        }

        captureState.error?.let {
            Text(text = it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        }

        captureState.result?.let { result ->
            val containerColor = if (result.success) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surfaceVariant
            Card(colors = CardDefaults.cardColors(containerColor = containerColor)) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(result.message, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(result.chanceDescription, style = MaterialTheme.typography.bodySmall)
                    Text("Região: ${result.region}", style = MaterialTheme.typography.bodySmall)
                    PokedexEntryCard(entry = result.target)
                }
            }
        }
    }
}
