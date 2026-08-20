package com.pokeapi.poke_api.application.usecase;

import com.pokeapi.poke_api.application.port.in.ListPokemonUseCase;
import com.pokeapi.poke_api.domain.Pokemon;
import java.util.List;

public class ListPokemonUseCaseService implements ListPokemonUseCase {
    @Override
    public List<Pokemon> listPokemons(int page, int size) {
        return List.of();
    }
}
