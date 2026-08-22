package com.pokeapi.poke_api.adapter.in.web;

import java.util.List;

public record PokemonPaginatedResponseDto(List<PokemonSummaryDto> pokemons, int total, int page, int size) {}
