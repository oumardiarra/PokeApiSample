package com.example.pokapisample.data.remote

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.pokapisample.data.mapper.PokemonMapper.toPokemonItem
import com.example.pokapisample.data.remote.api.PokeApiService
import com.example.pokapisample.domain.PokemonItem

class PokemonPagingDataSource(
    private val searchText: String?,
    private val api: PokeApiService,
) : PagingSource<Int, PokemonItem>() {
    companion object {
        private const val STARTING_PAGE_NUMBER = 0
    }

    override fun getRefreshKey(state: PagingState<Int, PokemonItem>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, PokemonItem> {
        return try {
            val nextPageNumber = params.key ?: STARTING_PAGE_NUMBER
            if (searchText.isNullOrEmpty() || searchText.isBlank()) {
                val response = api.getPokemonList(offset = nextPageNumber)
                LoadResult.Page(
                    data = response.pokemonTypeList.map { it.toPokemonItem() },
                    prevKey = null,
                    nextKey = if (response.nextPage == null) null else params.loadSize + nextPageNumber
                )
            } else {
                val response = api.getPokemonTypes(
                    pokemonType = searchText,
                    offset = nextPageNumber
                )
                LoadResult.Page(
                    data = response.pokemonList.map {
                        it.pokemon.toPokemonItem()
                    },
                    prevKey = null,
                    nextKey = null
                )
            }
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}