package com.pokeapi.poke_api.domain;

import java.util.Map;

public record PokemonEnrichment(int pokemonId, Map<String, Object> attributes) {

    public static PokemonEnrichment empty(int pokemonId) {
        return new PokemonEnrichment(pokemonId, Map.of());
    }
}
