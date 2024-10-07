package com.example.pokapisample.presentation

import androidx.paging.PagingData
import com.example.pokapisample.data.repository.PokemonRepositoryImpl
import com.example.pokapisample.domain.PokemonItem
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test

class PokemonListViewModelTest: BaseCoroutineTest(){

    private val pokeRepositoryImpl: PokemonRepositoryImpl = mockk()

    private lateinit var pokemonViewModel: PokemonListViewModel

    @Before
    fun setUp() {
        pokemonViewModel = PokemonListViewModel(
            pokemonRepository = pokeRepositoryImpl,
            ioDispatchers = testDispatcher
        )
    }

    @Test
    fun `onSearchPokeTypeValueChange - should set searchText value`() {
        // When
        pokemonViewModel.onSearchPokeTypeValueChange("search")
        // Then
        assertThat(pokemonViewModel.searchText.value).isEqualTo("search")
    }

    @Test
    fun `resetSearchText - should reset searchText value`() {
        // When
        pokemonViewModel.resetSearchText()
        // Then
        assertThat(pokemonViewModel.searchText.value).isEmpty()
    }

    @Test
    fun `getPokeList - should return data`() = runTest {
        // Given
        val mockedPokeItems = listOf(
            PokemonItem(
                id = 1,
                name = "bulbasaur",
                url = "https://pokeapi.co/api/v2/pokemon/1/",
                pokemonImageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/home/1.png"
            ),
            PokemonItem(
                id = 2,
                name = "ivysaur",
                url = "https://pokeapi.co/api/v2/pokemon/2/",
                pokemonImageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/home/2.png"
            ),
            PokemonItem(
                id = 3,
                name = "venusaur",
                url = "https://pokeapi.co/api/v2/pokemon/3/",
                pokemonImageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/home/3.png"
            ),
        )
        val pagingData = PagingData.from(
            data = mockedPokeItems
        )
        val flowPagingData = flow { emit(pagingData) }

        coEvery { pokeRepositoryImpl.getPokeList(any()) }.returns(flowPagingData)

        // When
        pokemonViewModel.getPokeList(searchText = "searchText")

        // Then
        scheduler.advanceUntilIdle()
        //Issue: This assert will fail due to difficulty on comparing 2 paging data. should try to call asSnapshot()
        assertThat(pokemonViewModel.uiState.value).isEqualTo(pagingData)
    }
}