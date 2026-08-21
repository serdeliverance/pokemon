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

    @Test
    void shouldMapToPokemonDetailDto() {
        var expected = new PokemonDetailDto(
                1,
                "bulbasaur",
                List.of("grass", "poison"),
                List.of("swords-dance", "razor-wind"),
                List.of("https://someurl/sprites/pokemon/back/1.png", "https://someurl/sprites/pokemon/front/2.png"),
                List.of(
                        new PokemonStatDto("hp", 45),
                        new PokemonStatDto("attack", 49),
                        new PokemonStatDto("defense", 49)),
                "A strange seed was planted on its back at birth.",
                List.of(
                        new EvolutionStageDto(1, "bulbasaur", 1),
                        new EvolutionStageDto(2, "ivysaur", 2),
                        new EvolutionStageDto(3, "venusaur", 3)));
        var result = subject.mapToPokemonDetailDto(PokemonFixtures.pokemonDetail());
        assertThat(result).isEqualTo(expected);
    }
}
