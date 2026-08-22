import { useState } from 'react'

function labelSprite(url: string): string {
  const isBack = url.includes('/back/')
  const isShiny = url.includes('/shiny/')

  if (isBack && isShiny) return 'Back shiny'
  if (isBack) return 'Back'
  if (isShiny) return 'Shiny'
  return 'Front'
}

export function SpriteGallery({ sprites, name }: { sprites: string[]; name: string }) {
  const [selectedIndex, setSelectedIndex] = useState(0)
  const selected = sprites[selectedIndex]

  return (
    <div className="flex flex-col items-center gap-3">
      <div className="flex h-40 w-40 items-center justify-center rounded-lg border border-neutral-200">
        {selected ? (
          <img src={selected} alt={name} className="h-full w-full object-contain" />
        ) : (
          <div className="h-full w-full rounded bg-neutral-100" />
        )}
      </div>

      {sprites.length > 1 && (
        <div className="flex flex-wrap justify-center gap-2">
          {sprites.map((sprite, index) => (
            <button
              key={sprite}
              type="button"
              onClick={() => setSelectedIndex(index)}
              aria-pressed={index === selectedIndex}
              title={labelSprite(sprite)}
              className={`flex h-12 w-12 items-center justify-center rounded border p-1 ${
                index === selectedIndex ? 'border-neutral-900' : 'border-neutral-200 hover:border-neutral-400'
              }`}
            >
              <img src={sprite} alt={labelSprite(sprite)} className="h-full w-full object-contain" />
            </button>
          ))}
        </div>
      )}
    </div>
  )
}
