package com.pokeapi.poke_api.adapter.in.web;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.pokeapi.poke_api.application.port.in.GetPokemonDetailUseCase;
import com.pokeapi.poke_api.application.port.in.ListPokemonUseCase;
import com.pokeapi.poke_api.support.PokemonFixtures;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
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
    private GetPokemonDetailUseCase getPokemonDetailUseCase;

    @MockitoBean
    private PokemonMapper pokemonMapper;

    @Test
    void shouldReturnPaginatedPokemons() throws Exception {
        when(listPokemonUseCase.listPokemons(2, 10)).thenReturn(PokemonFixtures.pokemonList());
        when(pokemonMapper.mapToPokemonSummaryDto(any())).thenReturn(
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

        mockMvc.perform(get("/pokemons?page=2&size=10")).andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].name").value("bulbasaur"))
                .andExpect(jsonPath("$[0].category", containsInAnyOrder("poison", "grass")))
                .andExpect(jsonPath("$[1].name").value("charmander"))
                .andExpect(jsonPath("$[*].name", containsInAnyOrder("bulbasaur", "squirtle", "charmander")));
    }

    @Test
    void shouldReturnEmptyListWhenNoPokemonExist() throws Exception {
        when(listPokemonUseCase.listPokemons(2, 10)).thenReturn(List.of());

        mockMvc.perform(get("/pokemons?page=2&size=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void shouldReturn400InvalidPage() throws Exception {
        mockMvc.perform(get("/pokemons?page=-2&size=10")).andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn400InvalidSize() throws Exception {
        mockMvc.perform(get("/pokemons?page=2&size=-10")).andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn500WhenErrorDownstream() {
        // TODO implement (this would lead to the implementation of ControllerAdvice)
    }

    @Test
    void shouldReturnPokemonDetail() throws Exception {
        when(getPokemonDetailUseCase.getPokemonDetail(1)).thenReturn(Optional.of(PokemonFixtures.pokemonDetail()));
        when(pokemonMapper.mapToPokemonDetailDto(any()))
                .thenReturn(new PokemonDetailDto(
                        1,
                        "bulbasaur",
                        List.of("grass", "poison"),
                        List.of("swords-dance", "razor-wind"),
                        List.of(
                                "https://someurl/sprites/pokemon/back/1.png",
                                "https://someurl/sprites/pokemon/front/2.png"),
                        List.of(new PokemonStatDto("hp", 45)),
                        "A strange seed was planted on its back at birth.",
                        List.of(new EvolutionStageDto(1, "bulbasaur", 1), new EvolutionStageDto(2, "ivysaur", 2))));

        mockMvc.perform(get("/pokemons/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("bulbasaur"))
                .andExpect(jsonPath("$.stats[0].name").value("hp"))
                .andExpect(jsonPath("$.description").value("A strange seed was planted on its back at birth."))
                .andExpect(jsonPath("$.evolutionChain", hasSize(2)))
                .andExpect(jsonPath("$.evolutionChain[1].name").value("ivysaur"));
    }

    @Test
    void shouldReturn404WhenPokemonNotFound() throws Exception {
        when(getPokemonDetailUseCase.getPokemonDetail(9999)).thenReturn(Optional.empty());

        mockMvc.perform(get("/pokemons/9999")).andExpect(status().isNotFound());
    }

    @Test
    void shouldReturn400WhenIdIsInvalid() throws Exception {
        mockMvc.perform(get("/pokemons/0")).andExpect(status().isBadRequest());
    }
}
