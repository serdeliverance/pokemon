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
        List<EvolutionStage> evolutionChain,
        PokemonEnrichment enrichment) {

    public PokemonDetail withEnrichment(PokemonEnrichment enrichment) {
        return new PokemonDetail(id, name, category, skills, sprites, stats, description, evolutionChain, enrichment);
    }
}
