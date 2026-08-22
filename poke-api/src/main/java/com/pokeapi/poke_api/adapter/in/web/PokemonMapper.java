package com.pokeapi.poke_api.adapter.in.web;

import com.pokeapi.poke_api.domain.Pokemon;
import com.pokeapi.poke_api.domain.PokemonDetail;
import com.pokeapi.poke_api.domain.PokemonPage;
import java.util.Comparator;
import org.springframework.stereotype.Component;

@Component
public class PokemonMapper {
    public PokemonSummaryDto mapToPokemonSummaryDto(Pokemon pokemon) {
        return new PokemonSummaryDto(
                pokemon.id(), pokemon.name(), pokemon.category(), pokemon.skills(), pokemon.sprites());
    }

    public PokemonPaginatedResponseDto mapToPokemonPaginatedResponseDto(PokemonPage pokemonPage) {
        var pokemons = pokemonPage.pokemons().stream()
                .map(this::mapToPokemonSummaryDto)
                .sorted(Comparator.comparingInt(PokemonSummaryDto::id))
                .toList();
        return new PokemonPaginatedResponseDto(pokemons, pokemonPage.total(), pokemonPage.page(), pokemonPage.size());
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
                        .toList(),
                pokemon.enrichment().attributes());
    }
}
