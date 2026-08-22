import { Link } from 'react-router-dom'

const ELLIPSIS = 'ellipsis' as const

/** Builds a windowed page list: first, last, current ±1, and ellipses for the gaps. */
function buildPageWindow(currentPage: number, totalPages: number): (number | typeof ELLIPSIS)[] {
  const pages = new Set<number>([1, totalPages, currentPage - 1, currentPage, currentPage + 1])
  const sorted = [...pages].filter((page) => page >= 1 && page <= totalPages).sort((a, b) => a - b)

  const result: (number | typeof ELLIPSIS)[] = []
  let previous = 0
  for (const page of sorted) {
    if (previous !== 0 && page - previous > 1) {
      result.push(ELLIPSIS)
    }
    result.push(page)
    previous = page
  }
  return result
}

function pageHref(page: number) {
  return `/pokemons?page=${page}`
}

export function Pagination({ currentPage, totalPages }: { currentPage: number; totalPages: number }) {
  const pageWindow = buildPageWindow(currentPage, totalPages)
  const hasPrev = currentPage > 1
  const hasNext = currentPage < totalPages

  return (
    <nav className="flex flex-col items-center gap-2" aria-label="Pagination">
      <div className="flex items-center gap-1">
        {hasPrev ? (
          <Link to={pageHref(currentPage - 1)} className="rounded-md px-3 py-1.5 text-sm hover:bg-neutral-100">
            Prev
          </Link>
        ) : (
          <span className="cursor-not-allowed rounded-md px-3 py-1.5 text-sm text-neutral-300">Prev</span>
        )}

        {pageWindow.map((entry, index) =>
          entry === ELLIPSIS ? (
            <span key={`ellipsis-${index}`} className="px-2 text-sm text-neutral-400">
              &hellip;
            </span>
          ) : (
            <Link
              key={entry}
              to={pageHref(entry)}
              aria-current={entry === currentPage ? 'page' : undefined}
              className={`rounded-md px-3 py-1.5 text-sm ${
                entry === currentPage
                  ? 'bg-neutral-900 text-white'
                  : 'hover:bg-neutral-100'
              }`}
            >
              {entry}
            </Link>
          ),
        )}

        {hasNext ? (
          <Link to={pageHref(currentPage + 1)} className="rounded-md px-3 py-1.5 text-sm hover:bg-neutral-100">
            Next
          </Link>
        ) : (
          <span className="cursor-not-allowed rounded-md px-3 py-1.5 text-sm text-neutral-300">Next</span>
        )}
      </div>
      <span className="text-xs text-neutral-500">
        Page {currentPage} of {totalPages}
      </span>
    </nav>
  )
}
