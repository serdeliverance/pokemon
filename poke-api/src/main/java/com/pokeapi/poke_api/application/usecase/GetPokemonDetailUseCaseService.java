package com.pokeapi.poke_api.application.usecase;

import com.pokeapi.poke_api.application.port.in.GetPokemonDetailUseCase;
import com.pokeapi.poke_api.application.port.out.PokemonProvider;
import com.pokeapi.poke_api.domain.PokemonDetail;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetPokemonDetailUseCaseService implements GetPokemonDetailUseCase {

    private final PokemonProvider pokemonProvider;

    @Override
    public Optional<PokemonDetail> getPokemonDetail(int id) {
        return pokemonProvider.getPokemonDetailById(id);
    }
}
