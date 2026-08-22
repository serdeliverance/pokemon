package com.pokeapi.poke_api.application.usecase;

import com.pokeapi.poke_api.application.port.in.ListPokemonUseCase;
import com.pokeapi.poke_api.application.port.out.PokemonProvider;
import com.pokeapi.poke_api.domain.PokemonPage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ListPokemonUseCaseService implements ListPokemonUseCase {

    private final PokemonProvider pokemonProvider;

    @Override
    public PokemonPage listPokemons(int page, int size) {
        return pokemonProvider.getPokemonPage(page, size);
    }
}
