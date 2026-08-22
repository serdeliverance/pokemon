package com.pokeapi.poke_api.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.pokeapi.poke_api.application.port.out.PokemonProvider;
import com.pokeapi.poke_api.application.port.out.PokemonRepository;
import com.pokeapi.poke_api.domain.Pokemon;
import com.pokeapi.poke_api.domain.PokemonIdPage;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class ListPokemonUseCaseServiceTest {

    private final PokemonProvider pokemonProvider = mock(PokemonProvider.class);
    private final PokemonRepository pokemonRepository = mock(PokemonRepository.class);
    private final ListPokemonUseCaseService subject = new ListPokemonUseCaseService(pokemonProvider, pokemonRepository);

    private static final Pokemon BULBASAUR =
            new Pokemon(1, "bulbasaur", List.of("grass"), List.of("overgrow"), List.of("sprite1"));
    private static final Pokemon IVYSAUR =
            new Pokemon(2, "ivysaur", List.of("grass"), List.of("overgrow"), List.of("sprite2"));

    @Test
    void shouldServeFromRepositoryWithoutCallingProviderWhenAllCached() {
        when(pokemonProvider.getPokemonIdPage(0, 2)).thenReturn(new PokemonIdPage(List.of(1, 2), 1302));
        when(pokemonRepository.findSummariesByIds(List.of(1, 2))).thenReturn(Map.of(1, BULBASAUR, 2, IVYSAUR));

        var result = subject.listPokemons(0, 2);

        assertThat(result.pokemons()).containsExactly(BULBASAUR, IVYSAUR);
        assertThat(result.total()).isEqualTo(1302);
        verify(pokemonProvider, never()).getPokemonById(org.mockito.ArgumentMatchers.anyInt());
    }

    @Test
    void shouldFetchOnlyMissingIdsFromProviderAndPersistThem() {
        when(pokemonProvider.getPokemonIdPage(0, 2)).thenReturn(new PokemonIdPage(List.of(1, 2), 1302));
        when(pokemonRepository.findSummariesByIds(List.of(1, 2))).thenReturn(Map.of(1, BULBASAUR));
        when(pokemonProvider.getPokemonById(2)).thenReturn(Optional.of(IVYSAUR));

        var result = subject.listPokemons(0, 2);

        assertThat(result.pokemons()).containsExactly(BULBASAUR, IVYSAUR);
        verify(pokemonProvider, never()).getPokemonById(1);
        verify(pokemonRepository).saveSummaries(List.of(IVYSAUR));
    }

    @Test
    void shouldPreserveProviderOrderRegardlessOfCacheHits() {
        when(pokemonProvider.getPokemonIdPage(0, 2)).thenReturn(new PokemonIdPage(List.of(2, 1), 1302));
        when(pokemonRepository.findSummariesByIds(List.of(2, 1))).thenReturn(Map.of(1, BULBASAUR, 2, IVYSAUR));

        var result = subject.listPokemons(0, 2);

        assertThat(result.pokemons()).containsExactly(IVYSAUR, BULBASAUR);
    }
}
