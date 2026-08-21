package com.pokeapi.poke_api.application.port.out;

import com.pokeapi.poke_api.domain.Pokemon;
import java.util.List;
import java.util.Optional;

public interface PokemonProvider {
    List<Pokemon> getPokemonPage(int page, int offset);

    Optional<Pokemon> getPokemonById(int id);
}
