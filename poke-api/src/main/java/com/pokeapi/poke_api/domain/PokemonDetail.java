package com.pokeapi.poke_api.domain;

import java.util.List;

public record PokemonDetail(
        int id,
        String name,
        List<String> category,
        List<String> skills,
        List<String> sprites,
        List<PokemonStat> stats,
        String description,
        List<EvolutionStage> evolutionChain) {}
