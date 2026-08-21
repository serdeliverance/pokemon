package com.pokeapi.poke_api.adapter.in.web;

import com.pokeapi.poke_api.application.port.in.GetPokemonDetailUseCase;
import com.pokeapi.poke_api.application.port.in.ListPokemonUseCase;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/pokemons")
@RequiredArgsConstructor
public class PokemonController {

    private final ListPokemonUseCase listPokemonUseCase;
    private final GetPokemonDetailUseCase getPokemonDetailUseCase;
    private final PokemonMapper pokemonMapper;

    @GetMapping
    public ResponseEntity<List<PokemonSummaryDto>> getPaginated(int page, int size) {
        // TODO validate page and size input params
        var pokemonList = listPokemonUseCase.listPokemons(page, size).stream()
                .map(pokemonMapper::mapToPokemonSummaryDto)
                .sorted(Comparator.comparingInt(PokemonSummaryDto::id))
                .toList();
        return ResponseEntity.ok(pokemonList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PokemonDetailDto> getById(@PathVariable int id) {
        if (id <= 0) {
            return ResponseEntity.badRequest().build();
        }
        return getPokemonDetailUseCase
                .getPokemonDetail(id)
                .map(pokemonMapper::mapToPokemonDetailDto)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
