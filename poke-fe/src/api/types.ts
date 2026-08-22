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
