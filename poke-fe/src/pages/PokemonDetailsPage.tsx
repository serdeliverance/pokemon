import { Link, useLocation, useParams } from 'react-router-dom'
import { useQuery } from '@tanstack/react-query'
import { ApiError } from '../api/client'
import { pokemonDetailQuery } from '../queries/pokemons'
import { TypeBadge } from '../components/TypeBadge'
import { SpriteGallery } from '../components/SpriteGallery'
import { StatBars } from '../components/StatBars'
import { ErrorState } from '../components/ErrorState'

function useBackToListHref(): string {
  const location = useLocation()
  const fromPage = (location.state as { fromPage?: number } | null)?.fromPage
  return fromPage ? `/pokemons?page=${fromPage}` : '/pokemons'
}

function DetailsSkeleton() {
  return (
    <div className="flex animate-pulse flex-col gap-6 md:flex-row">
      <div className="h-40 w-40 rounded-lg bg-neutral-100" />
      <div className="flex flex-1 flex-col gap-3">
        <div className="h-6 w-32 rounded bg-neutral-100" />
        <div className="h-4 w-full rounded bg-neutral-100" />
        <div className="h-4 w-3/4 rounded bg-neutral-100" />
        <div className="h-24 w-full rounded bg-neutral-100" />
      </div>
    </div>
  )
}

export function PokemonDetailsPage() {
  const { id } = useParams<{ id: string }>()
  const backHref = useBackToListHref()
  const numericId = Number(id)
  const isValidId = Number.isInteger(numericId) && numericId > 0

  const query = useQuery({ ...pokemonDetailQuery(numericId), enabled: isValidId })

  const isNotFound = !isValidId || (query.error instanceof ApiError && query.error.status === 404)

  return (
    <div className="flex flex-col gap-4">
      <Link to={backHref} className="text-sm underline">
        &larr; Back to list
      </Link>

      {isNotFound ? (
        <div className="rounded-lg border border-neutral-200 p-8 text-center text-neutral-500">
          <p>No pokemon with id {id}.</p>
        </div>
      ) : query.isError ? (
        <ErrorState message="Couldn't load this pokemon." onRetry={() => void query.refetch()} />
      ) : query.isPending ? (
        <DetailsSkeleton />
      ) : (
        <div className="flex flex-col gap-6 rounded-lg border border-neutral-200 p-6 md:flex-row">
          <SpriteGallery sprites={query.data.sprites} name={query.data.name} />

          <div className="flex flex-1 flex-col gap-3">
            <div>
              <span className="text-sm text-neutral-400">#{String(query.data.id).padStart(4, '0')}</span>
              <h1 className="text-2xl font-semibold capitalize">{query.data.name}</h1>
            </div>

            <div className="flex flex-wrap gap-1">
              {query.data.category.map((type) => (
                <TypeBadge key={type} type={type} />
              ))}
            </div>

            <p className="text-sm text-neutral-600">{query.data.description}</p>

            <div className="flex flex-wrap gap-1">
              {query.data.skills.map((skill) => (
                <span key={skill} className="rounded-full bg-neutral-100 px-2 py-0.5 text-xs capitalize">
                  {skill.replace(/-/g, ' ')}
                </span>
              ))}
            </div>

            <StatBars stats={query.data.stats} />
          </div>
        </div>
      )}
    </div>
  )
}
