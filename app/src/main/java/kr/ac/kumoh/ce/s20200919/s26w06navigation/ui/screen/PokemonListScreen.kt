package kr.ac.kumoh.ce.s20200919.s26w06navigation.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import kr.ac.kumoh.ce.s20200919.s26w06navigation.model.Pokemon
import kr.ac.kumoh.ce.s20200919.s26w06navigation.repository.PokemonRepository
import kr.ac.kumoh.ce.s20200919.s26w06navigation.viewmodel.PokemonListUiState
import kr.ac.kumoh.ce.s20200919.s26w06navigation.viewmodel.PokemonListViewModel

@Composable
fun PokemonListScreen(
    repository: PokemonRepository,
    viewModel: PokemonListViewModel = viewModel {
        PokemonListViewModel(repository)
    },
    onNavigateToDetail: (Int) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val query by viewModel.query.collectAsStateWithLifecycle()

    when (val state = uiState) {
        is PokemonListUiState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator()
            }
        }

        is PokemonListUiState.Error -> {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = "목록을 불러오지 못했어요.\n네트워크를 확인해 주세요.",
                    style = MaterialTheme.typography.bodyLarge,
                )
                Button(
                    onClick = { viewModel.load() },
                    modifier = Modifier.padding(top = 16.dp),
                ) {
                    Text("다시 시도")
                }
            }
        }

        is PokemonListUiState.Success -> {
            // 목록이나 검색어가 바뀔 때만 다시 계산
            val filtered = remember(state.pokemonList, query) {
                val q = query.trim()
                if (q.isEmpty()) {
                    state.pokemonList
                } else {
                    state.pokemonList.filter {
                        it.name.contains(q, ignoreCase = true) ||
                                it.type.contains(q, ignoreCase = true)
                    }
                }
            }

            Column(modifier = Modifier.fillMaxSize()) {
                OutlinedTextField(
                    value = query,
                    onValueChange = viewModel::onQueryChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    placeholder = { Text("포켓몬 이름 검색") },
                    trailingIcon = {
                        if (query.isNotEmpty()) {
                            IconButton(onClick = { viewModel.onQueryChange("") }) {
                                Text("✕")  // 아이콘 라이브러리 없이 글자로 대체
                            }
                        }
                    },
                    singleLine = true,
                )

                if (filtered.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text("검색 결과가 없어요.")
                    }
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(
                            start = 16.dp, end = 16.dp, bottom = 16.dp
                        ),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(filtered) { pokemon ->
                            PokemonCard(
                                pokemon = pokemon,
                                onClick = { onNavigateToDetail(pokemon.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PokemonCard(
    pokemon: Pokemon,
    onClick: () -> Unit,
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AsyncImage(
                model = pokemon.image,
                contentDescription = "${pokemon.name} 이미지",
                modifier = Modifier
                    .size(64.dp)
                    .background(
                        MaterialTheme.colorScheme.primaryContainer,
                        RoundedCornerShape(8.dp)
                    ),
            )
            Column(
                modifier = Modifier.padding(start = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(  // 카드에 도감 번호 나오게 하기 AI 도움
                    text = "No.%03d".format(pokemon.id),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text = pokemon.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = pokemon.type,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}