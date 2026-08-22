package com.pokeapi.poke_api.support;

import com.pokeapi.poke_api.domain.EvolutionStage;
import com.pokeapi.poke_api.domain.Pokemon;
import com.pokeapi.poke_api.domain.PokemonDetail;
import com.pokeapi.poke_api.domain.PokemonEnrichment;
import com.pokeapi.poke_api.domain.PokemonPage;
import com.pokeapi.poke_api.domain.PokemonStat;
import java.util.List;
import java.util.Map;

public class PokemonFixtures {

    public static List<Pokemon> pokemonList() {
        return List.of(
                new Pokemon(
                        1,
                        "bulbasaur",
                        List.of("grass", "poison"),
                        List.of("swords-dance", "razor-wind"),
                        List.of(
                                "https://someurl/sprites/pokemon/back/1.png",
                                "https://someurl/sprites/pokemon/front/2.png")),
                new Pokemon(
                        4,
                        "charmander",
                        List.of("fire"),
                        List.of("fire-punch"),
                        List.of(
                                "https://someurl/sprites/pokemon/back/1.png",
                                "https://someurl/sprites/pokemon/front/2.png")),
                new Pokemon(
                        1,
                        "squirtle",
                        List.of("water"),
                        List.of("bubble", "aqua-tail"),
                        List.of(
                                "https://someurl/sprites/pokemon/back/1.png",
                                "https://someurl/sprites/pokemon/front/2.png")));
    }

    public static PokemonPage pokemonPage() {
        return new PokemonPage(pokemonList(), 1302, 2, 10);
    }

    public static Pokemon pokemon() {
        return new Pokemon(
                1,
                "bulbasaur",
                List.of("grass", "poison"),
                List.of("swords-dance", "razor-wind"),
                List.of("https://someurl/sprites/pokemon/back/1.png", "https://someurl/sprites/pokemon/front/2.png"));
    }

    public static PokemonDetail pokemonDetail() {
        return new PokemonDetail(
                1,
                "bulbasaur",
                List.of("grass", "poison"),
                List.of("swords-dance", "razor-wind"),
                List.of("https://someurl/sprites/pokemon/back/1.png", "https://someurl/sprites/pokemon/front/2.png"),
                List.of(new PokemonStat("hp", 45), new PokemonStat("attack", 49), new PokemonStat("defense", 49)),
                "A strange seed was planted on its back at birth.",
                List.of(
                        new EvolutionStage(1, "bulbasaur", 1),
                        new EvolutionStage(2, "ivysaur", 2),
                        new EvolutionStage(3, "venusaur", 3)),
                PokemonEnrichment.empty(1));
    }

    public static PokemonEnrichment pokemonEnrichment() {
        return new PokemonEnrichment(1, Map.of("nameEs", "Bulbasaur", "region", "Kanto"));
    }
}
