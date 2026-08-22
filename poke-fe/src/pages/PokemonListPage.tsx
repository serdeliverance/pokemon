import { useEffect } from 'react'
import { Link, useSearchParams } from 'react-router-dom'
import { keepPreviousData, useQuery, useQueryClient } from '@tanstack/react-query'
import { ApiError } from '../api/client'
import { pokemonPageQuery } from '../queries/pokemons'
import { PokemonCard } from '../components/PokemonCard'
import { PokemonCardSkeleton } from '../components/PokemonCardSkeleton'
import { Pagination } from '../components/Pagination'
import { ErrorState } from '../components/ErrorState'

const PAGE_SIZE = 20

function parsePage(raw: string | null): number {
  const parsed = Number(raw)
  return Number.isInteger(parsed) && parsed >= 1 ? parsed : 1
}

export function PokemonListPage() {
  const [searchParams] = useSearchParams()
  const page = parsePage(searchParams.get('page'))
  const queryClient = useQueryClient()

  const query = useQuery({
    ...pokemonPageQuery(page - 1, PAGE_SIZE),
    placeholderData: keepPreviousData,
  })

  const totalPages = query.data ? Math.max(1, Math.ceil(query.data.total / query.data.size)) : undefined

  // Prefetch the next page so paging forward feels instant.
  useEffect(() => {
    if (totalPages !== undefined && page < totalPages) {
      void queryClient.prefetchQuery(pokemonPageQuery(page, PAGE_SIZE))
    }
  }, [page, totalPages, queryClient])

  return (
    <div className="flex flex-col gap-6">
      <h1 className="text-2xl font-semibold">Pokemon</h1>

      {query.isError ? (
        <ErrorState
          message={
            query.error instanceof ApiError
              ? `Couldn't load pokemon (${query.error.status}).`
              : "Couldn't load pokemon."
          }
          onRetry={() => void query.refetch()}
        />
      ) : query.isPending ? (
        <div className="grid grid-cols-2 gap-4 sm:grid-cols-3 lg:grid-cols-4 xl:grid-cols-5">
          {Array.from({ length: PAGE_SIZE }, (_, index) => (
            <PokemonCardSkeleton key={index} />
          ))}
        </div>
      ) : query.data.pokemons.length === 0 ? (
        <div className="flex flex-col items-center gap-3 rounded-lg border border-neutral-200 p-8 text-center text-neutral-500">
          <p>No pokemon on this page.</p>
          <Link to="/pokemons?page=1" className="text-sm underline">
            Back to page 1
          </Link>
        </div>
      ) : (
        <div className="grid grid-cols-2 gap-4 sm:grid-cols-3 lg:grid-cols-4 xl:grid-cols-5">
          {query.data.pokemons.map((pokemon) => (
            <PokemonCard key={pokemon.id} pokemon={pokemon} />
          ))}
        </div>
      )}

      {totalPages !== undefined && <Pagination currentPage={page} totalPages={totalPages} />}
    </div>
  )
}
