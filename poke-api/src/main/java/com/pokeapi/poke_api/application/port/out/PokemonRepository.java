package com.pokeapi.poke_api.application.port.out;

import com.pokeapi.poke_api.domain.Pokemon;
import com.pokeapi.poke_api.domain.PokemonDetail;
import com.pokeapi.poke_api.domain.PokemonEnrichment;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface PokemonRepository {
    Map<Integer, Pokemon> findSummariesByIds(List<Integer> ids);

    Optional<PokemonDetail> findDetailById(int id);

    void saveSummaries(List<Pokemon> pokemons);

    void saveDetail(PokemonDetail detail);

    Optional<PokemonEnrichment> findEnrichmentById(int id);
}
