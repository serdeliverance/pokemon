package com.pokeapi.poke_api.adapter.in.web;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.pokeapi.poke_api.generated.tables.PokemonEnrichment.POKEMON_ENRICHMENT;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.tomakehurst.wiremock.WireMockServer;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.jooq.DSLContext;
import org.jooq.JSONB;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.client.RestTestClient;
import org.springframework.util.StreamUtils;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureRestTestClient
class PokemonControllerIT {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:18.4");

    @Autowired
    private RestTestClient client;

    @Autowired
    private DSLContext dsl;

    static WireMockServer pokeApiMock = new WireMockServer(0);

    @BeforeAll
    static void startWireMock() {
        pokeApiMock.start();
    }

    @AfterAll
    static void stopWireMock() {
        pokeApiMock.stop();
    }

    @DynamicPropertySource
    static void overrideBaseUrl(DynamicPropertyRegistry registry) {
        registry.add("external-api.base.url", pokeApiMock::baseUrl);
    }

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Test
    void returnsPokemonPage() throws IOException {
        pokeApiMock.stubFor(get(urlPathEqualTo("/pokemon"))
                .withQueryParam("offset", equalTo("5"))
                .withQueryParam("limit", equalTo("5"))
                .willReturn(okJson(loadJson("stubs/pokemon-list-response.json"))));

        stubPokemonDetail(2, "ivysaur");
        stubPokemonDetail(3, "venusaur");
        stubPokemonDetail(4, "charmander");
        stubPokemonDetail(5, "charmeleon");
        stubPokemonDetail(6, "charizard");

        client.get()
                .uri("/pokemons?page=1&size=5")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .jsonPath("$.total")
                .isEqualTo(1351)
                .jsonPath("$.page")
                .isEqualTo(1)
                .jsonPath("$.size")
                .isEqualTo(5)
                .jsonPath("$.pokemons.length()")
                .isEqualTo(5)
                .jsonPath("$.pokemons[0].name")
                .isEqualTo("ivysaur")
                .jsonPath("$.pokemons[0].category[0]")
                .isEqualTo("grass")
                .jsonPath("$.pokemons[0].skills")
                .isArray()
                .jsonPath("$.pokemons[1].name")
                .isEqualTo("venusaur")
                .jsonPath("$.pokemons[2].name")
                .isEqualTo("charmander")
                .jsonPath("$.pokemons[3].name")
                .isEqualTo("charmeleon")
                .jsonPath("$.pokemons[4].name")
                .isEqualTo("charizard");
    }

    @Test
    void secondRequestForSamePageServesSummariesFromDbWithoutCallingDetailEndpoint() throws IOException {
        pokeApiMock.stubFor(get(urlPathEqualTo("/pokemon"))
                .withQueryParam("offset", equalTo("50"))
                .withQueryParam("limit", equalTo("5"))
                .willReturn(okJson(new PokemonListResponseBuilder()
                        .withResult(20, "raticate")
                        .withResult(21, "spearow")
                        .withResult(22, "fearow")
                        .withResult(23, "ekans")
                        .withResult(24, "arbok")
                        .build())));
        stubPokemonDetail(20, "raticate");
        stubPokemonDetail(21, "spearow");
        stubPokemonDetail(22, "fearow");
        stubPokemonDetail(23, "ekans");
        stubPokemonDetail(24, "arbok");

        client.get().uri("/pokemons?page=10&size=5").exchange().expectStatus().isOk();
        pokeApiMock.verify(5, getRequestedFor(urlPathMatching("/pokemon/2[0-4]/?")));

        client.get()
                .uri("/pokemons?page=10&size=5")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .jsonPath("$.pokemons[0].name")
                .isEqualTo("raticate");
        pokeApiMock.verify(5, getRequestedFor(urlPathMatching("/pokemon/2[0-4]/?")));
    }

    private void stubPokemonDetail(int id, String name) throws IOException {
        pokeApiMock.stubFor(
                get(urlPathMatching("/pokemon/" + id + "/?")).willReturn(okJson(pokemonDetailJson(id, name))));
    }

    private String pokemonDetailJson(int id, String name) throws IOException {
        ObjectNode template =
                (ObjectNode) OBJECT_MAPPER.readTree(loadJson("stubs/single-pokemon-response-template.json"));
        template.put("id", id);
        template.put("name", name);
        return OBJECT_MAPPER.writeValueAsString(template);
    }

    private static String loadJson(String classpathLocation) throws IOException {
        return StreamUtils.copyToString(
                new ClassPathResource(classpathLocation).getInputStream(), StandardCharsets.UTF_8);
    }

    @Test
    void returnsPokemonDetail() throws IOException {
        pokeApiMock.stubFor(get(urlPathMatching("/pokemon/1/?")).willReturn(okJson(pokemonDetailJson(1, "bulbasaur"))));
        pokeApiMock.stubFor(get(urlPathMatching("/pokemon-species/1/?"))
                .willReturn(okJson(loadJson("stubs/single-pokemon-species-response.json"))));
        pokeApiMock.stubFor(get(urlPathMatching("/evolution-chain/1/?"))
                .willReturn(okJson(loadJson("stubs/evolution-chain-response.json"))));

        client.get()
                .uri("/pokemons/1")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .jsonPath("$.name")
                .isEqualTo("bulbasaur")
                .jsonPath("$.sprites")
                .isArray()
                .jsonPath("$.stats[0].name")
                .isEqualTo("hp")
                .jsonPath("$.description")
                .isEqualTo("A strange seed was planted on its back at birth.")
                .jsonPath("$.evolutionChain.length()")
                .isEqualTo(3)
                .jsonPath("$.evolutionChain[0].name")
                .isEqualTo("bulbasaur")
                .jsonPath("$.evolutionChain[0].stage")
                .isEqualTo(1)
                .jsonPath("$.evolutionChain[1].name")
                .isEqualTo("ivysaur")
                .jsonPath("$.evolutionChain[2].name")
                .isEqualTo("venusaur");
    }

    @Test
    void secondRequestForSameDetailIsServedFromDbWithoutCallingRemote() throws IOException {
        pokeApiMock.stubFor(
                get(urlPathMatching("/pokemon/30/?")).willReturn(okJson(pokemonDetailJson(30, "nidoking"))));
        pokeApiMock.stubFor(get(urlPathMatching("/pokemon-species/30/?"))
                .willReturn(okJson(loadJson("stubs/single-pokemon-species-response.json"))));
        pokeApiMock.stubFor(get(urlPathMatching("/evolution-chain/30/?"))
                .willReturn(okJson(loadJson("stubs/evolution-chain-response.json"))));

        client.get().uri("/pokemons/30").exchange().expectStatus().isOk();
        pokeApiMock.verify(1, getRequestedFor(urlPathMatching("/pokemon/30/?")));

        client.get()
                .uri("/pokemons/30")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .jsonPath("$.name")
                .isEqualTo("nidoking");
        pokeApiMock.verify(1, getRequestedFor(urlPathMatching("/pokemon/30/?")));
    }

    @Test
    void includesEnrichmentAttributesInDetailResponseWhenPresent() throws IOException {
        pokeApiMock.stubFor(
                get(urlPathMatching("/pokemon/40/?")).willReturn(okJson(pokemonDetailJson(40, "wigglytuff"))));
        pokeApiMock.stubFor(get(urlPathMatching("/pokemon-species/40/?"))
                .willReturn(okJson(loadJson("stubs/single-pokemon-species-response.json"))));
        pokeApiMock.stubFor(get(urlPathMatching("/evolution-chain/40/?"))
                .willReturn(okJson(loadJson("stubs/evolution-chain-response.json"))));

        dsl.insertInto(POKEMON_ENRICHMENT)
                .set(POKEMON_ENRICHMENT.POKEMON_ID, 40)
                .set(
                        POKEMON_ENRICHMENT.ATTRIBUTES,
                        JSONB.valueOf("{\"nameEs\": \"Wigglytuff\", \"region\": \"Kanto\"}"))
                .execute();

        client.get()
                .uri("/pokemons/40")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .jsonPath("$.enrichment.nameEs")
                .isEqualTo("Wigglytuff")
                .jsonPath("$.enrichment.region")
                .isEqualTo("Kanto");
    }

    @Test
    void returns404WhenPokemonNotFound() {
        pokeApiMock.stubFor(get(urlPathMatching("/pokemon/9999/?")).willReturn(notFound()));

        client.get().uri("/pokemons/9999").exchange().expectStatus().isNotFound();
    }

    private static final class PokemonListResponseBuilder {
        private final ObjectMapper mapper = new ObjectMapper();
        private final com.fasterxml.jackson.databind.node.ArrayNode results = mapper.createArrayNode();

        PokemonListResponseBuilder withResult(int id, String name) {
            ObjectNode item = mapper.createObjectNode();
            item.put("name", name);
            item.put("url", "https://pokeapi.co/api/v2/pokemon/" + id + "/");
            results.add(item);
            return this;
        }

        String build() {
            ObjectNode root = mapper.createObjectNode();
            root.put("count", 1351);
            root.putNull("next");
            root.putNull("previous");
            root.set("results", results);
            return root.toString();
        }
    }
}
