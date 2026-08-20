package com.pokeapi.poke_api.domain;

import java.util.List;

public record Pokemon(int id, String name, List<String> category, List<String> skills, List<String> sprites) {}
