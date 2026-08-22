package com.pokeapi.poke_api.application.usecase;

import com.pokeapi.poke_api.application.port.in.ListPokemonUseCase;
import com.pokeapi.poke_api.application.port.out.PokemonProvider;
import com.pokeapi.poke_api.application.port.out.PokemonRepository;
import com.pokeapi.poke_api.domain.Pokemon;
import com.pokeapi.poke_api.domain.PokemonIdPage;
import com.pokeapi.poke_api.domain.PokemonPage;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ListPokemonUseCaseService implements ListPokemonUseCase {

    private final PokemonProvider pokemonProvider;
    private final PokemonRepository pokemonRepository;

    @Override
    public PokemonPage listPokemons(int page, int size) {
        PokemonIdPage idPage = pokemonProvider.getPokemonIdPage(page, size);

        Map<Integer, Pokemon> cached = new HashMap<>(pokemonRepository.findSummariesByIds(idPage.ids()));

        List<Integer> missingIds =
                idPage.ids().stream().filter(id -> !cached.containsKey(id)).toList();

        if (!missingIds.isEmpty()) {
            List<Pokemon> fetched = missingIds.stream()
                    .map(pokemonProvider::getPokemonById)
                    .flatMap(Optional::stream)
                    .toList();
            pokemonRepository.saveSummaries(fetched);
            fetched.forEach(pokemon -> cached.put(pokemon.id(), pokemon));
        }

        List<Pokemon> pokemons =
                idPage.ids().stream().map(cached::get).filter(Objects::nonNull).toList();

        return new PokemonPage(pokemons, idPage.total(), page, size);
    }
}
