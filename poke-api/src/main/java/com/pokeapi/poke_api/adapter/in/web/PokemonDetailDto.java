package com.pokeapi.poke_api.adapter.in.web;

import java.util.List;
import java.util.Map;

public record PokemonDetailDto(
        int id,
        String name,
        List<String> category,
        List<String> skills,
        List<String> sprites,
        List<PokemonStatDto> stats,
        String description,
        List<EvolutionStageDto> evolutionChain,
        Map<String, Object> enrichment) {}
