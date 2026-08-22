package com.pokeapi.poke_api.application.port.in;

import com.pokeapi.poke_api.domain.PokemonPage;

public interface ListPokemonUseCase {
    PokemonPage listPokemons(int page, int size);
}
