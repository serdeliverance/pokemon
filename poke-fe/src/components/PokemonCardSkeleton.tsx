export function PokemonCardSkeleton() {
  return (
    <div className="flex animate-pulse flex-col items-center gap-2 rounded-lg border border-neutral-200 p-4">
      <div className="h-24 w-24 rounded bg-neutral-100" />
      <div className="h-3 w-10 rounded bg-neutral-100" />
      <div className="h-4 w-20 rounded bg-neutral-100" />
      <div className="h-4 w-16 rounded bg-neutral-100" />
    </div>
  )
}
