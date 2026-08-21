package com.pokeapi.poke_api.adapter.out.externalapi;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.pokeapi.poke_api.application.port.out.PokemonProvider;
import com.pokeapi.poke_api.domain.EvolutionStage;
import com.pokeapi.poke_api.domain.Pokemon;
import com.pokeapi.poke_api.domain.PokemonDetail;
import com.pokeapi.poke_api.domain.PokemonStat;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

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
    public Optional<PokemonDetail> getPokemonDetailById(int id) {
        try {
            PokemonDetailResponse detail = fetchDetail(id);
            PokemonSpeciesResponse species = fetchSpecies(id);
            int evolutionChainId = extractId(species.evolutionChain().url());
            EvolutionChainResponse evolutionChain = fetchEvolutionChain(evolutionChainId);

            return Optional.of(toDomainDetail(detail, species, evolutionChain));
        } catch (HttpClientErrorException.NotFound e) {
            return Optional.empty();
        }
    }

    private PokemonListResponse fetchList(int page, int size) {
        return restClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/pokemon")
                        .queryParam("limit", size)
                        .queryParam("offset", page * size)
                        .build())
                .retrieve()
                .body(PokemonListResponse.class);
    }

    private PokemonDetailResponse fetchDetail(int id) {
        return restClient.get().uri("/pokemon/{id}", id).retrieve().body(PokemonDetailResponse.class);
    }

    private PokemonSpeciesResponse fetchSpecies(int id) {
        return restClient.get().uri("/pokemon-species/{id}", id).retrieve().body(PokemonSpeciesResponse.class);
    }

    private EvolutionChainResponse fetchEvolutionChain(int id) {
        return restClient.get().uri("/evolution-chain/{id}", id).retrieve().body(EvolutionChainResponse.class);
    }

    private Pokemon toDomain(PokemonDetailResponse dto) {
        return new Pokemon(dto.id(), dto.name(), category(dto), skills(dto), sprites(dto));
    }

    private PokemonDetail toDomainDetail(
            PokemonDetailResponse detail, PokemonSpeciesResponse species, EvolutionChainResponse evolutionChain) {
        return new PokemonDetail(
                detail.id(),
                detail.name(),
                category(detail),
                skills(detail),
                sprites(detail),
                stats(detail),
                description(species),
                evolutionStages(evolutionChain));
    }

    private List<String> category(PokemonDetailResponse dto) {
        return dto.types().stream().map(t -> t.type().name()).toList();
    }

    private List<String> skills(PokemonDetailResponse dto) {
        return dto.abilities().stream().map(a -> a.ability().name()).toList();
    }

    private List<String> sprites(PokemonDetailResponse dto) {
        return Stream.of(
                        dto.sprites().frontDefault(),
                        dto.sprites().frontShiny(),
                        dto.sprites().backDefault(),
                        dto.sprites().backShiny())
                .filter(Objects::nonNull)
                .toList();
    }

    private List<PokemonStat> stats(PokemonDetailResponse dto) {
        return dto.stats().stream()
                .map(s -> new PokemonStat(s.stat().name(), s.baseStat()))
                .toList();
    }

    private String description(PokemonSpeciesResponse species) {
        return species.flavorTextEntries().stream()
                .filter(entry -> "en".equals(entry.language().name()))
                .map(entry -> entry.flavorText().replaceAll("[\\n\\f\\r]", " "))
                .findFirst()
                .orElse("");
    }

    private List<EvolutionStage> evolutionStages(EvolutionChainResponse evolutionChain) {
        List<EvolutionStage> stages = new ArrayList<>();
        Deque<ChainLink> queue = new ArrayDeque<>();
        queue.add(evolutionChain.chain());
        int stage = 1;

        while (!queue.isEmpty()) {
            int levelSize = queue.size();
            for (int i = 0; i < levelSize; i++) {
                ChainLink link = queue.poll();
                stages.add(new EvolutionStage(
                        extractId(link.species().url()), link.species().name(), stage));
                queue.addAll(link.evolvesTo());
            }
            stage++;
        }

        return stages;
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
            List<PokemonStatSlot> stats,
            PokemonSprites sprites) {}

    record PokemonTypeSlot(int slot, NamedApiResource type) {}

    record PokemonAbilitySlot(
            NamedApiResource ability,
            @JsonProperty("is_hidden") boolean isHidden,
            int slot) {}

    record PokemonStatSlot(@JsonProperty("base_stat") int baseStat, int effort, NamedApiResource stat) {}

    record NamedApiResource(String name, String url) {}

    record PokemonSprites(
            @JsonProperty("front_default") String frontDefault,
            @JsonProperty("front_shiny") String frontShiny,
            @JsonProperty("back_default") String backDefault,
            @JsonProperty("back_shiny") String backShiny) {}

    record PokemonSpeciesResponse(
            @JsonProperty("flavor_text_entries") List<FlavorTextEntry> flavorTextEntries,
            @JsonProperty("evolution_chain") NamedApiResource evolutionChain) {}

    record FlavorTextEntry(@JsonProperty("flavor_text") String flavorText, NamedApiResource language) {}

    record EvolutionChainResponse(ChainLink chain) {}

    record ChainLink(
            NamedApiResource species,
            @JsonProperty("evolves_to") List<ChainLink> evolvesTo) {}
}
