import type { PokemonStat } from '../api/types'

const MAX_STAT = 255

function formatStatName(name: string): string {
  const withSpaces = name.replace(/-/g, ' ')
  return withSpaces.charAt(0).toUpperCase() + withSpaces.slice(1)
}

export function StatBars({ stats }: { stats: PokemonStat[] }) {
  return (
    <div className="flex flex-col gap-2">
      {stats.map((stat) => (
        <div key={stat.name} className="grid grid-cols-[7rem_1fr_2.5rem] items-center gap-2 text-sm">
          <span className="text-neutral-500">{formatStatName(stat.name)}</span>
          <div className="h-2 rounded-full bg-neutral-100">
            <div
              className="h-2 rounded-full bg-neutral-700"
              style={{ width: `${Math.min(100, (stat.baseStat / MAX_STAT) * 100)}%` }}
            />
          </div>
          <span className="text-right text-neutral-700">{stat.baseStat}</span>
        </div>
      ))}
    </div>
  )
}
