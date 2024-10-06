package com.example.pokapisample.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.pokapisample.data.remote.PokemonPagingDataSource
import com.example.pokapisample.data.remote.api.PokeApiService
import com.example.pokapisample.domain.PokemonItem
import com.example.pokapisample.domain.PokemonRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PokemonRepositoryImpl @Inject constructor(
    private val api: PokeApiService,
) : PokemonRepository {
    override suspend fun getPokeList(searchText: String?): Flow<PagingData<PokemonItem>> =
        Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false,
            ),
            pagingSourceFactory = { PokemonPagingDataSource(searchText = searchText, api = api) }
        ).flow
}