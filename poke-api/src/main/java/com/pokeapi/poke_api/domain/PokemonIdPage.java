package com.pokeapi.poke_api.domain;

import java.util.List;

public record PokemonIdPage(List<Integer> ids, int total) {}
