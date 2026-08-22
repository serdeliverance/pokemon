export interface Pokemon {
  id: number
  name: string
  category: string[]
  skills: string[]
  sprites: string[]
}

export interface PokemonPage {
  pokemons: Pokemon[]
  page: number
  size: number
  total: number
}

export interface PokemonStat {
  name: string
  baseStat: number
}

export interface EvolutionStage {
  id: number
  name: string
  stage: number
}

export interface PokemonDetail extends Pokemon {
  stats: PokemonStat[]
  description: string
  evolutionChain: EvolutionStage[]
}
