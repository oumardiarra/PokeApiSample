package com.example.pokapisample.data.mapper

import com.example.pokapisample.data.mapper.PokemonMapper.toPokemonItem
import com.example.pokapisample.domain.PokemonItem
import com.example.pokapisample.data.remote.dto.PokemonItemDto
import org.assertj.core.api.AssertionsForClassTypes.assertThat
import org.junit.Test

class PokemonMapperTest {
    @Test
    fun `toPokemonItem - should map PokemonItemDto to PokemonItem`() {
        // Given
        val givenPokeItemDto = PokemonItemDto(
            name = "name",
            url = "https://pokeapi.co/api/v2/pokemon/10/"
        )

        // When
        val result = givenPokeItemDto.toPokemonItem()

        // Then
        assertThat(result).isEqualTo(
            PokemonItem(
                id = 10,
                name = "name",
                url = "https://pokeapi.co/api/v2/pokemon/10/",
                pokemonImageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/home/10.png"
            )
        )
    }
}