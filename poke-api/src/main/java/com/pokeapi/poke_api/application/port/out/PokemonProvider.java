package com.pokeapi.poke_api.application.port.out;

import com.pokeapi.poke_api.domain.Pokemon;
import com.pokeapi.poke_api.domain.PokemonDetail;
import com.pokeapi.poke_api.domain.PokemonIdPage;
import java.util.Optional;

public interface PokemonProvider {
    PokemonIdPage getPokemonIdPage(int page, int size);

    Optional<Pokemon> getPokemonById(int id);

    Optional<PokemonDetail> getPokemonDetailById(int id);
}
