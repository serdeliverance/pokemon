import { queryOptions } from '@tanstack/react-query'
import { fetchPokemonPage } from '../api/pokemons'

export const pokemonKeys = {
  all: ['pokemons'] as const,
  page: (page: number, size: number) => [...pokemonKeys.all, 'page', page, size] as const,
}

/**
 * @param page 0-based page index, matching the backend's `page` query param.
 */
export function pokemonPageQuery(page: number, size: number) {
  return queryOptions({
    queryKey: pokemonKeys.page(page, size),
    queryFn: ({ signal }) => fetchPokemonPage(page, size, signal),
  })
}
