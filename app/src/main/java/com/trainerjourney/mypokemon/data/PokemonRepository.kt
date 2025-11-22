package com.trainerjourney.mypokemon.data

class PokemonRepository(private val api: PokeApiService = PokeApiFactory.create()) {
    suspend fun fetchSpecies(name: String): PokemonSpeciesResponse {
        return api.getPokemonSpecies(name.lowercase())
    }

    suspend fun fetchPokemonDetails(name: String): PokemonDetailResponse {
        return api.getPokemonDetail(name.lowercase())
    }

    suspend fun fetchRegionDex(region: String): List<PokedexEntryResponse> {
        return api.getPokedexByRegion(region.lowercase()).entries
    }
}
