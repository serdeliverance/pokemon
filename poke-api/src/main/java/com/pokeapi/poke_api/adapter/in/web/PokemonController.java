package com.pokeapi.poke_api.adapter.in.web;

import com.pokeapi.poke_api.application.port.in.ListPokemonUseCase;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/pokemons")
@RequiredArgsConstructor
public class PokemonController {

    private final ListPokemonUseCase listPokemonUseCase;
    private final PokemonMapper pokemonMapper;

    @GetMapping
    public ResponseEntity<List<PokemonSummaryDto>> getPaginated(int page, int size) {
        var pokemonList = listPokemonUseCase.listPokemons(page, size).stream()
                .map(pokemonMapper::mapToPokemonSummaryDto)
                .toList();
        return ResponseEntity.ok(pokemonList);
    }
}
