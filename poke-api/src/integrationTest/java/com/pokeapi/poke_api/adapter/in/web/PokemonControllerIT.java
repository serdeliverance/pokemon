package com.pokeapi.poke_api.adapter.in.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.client.RestTestClient;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureRestTestClient
class PokemonControllerIT {

    @Autowired
    private RestTestClient client;

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
        registry.add("external-api.base-url", pokeApiMock::baseUrl);
    }

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Test
    void returnsPokemonPage() throws IOException {
        // TODO fix error in stub, it is going against the real service instead.
        pokeApiMock.stubFor(get(urlPathEqualTo("/pokemon"))
                .withQueryParam("offset", equalTo("1"))
                .withQueryParam("limit", equalTo("5"))
                .willReturn(okJson(loadJson("stubs/pokemon-list-response.json"))));

        stubPokemonDetail(2, "ivysaur");
        stubPokemonDetail(3, "venusaur");
        stubPokemonDetail(4, "charmander");
        stubPokemonDetail(5, "charmeleon");
        stubPokemonDetail(6, "charizard");

        client.get().uri("/pokemons?page=1&size=5")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.length()").isEqualTo(5)
                .jsonPath("$[0].name").isEqualTo("ivysaur")
                .jsonPath("$[0].category[0]").isEqualTo("grass")
                .jsonPath("$[0].skills").isArray()
                .jsonPath("$[1].name").isEqualTo("venusaur")
                .jsonPath("$[2].name").isEqualTo("charmander")
                .jsonPath("$[3].name").isEqualTo("charmeleon")
                .jsonPath("$[4].name").isEqualTo("charizard");
    }

    private void stubPokemonDetail(int id, String name) throws IOException {
        pokeApiMock.stubFor(get(urlPathMatching("/pokemon/" + id + "/?"))
                .willReturn(okJson(pokemonDetailJson(id, name))));
    }

    private String pokemonDetailJson(int id, String name) throws IOException {
        ObjectNode template = (ObjectNode) OBJECT_MAPPER.readTree(loadJson("stubs/single-pokemon-response-template.json"));
        template.put("id", id);
        template.put("name", name);
        return OBJECT_MAPPER.writeValueAsString(template);
    }

    private static String loadJson(String classpathLocation) throws IOException {
        return StreamUtils.copyToString(
                new ClassPathResource(classpathLocation).getInputStream(),
                StandardCharsets.UTF_8);
    }
}
