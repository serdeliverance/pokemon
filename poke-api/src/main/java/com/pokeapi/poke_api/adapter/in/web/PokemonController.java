package com.pokeapi.poke_api.adapter.in.web;

import com.pokeapi.poke_api.application.port.in.GetPokemonDetailUseCase;
import com.pokeapi.poke_api.application.port.in.ListPokemonUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/pokemons")
@RequiredArgsConstructor
public class PokemonController {

    private final ListPokemonUseCase listPokemonUseCase;
    private final GetPokemonDetailUseCase getPokemonDetailUseCase;
    private final PokemonMapper pokemonMapper;

    @GetMapping
    public ResponseEntity<PokemonPaginatedResponseDto> getPaginated(
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
        // TODO validate page and size input params
        var pokemonPage = listPokemonUseCase.listPokemons(page, size);
        return ResponseEntity.ok(pokemonMapper.mapToPokemonPaginatedResponseDto(pokemonPage));
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
