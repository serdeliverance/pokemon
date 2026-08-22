package com.pokeapi.poke_api.domain;

import java.util.List;

public record PokemonPage(List<Pokemon> pokemons, int total, int page, int size) {}
