package com.example.pokapisample.data.mapper

import com.example.pokapisample.data.remote.dto.PokemonItemDto
import com.example.pokapisample.domain.PokemonItem

object PokemonMapper {

    fun PokemonItemDto.toPokemonItem(): PokemonItem {
        val pokemonId = url.substringAfter("pokemon").replace("/", "").toInt()
        return PokemonItem(
            id = pokemonId,
            name = name,
            url = url,
            pokemonImageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/home/${pokemonId}.png"
        )
    }
}
