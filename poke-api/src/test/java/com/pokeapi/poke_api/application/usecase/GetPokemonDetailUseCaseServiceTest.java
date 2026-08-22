package com.pokeapi.poke_api.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.pokeapi.poke_api.application.port.out.PokemonProvider;
import com.pokeapi.poke_api.support.PokemonFixtures;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class GetPokemonDetailUseCaseServiceTest {

    private final PokemonProvider pokemonProvider = mock(PokemonProvider.class);
    private final GetPokemonDetailUseCaseService subject = new GetPokemonDetailUseCaseService(pokemonProvider);

    @Test
    void shouldReturnPokemonDetailWhenFound() {
        when(pokemonProvider.getPokemonDetailById(1)).thenReturn(Optional.of(PokemonFixtures.pokemonDetail()));

        var result = subject.getPokemonDetail(1);

        assertThat(result).contains(PokemonFixtures.pokemonDetail());
    }

    @Test
    void shouldReturnEmptyWhenPokemonNotFound() {
        when(pokemonProvider.getPokemonDetailById(9999)).thenReturn(Optional.empty());

        var result = subject.getPokemonDetail(9999);

        assertThat(result).isEmpty();
    }
}
