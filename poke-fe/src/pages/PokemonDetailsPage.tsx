import { Link, useParams } from 'react-router-dom'
import { useQueryClient } from '@tanstack/react-query'
import type { Pokemon, PokemonPage } from '../api/types'
import { pokemonKeys } from '../queries/pokemons'
import { TypeBadge } from '../components/TypeBadge'

function findCachedPokemon(
  queryClient: ReturnType<typeof useQueryClient>,
  id: number,
): Pokemon | undefined {
  return queryClient
    .getQueriesData<PokemonPage>({ queryKey: pokemonKeys.all })
    .flatMap(([, data]) => data?.pokemons ?? [])
    .find((pokemon) => pokemon.id === id)
}

export function PokemonDetailsPage() {
  const { id } = useParams<{ id: string }>()
  const queryClient = useQueryClient()
  const pokemon = id ? findCachedPokemon(queryClient, Number(id)) : undefined

  return (
    <div className="flex flex-col gap-4">
      <Link to="/pokemons" className="text-sm underline">
        &larr; Back to list
      </Link>

      {pokemon ? (
        <div className="flex flex-col items-center gap-3 rounded-lg border border-neutral-200 p-8">
          <img src={pokemon.sprites[0]} alt={pokemon.name} className="h-32 w-32 object-contain" />
          <span className="text-sm text-neutral-400">#{String(pokemon.id).padStart(4, '0')}</span>
          <h1 className="text-2xl font-semibold capitalize">{pokemon.name}</h1>
          <div className="flex gap-1">
            {pokemon.category.map((type) => (
              <TypeBadge key={type} type={type} />
            ))}
          </div>
          <p className="text-sm text-neutral-500">Full details coming soon.</p>
        </div>
      ) : (
        <div className="rounded-lg border border-neutral-200 p-8 text-center text-neutral-500">
          <p>Pokemon #{id}</p>
          <p className="text-sm">Full details coming soon.</p>
        </div>
      )}
    </div>
  )
}
