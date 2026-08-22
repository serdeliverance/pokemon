package com.pokeapi.poke_api.application.port.out;

import com.pokeapi.poke_api.domain.PokemonDetail;
import com.pokeapi.poke_api.domain.PokemonPage;
import java.util.Optional;

public interface PokemonProvider {
    PokemonPage getPokemonPage(int page, int size);

    Optional<PokemonDetail> getPokemonDetailById(int id);
}
