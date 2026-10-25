package kr.ac.kumoh.ce.s20200919.s26w06navigation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kr.ac.kumoh.ce.s20200919.s26w06navigation.model.Pokemon
import kr.ac.kumoh.ce.s20200919.s26w06navigation.repository.PokemonRepository

// 화면이 가질 수 있는 상태를 3가지로 제한 AI한테 도움 받기
sealed interface PokemonListUiState {
    data object Loading : PokemonListUiState
    data class Success(val pokemonList: List<Pokemon>) : PokemonListUiState
    data object Error : PokemonListUiState
}

class PokemonListViewModel(
    private val repository: PokemonRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<PokemonListUiState>(PokemonListUiState.Loading)
    val uiState: StateFlow<PokemonListUiState> = _uiState.asStateFlow()

    // 검색어: ViewModel에 두면 상세 화면에 갔다가 돌아와도 유지된다 AI한테 도움 받기
    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    init {
        load()
    }

    fun onQueryChange(newQuery: String) {
        _query.value = newQuery
    }

    // "다시 시도" 버튼에서도 호출 AI 도움 받기
    fun load() {
        _uiState.value = PokemonListUiState.Loading
        viewModelScope.launch {
            _uiState.value = try {
                val list = repository.getAllPokemon()
                // 네트워크 실패 시 Repository가 빈 목록을 돌려주므로 비었으면 에러로 취급
                if (list.isEmpty()) PokemonListUiState.Error
                else PokemonListUiState.Success(list)
            } catch (e: CancellationException) {
                throw e  // 코루틴 취소는 에러가 아니므로 다시 던짐
            } catch (e: Exception) {
                PokemonListUiState.Error
            }
        }
    }
}