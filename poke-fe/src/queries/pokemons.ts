import { queryOptions } from '@tanstack/react-query'
import { ApiError } from '../api/client'
import { fetchPokemonDetail, fetchPokemonPage } from '../api/pokemons'

export const pokemonKeys = {
  all: ['pokemons'] as const,
  page: (page: number, size: number) => [...pokemonKeys.all, 'page', page, size] as const,
  detail: (id: number) => [...pokemonKeys.all, 'detail', id] as const,
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

export function pokemonDetailQuery(id: number) {
  return queryOptions({
    queryKey: pokemonKeys.detail(id),
    queryFn: ({ signal }) => fetchPokemonDetail(id, signal),
    // A 404 means the pokemon doesn't exist; retrying won't change that.
    retry: (failureCount, error) => !(error instanceof ApiError && error.status === 404) && failureCount < 1,
  })
}
