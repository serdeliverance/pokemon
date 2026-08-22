const TYPE_COLORS: Record<string, string> = {
  normal: 'bg-neutral-300 text-neutral-900',
  fire: 'bg-orange-500 text-white',
  water: 'bg-blue-500 text-white',
  electric: 'bg-yellow-400 text-yellow-950',
  grass: 'bg-green-500 text-white',
  ice: 'bg-cyan-300 text-cyan-950',
  fighting: 'bg-red-700 text-white',
  poison: 'bg-purple-500 text-white',
  ground: 'bg-amber-600 text-white',
  flying: 'bg-indigo-300 text-indigo-950',
  psychic: 'bg-pink-500 text-white',
  bug: 'bg-lime-500 text-lime-950',
  rock: 'bg-yellow-700 text-white',
  ghost: 'bg-violet-700 text-white',
  dragon: 'bg-indigo-600 text-white',
  dark: 'bg-neutral-700 text-white',
  steel: 'bg-slate-400 text-slate-950',
  fairy: 'bg-pink-300 text-pink-950',
}

const FALLBACK_COLOR = 'bg-neutral-200 text-neutral-800'

export function TypeBadge({ type }: { type: string }) {
  const colorClasses = TYPE_COLORS[type] ?? FALLBACK_COLOR

  return (
    <span
      className={`rounded-full px-2 py-0.5 text-xs font-medium capitalize ${colorClasses}`}
    >
      {type}
    </span>
  )
}
