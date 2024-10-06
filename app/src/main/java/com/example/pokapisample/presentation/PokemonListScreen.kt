package com.example.pokapisample.presentation

import SearchView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.example.pokapisample.domain.PokemonItem
import com.example.pokapisample.ui.theme.PokApiSampleTheme
import kotlinx.coroutines.flow.flowOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PokemonListScreen(
    modifier: Modifier = Modifier,
    viewModel: PokemonListViewModel = hiltViewModel(),
) {
    val searchText by viewModel.searchText.collectAsState()
    val pokemonTypesData = viewModel.uiState.collectAsLazyPagingItems()

    LaunchedEffect(Unit) {
        viewModel.getPokeList(null)
    }
    Scaffold(
        topBar = {
            TopAppBar(
                colors = topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                ),
                title = {
                    SearchView(
                        value = searchText,
                        onValueChange = viewModel::onSearchPokeTypeValueChange,
                        onSearch = { viewModel.searchPokeInType(searchText) },
                        onClearQuery = {
                            viewModel.resetSearchText()
                            viewModel.searchPokeInType(null)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = MaterialTheme.colorScheme.background,
                                shape = RoundedCornerShape(8.dp)
                            )
                    )
                }
            )
        },
    ) { innerPadding ->
        Column( verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {

            when (pokemonTypesData.loadState.refresh) {
                LoadState.Loading -> CircularProgressIndicator()
                is LoadState.Error -> Unit
                else -> PokemonListScreenContent(
                    pokemonListData = pokemonTypesData,
                    modifier = modifier
                )
            }
        }
    }
}


@Composable
private fun PokemonListScreenContent(
    pokemonListData: LazyPagingItems<PokemonItem>,
    modifier: Modifier = Modifier,
) {
    LazyVerticalGrid(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        columns = GridCells.Fixed(2)
    ) {
        items(pokemonListData.itemCount) { pokemonTypeIndex ->
            val pokemonType = pokemonListData[pokemonTypeIndex]
            pokemonType?.let {
                Column(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    Card(
                        modifier = Modifier.fillMaxSize(),
                        elevation = CardDefaults.cardElevation(10.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        ),
                    ) {
                        AsyncImage(
                            model = it.pokemonImageUrl,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = it.name,
                            textAlign = TextAlign.Companion.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }

        item {
            if (pokemonListData.loadState.append is LoadState.Loading) {
                CircularProgressIndicator()
            }
        }
    }
}

@Preview
@Composable
fun PokemonTypeScreenContentPreview() {
    PokApiSampleTheme {
        val pokemonItemList = listOf(
            PokemonItem(
                id = 1,
                name = "bulbasaur",
                url = "https://pokeapi.co/api/v2/pokemon/1/",
                pokemonImageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/home/1.png"
            ),
            PokemonItem(
                id = 2,
                name = "ivysaur",
                url = "https://pokeapi.co/api/v2/pokemon/2/",
                pokemonImageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/home/2.png"
            ),
            PokemonItem(
                id = 3,
                name = "venusaur",
                url = "https://pokeapi.co/api/v2/pokemon/3/",
                pokemonImageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/home/3.png"
            ),
        )
        val pagingData = PagingData.from(
            data = pokemonItemList
        )
        val lazyPagingPokemonItems = flowOf(pagingData).collectAsLazyPagingItems()
        PokemonListScreenContent(
            pokemonListData = lazyPagingPokemonItems
        )
    }
}