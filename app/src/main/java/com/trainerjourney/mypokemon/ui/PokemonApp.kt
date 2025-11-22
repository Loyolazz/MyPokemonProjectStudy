package com.trainerjourney.mypokemon.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.trainerjourney.mypokemon.BattleLogSection
import com.trainerjourney.mypokemon.CaptureSection
import com.trainerjourney.mypokemon.EncounterSection
import com.trainerjourney.mypokemon.PokedexSection
import com.trainerjourney.mypokemon.SelectedTeamSection
import com.trainerjourney.mypokemon.TeamSelectionSection
import com.trainerjourney.mypokemon.TrainerSection
import com.trainerjourney.mypokemon.TRAINERS
import com.trainerjourney.mypokemon.PokemonViewModel

@Composable
fun PokemonApp(viewModel: PokemonViewModel = viewModel()) {
    val state = viewModel.uiState

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                        MaterialTheme.colorScheme.background
                    )
                )
            )
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = { PokedexTopBar() }
        ) { innerPadding ->
            LazyColumn(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item { HeroHeader() }

                item {
                    SectionContainer {
                        TeamSelectionSection(
                            teams = viewModel.availableTeams(),
                            onSelectTeam = viewModel::selectTeam,
                            capturedAvailable = state.capturedPokemon.isNotEmpty(),
                            onUseCaptured = viewModel::useCustomTeam
                        )
                    }
                }

                state.selectedTeam?.let { team ->
                    item { SectionContainer { SelectedTeamSection(team = team) } }
                    item {
                        SectionContainer {
                            TrainerSection(
                                trainers = TRAINERS,
                                selectedTrainer = state.selectedTrainer,
                                onTrainerSelected = viewModel::selectTrainer,
                                onSimulateBattle = viewModel::simulateTrainerBattle
                            )
                        }
                    }
                }

                item {
                    SectionContainer {
                        EncounterSection(
                            encounterState = state.encounterState,
                            onRegionChange = viewModel::onRegionChange,
                            onGenerate = viewModel::generateEncounter,
                            onCapture = { viewModel.attemptCapture(it) }
                        )
                    }
                }

                item {
                    SectionContainer {
                        CaptureSection(
                            captureState = state.captureState,
                            onRegionChange = viewModel::onRegionChange,
                            onPokemonNameChange = viewModel::onPokemonNameChange,
                            onCapture = { viewModel.attemptCapture(null) }
                        )
                    }
                }

                if (state.pokedex.isNotEmpty()) {
                    item { SectionContainer { PokedexSection(entries = state.pokedex) } }
                }

                if (state.battleLog.isNotEmpty()) {
                    item { SectionContainer { BattleLogSection(log = state.battleLog) } }
                }
            }
        }
    }
}

@Composable
private fun PokedexTopBar() {
    CenterAlignedTopAppBar(
        title = {
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = "Pokédex Adventure",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Text(
                    text = "Powered by PokéAPI",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
                )
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary
        )
    )
}

@Composable
private fun HeroHeader() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.9f)
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.tertiary.copy(alpha = 0.4f))
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                text = "Monte sua aventura Pokémon",
                style = MaterialTheme.typography.displaySmall,
                color = MaterialTheme.colorScheme.onPrimary
            )
            Text(
                text = "Escolha uma equipe, desafie treinadores e capture novos parceiros usando a PokéAPI.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f)
            )
            Text(
                text = "Cada encontro registra sua jornada na Pokédex viva.",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}

@Composable
private fun SectionContainer(content: @Composable () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Box(
            modifier = Modifier
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.surface,
                            MaterialTheme.colorScheme.surfaceVariant
                        )
                    )
                )
                .padding(16.dp)
        ) {
            content()
        }
    }
}
