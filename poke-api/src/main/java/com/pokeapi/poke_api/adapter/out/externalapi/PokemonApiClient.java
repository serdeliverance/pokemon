package com.pokeapi.poke_api.adapter.out.externalapi;

import com.pokeapi.poke_api.application.port.out.PokemonProvider;
import com.pokeapi.poke_api.domain.Pokemon;
import java.util.List;
import java.util.Optional;

public class PokemonApiClient implements PokemonProvider {
    @Override
    public List<Pokemon> getPokemonPage(int page, int offset) {
        return List.of();
    }

    @Override
    public Optional<Pokemon> getPokemonById(int id) {
        return Optional.empty();
    }
}
