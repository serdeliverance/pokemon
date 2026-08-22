import { Link } from 'react-router-dom'
import type { Pokemon } from '../api/types'
import { TypeBadge } from './TypeBadge'

export function PokemonCard({ pokemon }: { pokemon: Pokemon }) {
  const sprite = pokemon.sprites[0]

  return (
    <Link
      to={`/pokemons/${pokemon.id}`}
      className="flex flex-col items-center gap-2 rounded-lg border border-neutral-200 p-4 transition hover:border-neutral-400 hover:shadow-md focus:outline-none focus:ring-2 focus:ring-neutral-400"
    >
      <div className="flex h-24 w-24 items-center justify-center">
        {sprite ? (
          <img src={sprite} alt={pokemon.name} loading="lazy" className="h-full w-full object-contain" />
        ) : (
          <div className="h-full w-full rounded bg-neutral-100" />
        )}
      </div>
      <span className="text-xs text-neutral-400">#{String(pokemon.id).padStart(4, '0')}</span>
      <span className="text-center font-medium capitalize">{pokemon.name}</span>
      <div className="flex flex-wrap justify-center gap-1">
        {pokemon.category.map((type) => (
          <TypeBadge key={type} type={type} />
        ))}
      </div>
    </Link>
  )
}
