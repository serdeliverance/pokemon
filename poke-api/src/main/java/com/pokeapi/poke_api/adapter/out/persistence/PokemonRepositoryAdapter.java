package com.pokeapi.poke_api.adapter.out.persistence;

import static com.pokeapi.poke_api.generated.tables.Pokemon.POKEMON;
import static com.pokeapi.poke_api.generated.tables.PokemonEnrichment.POKEMON_ENRICHMENT;
import static com.pokeapi.poke_api.generated.tables.PokemonEvolutionStage.POKEMON_EVOLUTION_STAGE;
import static com.pokeapi.poke_api.generated.tables.PokemonStat.POKEMON_STAT;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pokeapi.poke_api.application.port.out.PokemonRepository;
import com.pokeapi.poke_api.domain.EvolutionStage;
import com.pokeapi.poke_api.domain.Pokemon;
import com.pokeapi.poke_api.domain.PokemonDetail;
import com.pokeapi.poke_api.domain.PokemonEnrichment;
import com.pokeapi.poke_api.domain.PokemonStat;
import com.pokeapi.poke_api.generated.tables.records.PokemonEvolutionStageRecord;
import com.pokeapi.poke_api.generated.tables.records.PokemonRecord;
import com.pokeapi.poke_api.generated.tables.records.PokemonStatRecord;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.jooq.DSLContext;
import org.jooq.JSONB;
import org.jooq.Record;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class PokemonRepositoryAdapter implements PokemonRepository {

    // Owned locally rather than injected: this is a private serialization concern of the
    // JSONB<->Map mapping, independent of whichever ObjectMapper the web layer is wired with.
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private final DSLContext dsl;

    public PokemonRepositoryAdapter(DSLContext dsl) {
        this.dsl = dsl;
    }

    @Override
    public Map<Integer, Pokemon> findSummariesByIds(List<Integer> ids) {
        if (ids.isEmpty()) {
            return Map.of();
        }

        return dsl.selectFrom(POKEMON).where(POKEMON.ID.in(ids)).fetch().stream()
                .map(this::toPokemon)
                .collect(Collectors.toMap(Pokemon::id, pokemon -> pokemon));
    }

    @Override
    public Optional<PokemonDetail> findDetailById(int id) {
        PokemonRecord record = dsl.selectFrom(POKEMON)
                .where(POKEMON.ID.eq(id))
                .and(POKEMON.DETAIL_SYNCED_AT.isNotNull())
                .fetchOne();

        if (record == null) {
            return Optional.empty();
        }

        List<PokemonStat> stats = dsl.selectFrom(POKEMON_STAT)
                .where(POKEMON_STAT.POKEMON_ID.eq(id))
                .fetch()
                .map(r -> new PokemonStat(r.getName(), r.getBaseStat()));

        List<EvolutionStage> evolutionChain = dsl.selectFrom(POKEMON_EVOLUTION_STAGE)
                .where(POKEMON_EVOLUTION_STAGE.POKEMON_ID.eq(id))
                .orderBy(POKEMON_EVOLUTION_STAGE.STAGE)
                .fetch()
                .map(r -> new EvolutionStage(r.getSpeciesId(), r.getSpeciesName(), r.getStage()));

        return Optional.of(new PokemonDetail(
                record.getId(),
                record.getName(),
                List.of(record.getCategory()),
                List.of(record.getSkills()),
                List.of(record.getSprites()),
                stats,
                record.getDescription(),
                evolutionChain,
                PokemonEnrichment.empty(record.getId())));
    }

    @Override
    public void saveSummaries(List<Pokemon> pokemons) {
        if (pokemons.isEmpty()) {
            return;
        }

        OffsetDateTime now = OffsetDateTime.now();
        var batch = dsl.batch(pokemons.stream()
                .map(pokemon -> dsl.insertInto(POKEMON)
                        .set(POKEMON.ID, pokemon.id())
                        .set(POKEMON.NAME, pokemon.name())
                        .set(POKEMON.CATEGORY, toArray(pokemon.category()))
                        .set(POKEMON.SKILLS, toArray(pokemon.skills()))
                        .set(POKEMON.SPRITES, toArray(pokemon.sprites()))
                        .set(POKEMON.SUMMARY_SYNCED_AT, now)
                        .onConflict(POKEMON.ID)
                        .doUpdate()
                        .set(POKEMON.NAME, pokemon.name())
                        .set(POKEMON.CATEGORY, toArray(pokemon.category()))
                        .set(POKEMON.SKILLS, toArray(pokemon.skills()))
                        .set(POKEMON.SPRITES, toArray(pokemon.sprites()))
                        .set(POKEMON.SUMMARY_SYNCED_AT, now))
                .toList());
        batch.execute();
    }

    @Override
    @Transactional
    public void saveDetail(PokemonDetail detail) {
        OffsetDateTime now = OffsetDateTime.now();

        dsl.insertInto(POKEMON)
                .set(POKEMON.ID, detail.id())
                .set(POKEMON.NAME, detail.name())
                .set(POKEMON.CATEGORY, toArray(detail.category()))
                .set(POKEMON.SKILLS, toArray(detail.skills()))
                .set(POKEMON.SPRITES, toArray(detail.sprites()))
                .set(POKEMON.DESCRIPTION, detail.description())
                .set(POKEMON.SUMMARY_SYNCED_AT, now)
                .set(POKEMON.DETAIL_SYNCED_AT, now)
                .onConflict(POKEMON.ID)
                .doUpdate()
                .set(POKEMON.NAME, detail.name())
                .set(POKEMON.CATEGORY, toArray(detail.category()))
                .set(POKEMON.SKILLS, toArray(detail.skills()))
                .set(POKEMON.SPRITES, toArray(detail.sprites()))
                .set(POKEMON.DESCRIPTION, detail.description())
                .set(POKEMON.SUMMARY_SYNCED_AT, now)
                .set(POKEMON.DETAIL_SYNCED_AT, now)
                .execute();

        dsl.deleteFrom(POKEMON_STAT)
                .where(POKEMON_STAT.POKEMON_ID.eq(detail.id()))
                .execute();
        if (!detail.stats().isEmpty()) {
            var statRecords = detail.stats().stream()
                    .map(stat -> new PokemonStatRecord(detail.id(), stat.name(), stat.baseStat()))
                    .toList();
            dsl.batchInsert(statRecords).execute();
        }

        dsl.deleteFrom(POKEMON_EVOLUTION_STAGE)
                .where(POKEMON_EVOLUTION_STAGE.POKEMON_ID.eq(detail.id()))
                .execute();
        if (!detail.evolutionChain().isEmpty()) {
            var evolutionRecords = detail.evolutionChain().stream()
                    .map(stage -> new PokemonEvolutionStageRecord(detail.id(), stage.id(), stage.name(), stage.stage()))
                    .toList();
            dsl.batchInsert(evolutionRecords).execute();
        }
    }

    @Override
    public Optional<PokemonEnrichment> findEnrichmentById(int id) {
        return dsl.selectFrom(POKEMON_ENRICHMENT)
                .where(POKEMON_ENRICHMENT.POKEMON_ID.eq(id))
                .fetchOptional()
                .map(r -> new PokemonEnrichment(id, toMap(r.getAttributes())));
    }

    private Pokemon toPokemon(Record record) {
        return new Pokemon(
                record.get(POKEMON.ID),
                record.get(POKEMON.NAME),
                List.of(record.get(POKEMON.CATEGORY)),
                List.of(record.get(POKEMON.SKILLS)),
                List.of(record.get(POKEMON.SPRITES)));
    }

    private String[] toArray(List<String> values) {
        return values.toArray(new String[0]);
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> toMap(JSONB attributes) {
        try {
            return OBJECT_MAPPER.readValue(attributes.data(), Map.class);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to parse pokemon_enrichment.attributes as JSON", e);
        }
    }
}
