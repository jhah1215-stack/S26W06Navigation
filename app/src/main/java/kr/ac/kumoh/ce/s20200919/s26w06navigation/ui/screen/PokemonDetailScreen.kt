package kr.ac.kumoh.ce.s20200919.s26w06navigation.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import kr.ac.kumoh.ce.s20200919.s26w06navigation.repository.PokemonRepository
import kr.ac.kumoh.ce.s20200919.s26w06navigation.viewmodel.PokemonDetailViewModel

@Composable
fun PokemonDetailScreen(
    pokemonId: Int,
    repository: PokemonRepository,
    viewModel: PokemonDetailViewModel = viewModel {
        PokemonDetailViewModel(
            repository = repository,
            pokemonId = pokemonId
        )
    },
) {
    val pokemon by viewModel.pokemon.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        AsyncImage(
            model = pokemon.image,
            contentDescription = "${pokemon.name} 이미지",
            filterQuality = FilterQuality.None,
            modifier = Modifier
                .size(220.dp)
                .background(
                    MaterialTheme.colorScheme.primaryContainer,
                    RoundedCornerShape(24.dp)
                ),
        )

        if (pokemon.id > 0) {
            Text(
                text = "No.%03d".format(pokemon.id),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 16.dp),
            )
        }
        Text(
            text = pokemon.name,
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(top = 12.dp),
        ) {
            pokemon.type
                .split(",", "/")
                .map { it.trim() }
                .filter { it.isNotEmpty() }
                .forEach { TypeChip(it) }
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp),
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "설명",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = pokemon.info.ifBlank { "설명이 없습니다." },
                    modifier = Modifier.padding(top = 8.dp),
                )
            }
        }
    }
}

@Composable
private fun TypeChip(type: String) {
    Text(
        text = type,
        color = Color.White,
        style = MaterialTheme.typography.labelLarge,
        modifier = Modifier
            .background(typeColor(type), RoundedCornerShape(50))
            .padding(horizontal = 14.dp, vertical = 6.dp),
    )
}
//AI한테 도움받은 내용
private fun typeColor(type: String): Color = when (type.lowercase()) {
    "grass", "풀" -> Color(0xFF4CAF50)
    "poison", "독" -> Color(0xFF9C27B0)
    "fire", "불꽃" -> Color(0xFFFF7043)
    "water", "물" -> Color(0xFF2196F3)
    "electric", "전기" -> Color(0xFFF9A825)
    "flying" -> Color(0xFF64B5F6)
    "bug" -> Color(0xFF9E9D24)
    "normal", "노말" -> Color(0xFF9E9E9E)
    "psychic", "에스퍼" -> Color(0xFFE91E63)
    "ground" -> Color(0xFF8D6E63)
    "rock" -> Color(0xFF795548)
    "fairy" -> Color(0xFFF48FB1)
    "fighting" -> Color(0xFFD32F2F)
    "ghost" -> Color(0xFF5E35B1)
    "ice" -> Color(0xFF4DD0E1)
    "dragon" -> Color(0xFF3F51B5)
    "steel" -> Color(0xFF78909C)
    "dark" -> Color(0xFF424242)
    else -> Color(0xFF757575)
}