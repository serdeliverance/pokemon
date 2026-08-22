import { request } from './client'
import type { PokemonPage } from './types'

/**
 * @param page 0-based page index, matching the backend's `page` query param.
 */
export function fetchPokemonPage(
  page: number,
  size: number,
  signal?: AbortSignal,
): Promise<PokemonPage> {
  return request<PokemonPage>(`/pokemons?page=${page}&size=${size}`, signal)
}
