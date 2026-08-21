package com.pokeapi.poke_api.application.port.in;

import com.pokeapi.poke_api.domain.PokemonDetail;
import java.util.Optional;

public interface GetPokemonDetailUseCase {
    Optional<PokemonDetail> getPokemonDetail(int id);
}
