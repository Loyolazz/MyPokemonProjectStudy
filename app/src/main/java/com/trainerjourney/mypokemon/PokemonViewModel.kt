package com.trainerjourney.mypokemon

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trainerjourney.mypokemon.data.PokedexEntryResponse
import com.trainerjourney.mypokemon.data.PokemonDetailResponse
import com.trainerjourney.mypokemon.data.PokemonRepository
import com.trainerjourney.mypokemon.data.PokemonSpeciesResponse
import kotlin.math.roundToInt
import kotlin.random.Random
import kotlinx.coroutines.launch

class PokemonViewModel(
    private val repository: PokemonRepository = PokemonRepository()
) : ViewModel() {

    var uiState by mutableStateOf(PokemonUiState())
        private set

    private val predefinedTeams = listOf(
        TeamOption(
            name = "Clássicos de Kanto",
            description = "Equipe balanceada com tipos iniciais icônicos.",
            members = listOf(
                TeamMember("Pikachu", listOf("Electric"), listOf("Thunder Shock", "Quick Attack", "Iron Tail")),
                TeamMember("Charizard", listOf("Fire", "Flying"), listOf("Flamethrower", "Air Slash", "Fire Blast")),
                TeamMember("Blastoise", listOf("Water"), listOf("Hydro Pump", "Rapid Spin", "Ice Beam")),
                TeamMember("Venusaur", listOf("Grass", "Poison"), listOf("Solar Beam", "Sleep Powder", "Flash"))
            )
        ),
        TeamOption(
            name = "Exploradores de Galar",
            description = "Ataques rápidos com cobertura elemental.",
            members = listOf(
                TeamMember("Cinderace", listOf("Fire"), listOf("Pyro Ball", "Quick Attack", "Feint")),
                TeamMember("Inteleon", listOf("Water"), listOf("Snipe Shot", "Hydro Pump", "Ice Punch")),
                TeamMember("Rillaboom", listOf("Grass"), listOf("Drum Beating", "Giga Drain", "Wood Hammer")),
                TeamMember("Corviknight", listOf("Steel", "Flying"), listOf("Brave Bird", "Steel Wing", "Metal Claw"))
            )
        ),
        TeamOption(
            name = "Estratégia Lendária",
            description = "Foco em lendários com controle e dano massivo.",
            members = listOf(
                TeamMember("Suicune", listOf("Water"), listOf("Hydro Pump", "Icy Wind", "Aurora Beam")),
                TeamMember("Zapdos", listOf("Electric", "Flying"), listOf("Thunderbolt", "Hurricane", "Thunder Shock")),
                TeamMember("Mew", listOf("Psychic"), listOf("Psychic", "Shadow Ball", "Metronome")),
                TeamMember("Garchomp", listOf("Dragon", "Ground"), listOf("Dragon Claw", "Earthquake", "Stone Edge"))
            )
        )
    )

    private val signatureOpponents = mapOf(
        "Brock" to "onix",
        "Misty" to "starmie",
        "Cynthia" to "garchomp",
        "Leon" to "charizard"
    )

    private val regionDexCache: MutableMap<String, List<PokedexEntryResponse>> = mutableMapOf()

    fun selectTeam(team: TeamOption) {
        uiState = uiState.copy(selectedTeam = team, selectedTrainer = null)
    }

    fun selectTrainer(trainer: Trainer) {
        uiState = uiState.copy(selectedTrainer = trainer)
    }

    fun onRegionChange(region: String) {
        uiState = uiState.copy(
            captureState = uiState.captureState.copy(region = region),
            encounterState = uiState.encounterState.copy(region = region)
        )
    }

    fun onPokemonNameChange(name: String) {
        uiState = uiState.copy(captureState = uiState.captureState.copy(pokemonName = name))
    }

    fun useCustomTeam() {
        if (uiState.capturedPokemon.isNotEmpty()) {
            val customTeam = TeamOption(
                name = "Equipe Capturada",
                description = "Usando os Pokémon que você capturou",
                members = uiState.capturedPokemon
            )
            selectTeam(customTeam)
        }
    }

    fun generateEncounter() {
        viewModelScope.launch {
            uiState = uiState.copy(encounterState = uiState.encounterState.copy(isLoading = true, error = null))
            val region = uiState.encounterState.region.ifEmpty { "kanto" }
            try {
                val entries = regionDexCache[region] ?: repository.fetchRegionDex(region).also {
                    regionDexCache[region] = it
                }
                if (entries.isEmpty()) throw IllegalStateException("Pokedéx vazia para $region")
                val selected = entries.random()
                val species = repository.fetchSpecies(selected.species.name)

                val encounter = EncounteredPokemon(
                    entryNumber = selected.entryNumber,
                    name = selected.species.name.capitalizeFirst(),
                    region = region.capitalizeFirst(),
                    legendary = species.isLegendary || species.isMythical,
                    habitat = species.habitat?.name?.capitalizeFirst(),
                    captureRate = species.captureRate
                )

                registerSeen(encounter.entryNumber, encounter.name, region)

                uiState = uiState.copy(
                    encounterState = uiState.encounterState.copy(
                        isLoading = false,
                        current = encounter,
                        error = null
                    )
                )
            } catch (ex: Exception) {
                uiState = uiState.copy(
                    encounterState = uiState.encounterState.copy(
                        isLoading = false,
                        error = ex.message ?: "Erro ao gerar encontro"
                    )
                )
            }
        }
    }

    fun simulateTrainerBattle() {
        val trainer = uiState.selectedTrainer ?: return
        viewModelScope.launch {
            uiState = uiState.copy(encounterState = uiState.encounterState.copy(isLoading = true, error = null))
            val pokemonName = signatureOpponents[trainer.name]?.lowercase() ?: trainer.specialization.split(" ")
                .firstOrNull()
                ?.lowercase()
                ?: "pikachu"
            try {
                val species = repository.fetchSpecies(pokemonName)
                val details = repository.fetchPokemonDetails(pokemonName)
                registerSeen(details.id, details.name.capitalizeFirst(), uiState.captureState.region)

                val win = Random.nextBoolean()
                val record = BattleRecord(
                    trainer = trainer.name,
                    opponent = details.name.capitalizeFirst(),
                    win = win
                )
                val encounter = EncounteredPokemon(
                    entryNumber = details.id,
                    name = details.name.capitalizeFirst(),
                    region = uiState.captureState.region.ifEmpty { "Kanto" },
                    legendary = species.isLegendary || species.isMythical,
                    habitat = species.habitat?.name?.capitalizeFirst(),
                    captureRate = species.captureRate
                )

                uiState = uiState.copy(
                    battleLog = (listOf(record) + uiState.battleLog).take(8),
                    encounterState = uiState.encounterState.copy(
                        isLoading = false,
                        current = encounter
                    )
                )
            } catch (ex: Exception) {
                uiState = uiState.copy(
                    encounterState = uiState.encounterState.copy(
                        isLoading = false,
                        error = "Treinador sem dados: ${ex.message}".take(120)
                    )
                )
            }
        }
    }

    fun attemptCapture(targetName: String? = null) {
        val pokemonName = targetName?.ifBlank { null } ?: uiState.encounterState.current?.name ?: uiState.captureState.pokemonName
        if (pokemonName.isBlank()) {
            uiState = uiState.copy(
                captureState = uiState.captureState.copy(
                    error = "Digite ou escolha um Pokémon para capturar."
                )
            )
            return
        }

        viewModelScope.launch {
            uiState = uiState.copy(
                captureState = uiState.captureState.copy(isLoading = true, error = null, result = null)
            )
            try {
                val species = repository.fetchSpecies(pokemonName)
                val details = repository.fetchPokemonDetails(pokemonName)
                val baseChance = (species.captureRate.toDouble() / 255.0).coerceIn(0.05, 0.95)
                val adjustedChance = if (species.isLegendary || species.isMythical) baseChance * 0.35 else baseChance
                val success = Random.nextDouble() <= adjustedChance
                val chancePercent = (adjustedChance * 100).roundToInt()

                val member = buildMemberFromDetails(details, species)

                if (success) registerCaptured(member, uiState.captureState.region)
                else registerSeen(member.entryNumber, member.name, uiState.captureState.region)

                uiState = uiState.copy(
                    capturedPokemon = if (success) uiState.capturedPokemon + member.toTeamMember() else uiState.capturedPokemon,
                    captureState = uiState.captureState.copy(
                        isLoading = false,
                        result = CaptureResult(
                            success = success,
                            message = if (success) "Você capturou ${member.name}!" else "${member.name} escapou...",
                            chanceDescription = "Chance estimada: $chancePercent%",
                            region = uiState.captureState.region.ifEmpty { "Qualquer" },
                            target = member
                        )
                    )
                )
            } catch (ex: Exception) {
                uiState = uiState.copy(
                    captureState = uiState.captureState.copy(
                        isLoading = false,
                        error = "Não foi possível buscar ${pokemonName}: ${ex.message}".take(120)
                    )
                )
            }
        }
    }

    private fun buildMemberFromDetails(
        details: PokemonDetailResponse,
        species: PokemonSpeciesResponse
    ): PokedexEntryState {
        return PokedexEntryState(
            entryNumber = details.id,
            name = details.name.capitalizeFirst(),
            region = uiState.captureState.region.ifEmpty { "Kanto" },
            captured = true,
            types = details.types.map { it.type.name.capitalizeFirst() },
            spriteUrl = details.sprites.frontDefault,
            height = details.height,
            weight = details.weight,
            abilities = details.abilities.map { it.ability.name.capitalizeFirst() },
            legendary = species.isLegendary || species.isMythical
        )
    }

    private fun registerSeen(entryNumber: Int, name: String, region: String) {
        val current = uiState.pokedex.find { it.entryNumber == entryNumber }
        val updated = current?.copy(seen = true) ?: PokedexEntryState(
            entryNumber = entryNumber,
            name = name.capitalizeFirst(),
            region = region.capitalizeFirst(),
            seen = true
        )
        commitPokedex(updated)
    }

    private fun registerCaptured(entry: PokedexEntryState, region: String) {
        val enriched = entry.copy(region = region.capitalizeFirst(), seen = true, captured = true)
        commitPokedex(enriched)
    }

    private fun commitPokedex(entry: PokedexEntryState) {
        val mutable = uiState.pokedex.toMutableList()
        val index = mutable.indexOfFirst { it.entryNumber == entry.entryNumber }
        if (index >= 0) mutable[index] = entry else mutable.add(entry)
        uiState = uiState.copy(pokedex = mutable.sortedBy { it.entryNumber })
    }

    private fun String.capitalizeFirst(): String = replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }

    private fun PokedexEntryState.toTeamMember(): TeamMember = TeamMember(
        name = name,
        types = types.ifEmpty { listOf("Unknown") },
        attacks = listOf("Quick Attack", "Tackle", "Special Strike", "Secret Technique"),
        spriteUrl = spriteUrl,
        legendary = legendary
    )

    fun availableTeams(): List<TeamOption> = predefinedTeams
}

data class PokemonUiState(
    val selectedTeam: TeamOption? = null,
    val selectedTrainer: Trainer? = null,
    val capturedPokemon: List<TeamMember> = emptyList(),
    val captureState: CaptureState = CaptureState(),
    val encounterState: EncounterState = EncounterState(),
    val pokedex: List<PokedexEntryState> = emptyList(),
    val battleLog: List<BattleRecord> = emptyList()
)

data class TeamOption(
    val name: String,
    val description: String,
    val members: List<TeamMember>
)

data class TeamMember(
    val name: String,
    val types: List<String>,
    val attacks: List<String>,
    val spriteUrl: String? = null,
    val legendary: Boolean = false
)

data class Trainer(
    val name: String,
    val specialization: String,
    val difficulty: String
)

data class CaptureState(
    val region: String = "Kanto",
    val pokemonName: String = "",
    val isLoading: Boolean = false,
    val result: CaptureResult? = null,
    val error: String? = null
)

data class EncounterState(
    val region: String = "Kanto",
    val isLoading: Boolean = false,
    val current: EncounteredPokemon? = null,
    val error: String? = null
)

data class EncounteredPokemon(
    val entryNumber: Int,
    val name: String,
    val region: String,
    val legendary: Boolean,
    val habitat: String?,
    val captureRate: Int
)

data class CaptureResult(
    val success: Boolean,
    val message: String,
    val chanceDescription: String,
    val region: String,
    val target: PokedexEntryState
)

data class PokedexEntryState(
    val entryNumber: Int,
    val name: String,
    val region: String,
    val seen: Boolean = false,
    val captured: Boolean = false,
    val types: List<String> = emptyList(),
    val spriteUrl: String? = null,
    val height: Int? = null,
    val weight: Int? = null,
    val abilities: List<String> = emptyList(),
    val legendary: Boolean = false
)

data class BattleRecord(
    val trainer: String,
    val opponent: String,
    val win: Boolean
)

val TRAINERS = listOf(
    Trainer("Brock", "Rock & Defense", "Iniciante"),
    Trainer("Misty", "Water & Control", "Médio"),
    Trainer("Cynthia", "Strong Dragons", "Difícil"),
    Trainer("Leon", "Campeão de Galar", "Muito Difícil")
)
