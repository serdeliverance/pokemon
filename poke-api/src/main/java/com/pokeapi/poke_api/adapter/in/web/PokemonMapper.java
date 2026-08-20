package com.pokeapi.poke_api.adapter.in.web;

import com.pokeapi.poke_api.domain.Pokemon;
import org.springframework.stereotype.Component;

@Component
public class PokemonMapper {
    public PokemonSummaryDto mapToPokemonSummaryDto(Pokemon pokemon) {
        return new PokemonSummaryDto(pokemon.id(), pokemon.name(), pokemon.category(), pokemon.skills(), pokemon.sprites());
    }
}
