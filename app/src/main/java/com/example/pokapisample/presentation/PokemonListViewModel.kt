package com.example.pokapisample.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.pokapisample.domain.PokemonRepository
import com.example.pokapisample.domain.PokemonItem
import com.example.pokapisample.util.IoDispatcher
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PokemonListViewModel @Inject constructor(
    private val pokemonRepository: PokemonRepository,
    @IoDispatcher private val ioDispatchers: CoroutineDispatcher,
) : ViewModel() {

    private val _searchText = MutableStateFlow("")
    val searchText = _searchText.asStateFlow()

    private val _uiState = MutableStateFlow<PagingData<PokemonItem>>(PagingData.empty())
    val uiState = _uiState.asStateFlow()

    fun getPokeList(searchText: String?) = viewModelScope.launch(ioDispatchers) {
        pokemonRepository.getPokeList(searchText)
            .distinctUntilChanged()
            .cachedIn(viewModelScope).collect {
                _uiState.value = it
            }
    }

    fun onSearchPokeTypeValueChange(searchText: String) {
        _searchText.value = searchText
    }

    fun searchPokeInType(searchText: String?) {
        getPokeList(searchText = searchText)
    }

    fun resetSearchText() {
        _searchText.value = ""
    }
}