package com.example.pokapisample.data.remote.dto

import com.squareup.moshi.Json

data class PokemonListDataDto(
    @Json(name = "pokemon")
    val pokemonList: List<PokemonDataDto>
)
data class PokemonDataDto(
    @Json(name = "pokemon")
    val pokemon: PokemonItemDto
)
