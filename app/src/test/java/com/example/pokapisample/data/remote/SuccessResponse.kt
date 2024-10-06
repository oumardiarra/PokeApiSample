package com.example.pokapisample.data.remote

val validPokemonListResponse = """
    {
       "count":1302,
       "next":"https://pokeapi.co/api/v2/pokemon?offset=20&limit=20",
       "previous":null,
       "results":[
          {
             "name":"bulbasaur",
             "url":"https://pokeapi.co/api/v2/pokemon/1/"
          },
          {
             "name":"ivysaur",
             "url":"https://pokeapi.co/api/v2/pokemon/2/"
          },
          {
             "name":"venusaur",
             "url":"https://pokeapi.co/api/v2/pokemon/3/"
          }
       ]
    }
""".trimIndent()

val nextPageValidPokemonListResponse = """
   {
       "count":1302,
       "next":"https://pokeapi.co/api/v2/pokemon?offset=40&limit=20",
       "previous":null,
       "results":[
          {
             "name":"charmander",
             "url":"https://pokeapi.co/api/v2/pokemon/4/"
          }
       ]
    }
""".trimIndent()

val validFightingPokemonListResponse = """
    {
       "id":2,
       "name":"fighting",
       "pokemon":[
          {
             "pokemon":{
                "name":"mankey",
                "url":"https://pokeapi.co/api/v2/pokemon/56/"
             },
             "slot":1
          },
          {
             "pokemon":{
                "name":"primeape",
                "url":"https://pokeapi.co/api/v2/pokemon/57/"
             },
             "slot":1
          }
       ]
    }
""".trimIndent()