package com.pokeapi.poke_api.adapter.in.web;

import com.pokeapi.poke_api.domain.Pokemon;
import com.pokeapi.poke_api.domain.PokemonDetail;
import org.springframework.stereotype.Component;

@Component
public class PokemonMapper {
    public PokemonSummaryDto mapToPokemonSummaryDto(Pokemon pokemon) {
        return new PokemonSummaryDto(
                pokemon.id(), pokemon.name(), pokemon.category(), pokemon.skills(), pokemon.sprites());
    }

    public PokemonDetailDto mapToPokemonDetailDto(PokemonDetail pokemon) {
        return new PokemonDetailDto(
                pokemon.id(),
                pokemon.name(),
                pokemon.category(),
                pokemon.skills(),
                pokemon.sprites(),
                pokemon.stats().stream()
                        .map(stat -> new PokemonStatDto(stat.name(), stat.baseStat()))
                        .toList(),
                pokemon.description(),
                pokemon.evolutionChain().stream()
                        .map(stage -> new EvolutionStageDto(stage.id(), stage.name(), stage.stage()))
                        .toList());
    }
}
