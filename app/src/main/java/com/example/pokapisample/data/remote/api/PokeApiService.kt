package com.example.pokapisample.data.remote.api

import com.example.pokapisample.data.remote.dto.PokemonEntryDto
import com.example.pokapisample.data.remote.dto.PokemonListDataDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface PokeApiService {

    companion object {
        const val BASE_URL = "https://pokeapi.co/api/v2/"
    }

    @GET("pokemon")
    suspend fun getPokemonList(
        @Query("offset") offset: Int,
    ): PokemonEntryDto

    @GET("type/{pokemonType}")
    suspend fun getPokemonTypes(
        @Path("pokemonType") pokemonType: String,
        @Query("offset") offset: Int,
    ): PokemonListDataDto
}