package com.example.pokapisample.domain

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow

interface PokemonRepository {
    suspend fun getPokeList(searchText: String?): Flow<PagingData<PokemonItem>>
}