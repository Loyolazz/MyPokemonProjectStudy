package com.trainerjourney.mypokemon.data

import com.google.gson.annotations.SerializedName
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

interface PokeApiService {
    @GET("pokemon-species/{name}")
    suspend fun getPokemonSpecies(@Path("name") name: String): PokemonSpeciesResponse

    @GET("pokemon/{name}")
    suspend fun getPokemonDetail(@Path("name") name: String): PokemonDetailResponse

    @GET("pokedex/{region}")
    suspend fun getPokedexByRegion(@Path("region") region: String): PokedexResponse
}

object PokeApiFactory {
    private const val BASE_URL = "https://pokeapi.co/api/v2/"

    fun create(): PokeApiService {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PokeApiService::class.java)
    }
}

data class PokemonSpeciesResponse(
    val name: String,
    @SerializedName("is_legendary") val isLegendary: Boolean,
    @SerializedName("is_mythical") val isMythical: Boolean,
    @SerializedName("capture_rate") val captureRate: Int,
    val habitat: NamedApiResource?
)

data class PokemonDetailResponse(
    val id: Int,
    val name: String,
    val height: Int,
    val weight: Int,
    val types: List<TypeSlot>,
    val sprites: Sprites,
    val abilities: List<AbilitySlot>
)

data class TypeSlot(
    val slot: Int,
    val type: NamedApiResource
)

data class AbilitySlot(
    val ability: NamedApiResource,
    @SerializedName("is_hidden") val isHidden: Boolean,
)

data class NamedApiResource(
    val name: String,
    val url: String?,
)

data class Sprites(
    @SerializedName("front_default") val frontDefault: String?,
)

data class PokedexResponse(
    @SerializedName("pokemon_entries") val entries: List<PokedexEntryResponse>
)

data class PokedexEntryResponse(
    @SerializedName("entry_number") val entryNumber: Int,
    @SerializedName("pokemon_species") val species: NamedApiResource
)
