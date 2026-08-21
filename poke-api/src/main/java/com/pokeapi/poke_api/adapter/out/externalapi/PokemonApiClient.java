package com.pokeapi.poke_api.adapter.out.externalapi;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.pokeapi.poke_api.application.port.out.PokemonProvider;
import com.pokeapi.poke_api.domain.Pokemon;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

@Component
public class PokemonApiClient implements PokemonProvider {

    private final RestClient restClient;

    public PokemonApiClient(RestClient.Builder builder, @Value("${external-api.base.url}") String externalApiBaseUrl) {
        this.restClient = builder.baseUrl(externalApiBaseUrl).build();
    }


    // TODO add caching
    // TODO add resilience4j
    @Override
    public List<Pokemon> getPokemonPage(int page, int size) {
        PokemonListResponse list = fetchList(page, size);

        return list.results().stream()
                .map(item -> extractId(item.url()))
                .map(this::fetchDetail)
                .map(this::toDomain)
                .toList();
    }

    @Override
    public Optional<Pokemon> getPokemonById(int id) {
        return Optional.empty();
    }

    private PokemonListResponse fetchList(int page, int size) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder.path("/pokemon")
                        .queryParam("limit", size)
                        .queryParam("offset", page * size)
                        .build())
                .retrieve()
                .body(PokemonListResponse.class);
    }

    private PokemonDetailResponse fetchDetail(int id) {
        return restClient.get()
                .uri("/pokemon/{id}", id)
                .retrieve()
                .body(PokemonDetailResponse.class);
    }

    private Pokemon toDomain(PokemonDetailResponse dto) {
        List<String> category = dto.types().stream()
                .map(t -> t.type().name())
                .toList();
        List<String> skills = dto.abilities().stream()
                .map(a -> a.ability().name())
                .toList();
        List<String> sprites = Stream.of(
                        dto.sprites().frontDefault(),
                        dto.sprites().frontShiny(),
                        dto.sprites().backDefault(),
                        dto.sprites().backShiny())
                .filter(Objects::nonNull)
                .toList();

        return new Pokemon(dto.id(), dto.name(), category, skills, sprites);
    }

    private int extractId(String url) {
        String trimmed = url.replaceAll("/$", "");
        return Integer.parseInt(trimmed.substring(trimmed.lastIndexOf('/') + 1));
    }

    record PokemonListResponse(int count, String next, String previous, List<PokemonListItem> results) {}
    record PokemonListItem(String name, String url) {}

    record PokemonDetailResponse(
            int id,
            String name,
            List<PokemonTypeSlot> types,
            List<PokemonAbilitySlot> abilities,
            PokemonSprites sprites
    ) {}

    record PokemonTypeSlot(int slot, NamedApiResource type) {}
    record PokemonAbilitySlot(NamedApiResource ability, @JsonProperty("is_hidden") boolean isHidden, int slot) {}
    record NamedApiResource(String name, String url) {}

    record PokemonSprites(
            @JsonProperty("front_default") String frontDefault,
            @JsonProperty("front_shiny") String frontShiny,
            @JsonProperty("back_default") String backDefault,
            @JsonProperty("back_shiny") String backShiny
    ) {}
}
