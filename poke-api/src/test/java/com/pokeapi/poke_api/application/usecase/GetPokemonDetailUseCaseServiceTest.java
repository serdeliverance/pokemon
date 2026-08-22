package com.pokeapi.poke_api.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.pokeapi.poke_api.application.port.out.PokemonProvider;
import com.pokeapi.poke_api.application.port.out.PokemonRepository;
import com.pokeapi.poke_api.domain.PokemonEnrichment;
import com.pokeapi.poke_api.support.PokemonFixtures;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class GetPokemonDetailUseCaseServiceTest {

    private final PokemonProvider pokemonProvider = mock(PokemonProvider.class);
    private final PokemonRepository pokemonRepository = mock(PokemonRepository.class);
    private final GetPokemonDetailUseCaseService subject =
            new GetPokemonDetailUseCaseService(pokemonProvider, pokemonRepository);

    @Test
    void shouldServeFromRepositoryWithoutCallingProviderOnCacheHit() {
        var cached = PokemonFixtures.pokemonDetail();
        when(pokemonRepository.findDetailById(1)).thenReturn(Optional.of(cached));
        when(pokemonRepository.findEnrichmentById(1)).thenReturn(Optional.empty());

        var result = subject.getPokemonDetail(1);

        assertThat(result).contains(cached);
        verify(pokemonProvider, never()).getPokemonDetailById(1);
    }

    @Test
    void shouldFetchFromProviderAndPersistOnCacheMiss() {
        var fetched = PokemonFixtures.pokemonDetail();
        when(pokemonRepository.findDetailById(1)).thenReturn(Optional.empty());
        when(pokemonProvider.getPokemonDetailById(1)).thenReturn(Optional.of(fetched));
        when(pokemonRepository.findEnrichmentById(1)).thenReturn(Optional.empty());

        var result = subject.getPokemonDetail(1);

        assertThat(result).contains(fetched);
        verify(pokemonRepository).saveDetail(fetched);
    }

    @Test
    void shouldMergeEnrichmentIntoDetail() {
        var cached = PokemonFixtures.pokemonDetail();
        var enrichment = PokemonFixtures.pokemonEnrichment();
        when(pokemonRepository.findDetailById(1)).thenReturn(Optional.of(cached));
        when(pokemonRepository.findEnrichmentById(1)).thenReturn(Optional.of(enrichment));

        var result = subject.getPokemonDetail(1);

        assertThat(result).contains(cached.withEnrichment(enrichment));
    }

    @Test
    void shouldDefaultToEmptyEnrichmentWhenNoneStored() {
        var cached = PokemonFixtures.pokemonDetail();
        when(pokemonRepository.findDetailById(1)).thenReturn(Optional.of(cached));
        when(pokemonRepository.findEnrichmentById(1)).thenReturn(Optional.empty());

        var result = subject.getPokemonDetail(1);

        assertThat(result).contains(cached.withEnrichment(PokemonEnrichment.empty(1)));
    }

    @Test
    void shouldReturnEmptyWhenPokemonNotFoundAnywhere() {
        when(pokemonRepository.findDetailById(9999)).thenReturn(Optional.empty());
        when(pokemonProvider.getPokemonDetailById(9999)).thenReturn(Optional.empty());

        var result = subject.getPokemonDetail(9999);

        assertThat(result).isEmpty();
        verify(pokemonRepository, never()).saveDetail(org.mockito.ArgumentMatchers.any());
    }
}
