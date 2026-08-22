CREATE TABLE pokemon (
    id                 INT PRIMARY KEY,
    name               TEXT        NOT NULL,
    category           TEXT[]      NOT NULL,
    skills             TEXT[]      NOT NULL,
    sprites            TEXT[]      NOT NULL,
    description        TEXT,
    summary_synced_at  TIMESTAMPTZ NOT NULL,
    detail_synced_at   TIMESTAMPTZ
);

CREATE TABLE pokemon_stat (
    pokemon_id INT  NOT NULL REFERENCES pokemon (id) ON DELETE CASCADE,
    name       TEXT NOT NULL,
    base_stat  INT  NOT NULL,
    PRIMARY KEY (pokemon_id, name)
);

CREATE TABLE pokemon_evolution_stage (
    pokemon_id   INT  NOT NULL REFERENCES pokemon (id) ON DELETE CASCADE,
    species_id   INT  NOT NULL,
    species_name TEXT NOT NULL,
    stage        INT  NOT NULL,
    PRIMARY KEY (pokemon_id, species_id)
);
