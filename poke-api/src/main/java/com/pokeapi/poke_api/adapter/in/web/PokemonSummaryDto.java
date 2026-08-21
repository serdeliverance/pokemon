package com.pokeapi.poke_api.adapter.in.web;

import java.util.List;

public record PokemonSummaryDto(
        int id, String name, List<String> category, List<String> skills, List<String> sprites) {}
