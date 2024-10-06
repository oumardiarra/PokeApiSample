package com.example.pokapisample.data.remote.dto

import com.squareup.moshi.Json

data class PokemonItemDto(
    @Json(name = "name")
    val name: String,
    @Json(name = "url")
    val url: String,
)
