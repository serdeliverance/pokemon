-- pokemon_id intentionally has no FK to pokemon(id): proprietary attributes must be
-- authorable for a Pokemon that has not been replicated into the local store yet.
CREATE TABLE pokemon_enrichment (
    pokemon_id INT PRIMARY KEY,
    attributes JSONB       NOT NULL DEFAULT '{}'::jsonb,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_pokemon_enrichment_attributes ON pokemon_enrichment USING GIN (attributes);
