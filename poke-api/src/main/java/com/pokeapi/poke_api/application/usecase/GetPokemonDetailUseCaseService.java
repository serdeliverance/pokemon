package com.pokeapi.poke_api.application.usecase;

import com.pokeapi.poke_api.application.port.in.GetPokemonDetailUseCase;
import com.pokeapi.poke_api.application.port.out.PokemonProvider;
import com.pokeapi.poke_api.application.port.out.PokemonRepository;
import com.pokeapi.poke_api.domain.PokemonDetail;
import com.pokeapi.poke_api.domain.PokemonEnrichment;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetPokemonDetailUseCaseService implements GetPokemonDetailUseCase {

    private final PokemonProvider pokemonProvider;
    private final PokemonRepository pokemonRepository;

    @Override
    public Optional<PokemonDetail> getPokemonDetail(int id) {
        Optional<PokemonDetail> cached = pokemonRepository.findDetailById(id);
        Optional<PokemonDetail> detail = cached.or(() -> fetchAndPersist(id));

        return detail.map(this::withEnrichment);
    }

    private Optional<PokemonDetail> fetchAndPersist(int id) {
        Optional<PokemonDetail> fetched = pokemonProvider.getPokemonDetailById(id);
        fetched.ifPresent(pokemonRepository::saveDetail);
        return fetched;
    }

    private PokemonDetail withEnrichment(PokemonDetail detail) {
        PokemonEnrichment enrichment =
                pokemonRepository.findEnrichmentById(detail.id()).orElseGet(() -> PokemonEnrichment.empty(detail.id()));
        return detail.withEnrichment(enrichment);
    }
}
