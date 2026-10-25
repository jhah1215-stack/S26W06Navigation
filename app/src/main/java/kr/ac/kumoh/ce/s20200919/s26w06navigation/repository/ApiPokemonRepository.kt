package kr.ac.kumoh.ce.s20200919.s26w06navigation.repository

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.serialization.json.Json
import kr.ac.kumoh.ce.s20200919.s26w06navigation.model.Pokemon
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

private const val KOREAN = "ko"

class ApiPokemonRepository(
    private val api: PokeApi = pokeService
) : PokemonRepository {

    companion object {
        private val jsonConfig = Json { ignoreUnknownKeys = true }

        private val pokeService: PokeApi by lazy {
            Retrofit.Builder()
                .baseUrl("https://pokeapi.co/api/v2/")
                .addConverterFactory(
                    jsonConfig.asConverterFactory("application/json".toMediaType())
                )
                .build()
                .create(PokeApi::class.java)
        }
    }

    override suspend fun getAllPokemon(): List<Pokemon> = coroutineScope {
        api.getPokemonList().results
            .mapNotNull { result ->
                val id = result.url
                    .trimEnd('/')
                    .substringAfterLast('/')
                    .toIntOrNull() ?: return@mapNotNull null

                async {
                    runCatching { fetchPokemon(id) }.getOrNull()
                }
            }
            .awaitAll()
            .filterNotNull()
    }

    override suspend fun getPokemonById(id: Int): Pokemon? =
        runCatching { fetchPokemon(id) }.getOrNull()

    private suspend fun fetchPokemon(id: Int): Pokemon = coroutineScope {
        val detail = async { api.getPokemonDetail(id) }
        val species = async { runCatching { api.getPokemonSpecies(id) }.getOrNull() }

        val dto = detail.await()
        val speciesDto = species.await()

        val typeList = dto.types
            .sortedBy { it.slot }
            .map { it.type.name }

        val types = typeList
            .joinToString(", ")
            .ifEmpty { "unknown" }

        Pokemon(
            id = dto.id,
            name = speciesDto?.koreanName ?: dto.name,
            type = types,
            info = speciesDto?.koreanInfo.orEmpty(),
            image = dto.sprites.frontDefault ?: ""
        )
    }
}

private val PokemonSpeciesDto.koreanName: String?
    get() = names.firstOrNull { it.language.name == KOREAN }?.name

private val PokemonSpeciesDto.koreanInfo: String?
    get() = flavorTextEntries
        .firstOrNull { it.language.name == KOREAN }
        ?.flavorText
        ?.replace(Regex("\\s+"), " ")
        ?.trim()