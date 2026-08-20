package com.pokeapi.poke_api.adapter.in.web;

import com.pokeapi.poke_api.application.port.in.ListPokemonUseCase;
import com.pokeapi.poke_api.support.PokemonFixtures;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PokemonController.class)
class PokemonControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ListPokemonUseCase listPokemonUseCase;

    @MockitoBean
    private PokemonMapper pokemonMapper;

    @Test
    void shouldReturnPaginatedPokemons() throws Exception {
        when(listPokemonUseCase.listPokemons(2, 10)).thenReturn(PokemonFixtures.pokemonList());

        var expected = List.of(
                new PokemonSummaryDto(
                        1,
                        "bulbasaur",
                        List.of("grass", "poison"),
                        List.of("swords-dance", "razor-wind"),
                        List.of("https://someurl/sprites/pokemon/back/1.png", "https://someurl/sprites/pokemon/front/2.png")),
                new PokemonSummaryDto(
                        4,
                        "charmander",
                        List.of("fire"),
                        List.of("fire-punch"),
                        List.of("https://someurl/sprites/pokemon/back/1.png", "https://someurl/sprites/pokemon/front/2.png")),
                new PokemonSummaryDto(
                        1,
                        "squirtle",
                        List.of("water"),
                        List.of("bubble", "aqua-tail"),
                        List.of("https://someurl/sprites/pokemon/back/1.png", "https://someurl/sprites/pokemon/front/2.png"))
        );

        mockMvc.perform(get("/pokemons/page=2&size=10")).andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].name").value("bulbasaur"))
                .andExpect(jsonPath("$[0].type").value("Electric"))
                .andExpect(jsonPath("$[1].name").value("Charmander"))
                .andExpect(jsonPath("$[*].name", containsInAnyOrder("Pikachu", "Charmander")));
    }

    @Test
    void shouldReturnEmptyListWhenNoPokemonExist() {
        // TODO
    }

    @Test
    void shouldReturn500WhenErrorDownstream() {
        // TODO
    }
}
