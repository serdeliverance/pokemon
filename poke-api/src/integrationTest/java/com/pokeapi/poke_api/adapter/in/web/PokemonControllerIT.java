package com.pokeapi.poke_api.adapter.in.web;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.tomakehurst.wiremock.WireMockServer;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.client.RestTestClient;
import org.springframework.util.StreamUtils;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureRestTestClient
class PokemonControllerIT {

    @Autowired
    private RestTestClient client;

    static WireMockServer pokeApiMock = new WireMockServer(0);

    // TODO add postgres test container (we are getting a warning on start up because of that)

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
    void returns404WhenPokemonNotFound() {
        pokeApiMock.stubFor(get(urlPathMatching("/pokemon/9999/?")).willReturn(notFound()));

        client.get().uri("/pokemons/9999").exchange().expectStatus().isNotFound();
    }
}
