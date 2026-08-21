package com.pokeapi.poke_api.application.port.in;

import com.pokeapi.poke_api.domain.Pokemon;
import java.util.List;

public interface ListPokemonUseCase {
    List<Pokemon> listPokemons(int page, int size);
}
