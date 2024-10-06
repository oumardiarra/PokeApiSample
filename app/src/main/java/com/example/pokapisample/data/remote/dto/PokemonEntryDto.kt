package com.example.pokapisample.data.remote.dto

import com.squareup.moshi.Json

data class PokemonEntryDto(
    @Json(name = "count")
    val count: Int,
    @Json(name = "next")
    val nextPage: String?,
    @Json(name = "previous")
    val previousPage: String?,
    @Json(name = "results")
    val pokemonTypeList: List<PokemonItemDto>
)

