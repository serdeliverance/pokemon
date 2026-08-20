package com.pokeapi.poke_api.adapter.in.web;

import static org.assertj.core.api.Assertions.assertThat;

import com.pokeapi.poke_api.support.PokemonFixtures;
import java.util.List;
import org.junit.jupiter.api.Test;

class PokemonMapperTest {

    private final PokemonMapper subject = new PokemonMapper();

    @Test
    void shouldMapToPokemonSummaryDto() {
        var expected = new PokemonSummaryDto(
                1,
                "bulbasaur",
                List.of("grass", "poison"),
                List.of("swords-dance", "razor-wind"),
                List.of("https://someurl/sprites/pokemon/back/1.png", "https://someurl/sprites/pokemon/front/2.png"));
        var result = subject.mapToPokemonSummaryDto(PokemonFixtures.pokemon());
        assertThat(result).isEqualTo(expected);
    }
}
