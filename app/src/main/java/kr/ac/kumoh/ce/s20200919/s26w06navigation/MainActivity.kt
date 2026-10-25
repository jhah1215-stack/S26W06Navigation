package kr.ac.kumoh.ce.s20200919.s26w06navigation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import kotlinx.serialization.Serializable
import kr.ac.kumoh.ce.s20200919.s26w06navigation.ui.theme.S26W06NavigationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            S26W06NavigationTheme {
                MainScreen()
            }
        }
    }
}

// 1. NavKey: 화면 식별자
sealed interface PokemonRoute : NavKey {
    @Serializable
    data object PokemonList : PokemonRoute

    @Serializable
    data class PokemonDetail(val pokemonId: Int) : PokemonRoute
}

@Composable
fun MainScreen() {
    val backStack = rememberNavBackStack(PokemonRoute.PokemonList)

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        NavDisplay(
            modifier = Modifier.padding(innerPadding),
            backStack = backStack,
            onBack = { backStack.removeLastOrNull() },
            entryDecorators = listOf(
                rememberSaveableStateHolderNavEntryDecorator()
            ),
            entryProvider = entryProvider {
                entry<PokemonRoute.PokemonList> {
                    PokemonListScreen(
                        onClick = { id ->
                            backStack.add(PokemonRoute.PokemonDetail(id))
                        }
                    )
                }
                entry<PokemonRoute.PokemonDetail> { key ->
                    PokemonDetailScreen(key.pokemonId)
                }
            }
        )
    }
}

data class Pokemon(
    val id: Int,
    val name: String,
    val description: String,
)

val pokemonList = listOf(
    Pokemon(1, "이상해씨", "태어났을 때부터 등에 씨앗이 있고, 햇빛을 받으며 조금씩 자란다."),
    Pokemon(4, "파이리", "꼬리 끝에 불꽃이 타오르고 있고, 불꽃이 꺼지면 생명이 위험하다."),
    Pokemon(7, "꼬부기", "등껍질로 몸을 보호하고, 입에서 강한 물줄기를 뿜어낸다."),
    Pokemon(25, "피카츄", "양 볼에 전기 주머니가 있어서 위기에 처하면 전기를 방출한다."),
)


@Composable
fun PokemonListScreen(onClick: (Int) -> Unit) {
    Column {
        pokemonList.forEach { pokemon ->
            Button(onClick = { onClick(pokemon.id) }) {
                Text(pokemon.name)
            }
        }
    }
}

@Composable
fun PokemonDetailScreen(pokemonId: Int) {
    val pokemon = pokemonList.find { it.id == pokemonId }
    Text(pokemon?.description ?: "포켓몬을 찾을 수 없어요")
}