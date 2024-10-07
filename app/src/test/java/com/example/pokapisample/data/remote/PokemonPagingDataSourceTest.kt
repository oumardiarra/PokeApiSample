package com.example.pokapisample.data.remote

import androidx.paging.PagingSource
import com.example.pokapisample.data.mapper.PokemonMapper.toPokemonItem
import com.example.pokapisample.data.remote.api.PokeApiService
import com.example.pokapisample.domain.PokemonItem
import com.example.pokapisample.data.remote.dto.PokemonDataDto
import com.example.pokapisample.data.remote.dto.PokemonEntryDto
import com.example.pokapisample.data.remote.dto.PokemonItemDto
import com.example.pokapisample.data.remote.dto.PokemonListDataDto
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.assertj.core.api.AssertionsForClassTypes.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class PokemonPagingDataSourceTest{
    private lateinit var apiService: PokeApiService
    private lateinit var mockWebServer: MockWebServer
    private lateinit var pokeDataSource: PokemonPagingDataSource

    @Before
    fun setUp() {
        mockWebServer = MockWebServer()
        apiService = mockk(relaxed = true)
        pokeDataSource = PokemonPagingDataSource(
            searchText = null,
            api = apiService
        )
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `load - when load returns pokemon list - should return Data`() = runTest {
        // Given
        val mockedPokeItemsDto = listOf(
            PokemonItemDto(name = "bulbasaur", url = "https://pokeapi.co/api/v2/pokemon/1/"),
            PokemonItemDto(name = "ivysaur", url = "https://pokeapi.co/api/v2/pokemon/2/"),
            PokemonItemDto(name = "venusaur", url = "https://pokeapi.co/api/v2/pokemon/3/"),
        )
        val pokeEntryDtoResponse = PokemonEntryDto(
            count = 1302,
            nextPage = "https://pokeapi.co/api/v2/pokemon?offset=20&limit=20",
            previousPage = null,
            pokemonTypeList = mockedPokeItemsDto
        )
        coEvery { apiService.getPokemonList(any()) }.returns(pokeEntryDtoResponse)
        // When

        val result = pokeDataSource.load(
            PagingSource.LoadParams.Refresh(
                key = 0,
                loadSize = 1,
                placeholdersEnabled = false
            )
        )

        // Then
        assertThat(result).isEqualTo(
            PagingSource.LoadResult.Page(
                data = pokeEntryDtoResponse.pokemonTypeList.map { it.toPokemonItem() },
                prevKey = null,
                nextKey = 1
            )
        )
    }

    @Test
    fun `load - when is append success - should return Data`() = runTest {
        // Given
        val mockedPokeItemsDto = listOf(
            PokemonItemDto(name = "charmander", url = "https://pokeapi.co/api/v2/pokemon/4/"),
        )
        val pokeEntryDtoResponse = PokemonEntryDto(
            count = 1302,
            nextPage = "https://pokeapi.co/api/v2/pokemon?offset=60&limit=20",
            previousPage = "https://pokeapi.co/api/v2/pokemon?offset=40&limit=20",
            pokemonTypeList = mockedPokeItemsDto
        )
        coEvery { apiService.getPokemonList(any()) }.returns(pokeEntryDtoResponse)
        // When
        val result = pokeDataSource.load(
            PagingSource.LoadParams.Append(
                key = 1,
                loadSize = 1,
                placeholdersEnabled = false
            )
        )

        // Then
        assertThat(result).isEqualTo(
            PagingSource.LoadResult.Page(
                data = pokeEntryDtoResponse.pokemonTypeList.map { it.toPokemonItem() },
                prevKey = null,
                nextKey = 2
            )
        )
    }

    @Test
    fun `load - when load returns pokemon list belonging to type - should return Data`() = runTest {
        // Given
        pokeDataSource = PokemonPagingDataSource(
            searchText = "fighting",
            api = apiService
        )
        val mockedPokeDataDto = listOf(
            PokemonDataDto(
                pokemon = PokemonItemDto(
                    name = "mankey",
                    url = "https://pokeapi.co/api/v2/pokemon/56/"
                )
            ),
            PokemonDataDto(
                PokemonItemDto(
                    name = "primeape",
                    url = "https://pokeapi.co/api/v2/pokemon/57/"
                )
            ),
        )
        val pokeListDataDtoResponse = PokemonListDataDto(
            pokemonList = mockedPokeDataDto
        )
        coEvery { apiService.getPokemonTypes("fighting",any()) }.returns(pokeListDataDtoResponse)
        // When
        val result = pokeDataSource.load(
            PagingSource.LoadParams.Refresh(
                key = 0,
                loadSize = 1,
                placeholdersEnabled = false
            )
        )

        // Then
        assertThat(result).isEqualTo(
            PagingSource.LoadResult.Page(
                data = pokeListDataDtoResponse.pokemonList.map { it.pokemon.toPokemonItem() },
                prevKey = null,
                nextKey = null
            )
        )
    }



    @Test
    fun `load - when service returns exception - should return Error`() = runTest {
        // Given
        val mockedError =  mockk<HttpException>().apply {
            every { code() } returns 500
            every { message() } returns "Server Error"
        }

        coEvery { apiService.getPokemonList(any()) }.throws(mockedError)

        // When
        val result = pokeDataSource.load(
            PagingSource.LoadParams.Refresh(
                key = 0,
                loadSize = 1,
                placeholdersEnabled = false
            )
        )

        // Then
        assertThat(result).isEqualTo(PagingSource.LoadResult.Error<Int, PokemonItem>(mockedError))
    }

    @Test
    fun `load - when server returns valid json pokemon list data - should return transformed data`() = runTest {
        // Given
        pokeDataSource = PokemonPagingDataSource(
            searchText = null,
            api = getApiService()
        )
        val mockedPokeItemsDto = listOf(
            PokemonItemDto(name = "bulbasaur", url = "https://pokeapi.co/api/v2/pokemon/1/"),
            PokemonItemDto(name = "ivysaur", url = "https://pokeapi.co/api/v2/pokemon/2/"),
            PokemonItemDto(name = "venusaur", url = "https://pokeapi.co/api/v2/pokemon/3/"),
        )
        val pokeEntryDtoResponse = PokemonEntryDto(
            count = 1302,
            nextPage = "https://pokeapi.co/api/v2/pokemon?offset=20&limit=20",
            previousPage = null,
            pokemonTypeList = mockedPokeItemsDto
        )
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(validPokemonListResponse)
        )
        // When

        val result = pokeDataSource.load(
            PagingSource.LoadParams.Refresh(
                key = 0,
                loadSize = 1,
                placeholdersEnabled = false
            )
        )

        // Then
        assertThat(result).isEqualTo(
            PagingSource.LoadResult.Page(
                data = pokeEntryDtoResponse.pokemonTypeList.map { it.toPokemonItem() },
                prevKey = null,
                nextKey = 1
            )
        )
    }

    @Test
    fun `load - when server returns valid json pokemon list for fighting type - should return transformed data`() = runTest {
        // Given
        pokeDataSource = PokemonPagingDataSource(
            searchText = "fighting",
            api = getApiService()
        )
        val mockedPokeDataDto = listOf(
            PokemonDataDto(
                pokemon = PokemonItemDto(
                    name = "mankey",
                    url = "https://pokeapi.co/api/v2/pokemon/56/"
                )
            ),
            PokemonDataDto(
                PokemonItemDto(
                    name = "primeape",
                    url = "https://pokeapi.co/api/v2/pokemon/57/"
                )
            ),
        )
        val pokeListDataDtoResponse = PokemonListDataDto(
            pokemonList = mockedPokeDataDto
        )

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(validFightingPokemonListResponse)
        )
        // When
        val result = pokeDataSource.load(
            PagingSource.LoadParams.Refresh(
                key = 0,
                loadSize = 1,
                placeholdersEnabled = false
            )
        )

        // Then
        assertThat(result).isEqualTo(
            PagingSource.LoadResult.Page(
                data = pokeListDataDtoResponse.pokemonList.map { it.pokemon.toPokemonItem() },
                prevKey = null,
                nextKey = null
            )
        )
    }

    private fun getApiService() = Retrofit.Builder()
        .baseUrl(mockWebServer.url("/"))
        .addConverterFactory(
            MoshiConverterFactory.create(
                Moshi.Builder().addLast(
                    KotlinJsonAdapterFactory()
                ).build()
            )
        )
        .build()
        .create(PokeApiService::class.java)
}