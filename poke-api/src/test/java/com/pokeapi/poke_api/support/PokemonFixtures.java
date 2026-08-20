package com.pokeapi.poke_api.support;

import com.pokeapi.poke_api.domain.Pokemon;
import java.util.List;

public class PokemonFixtures {

    public static List<Pokemon> pokemonList() {
        return List.of(
                new Pokemon(
                        1,
                        "bulbasaur",
                        List.of("grass", "poison"),
                        List.of("swords-dance", "razor-wind"),
                        List.of("https://someurl/sprites/pokemon/back/1.png", "https://someurl/sprites/pokemon/front/2.png")),
                new Pokemon(
                        4,
                        "charmander",
                        List.of("fire"),
                        List.of("fire-punch"),
                        List.of("https://someurl/sprites/pokemon/back/1.png", "https://someurl/sprites/pokemon/front/2.png")),
                new Pokemon(
                        1,
                        "squirtle",
                        List.of("water"),
                        List.of("bubble", "aqua-tail"),
                        List.of("https://someurl/sprites/pokemon/back/1.png", "https://someurl/sprites/pokemon/front/2.png"))
        );
    }

    public static Pokemon pokemon() {
        return new Pokemon(
                1,
                "bulbasaur",
                List.of("grass", "poison"),
                List.of("swords-dance", "razor-wind"),
                List.of("https://someurl/sprites/pokemon/back/1.png", "https://someurl/sprites/pokemon/front/2.png"));
    }
}
