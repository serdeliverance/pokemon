package com.pokeapi.poke_api.application.usecase;

import com.pokeapi.poke_api.application.port.in.ListPokemonUseCase;
import com.pokeapi.poke_api.application.port.out.PokemonProvider;
import com.pokeapi.poke_api.domain.Pokemon;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ListPokemonUseCaseService implements ListPokemonUseCase {

    private final PokemonProvider pokemonProvider;

    @Override
    public List<Pokemon> listPokemons(int page, int size) {
        return pokemonProvider.getPokemonPage(page, size);
    }
}
