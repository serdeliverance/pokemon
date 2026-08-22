import { Link, Navigate, Route, Routes } from 'react-router-dom'
import { PokemonListPage } from './pages/PokemonListPage'
import { PokemonDetailsPage } from './pages/PokemonDetailsPage'

function NotFoundPage() {
  return (
    <div className="flex flex-col items-center gap-3 py-16 text-center">
      <h1 className="text-2xl font-semibold">Page not found</h1>
      <Link to="/pokemons" className="text-sm underline">
        Back to the pokemon list
      </Link>
    </div>
  )
}

function App() {
  return (
    <div className="min-h-screen bg-white text-neutral-900">
      <header className="border-b border-neutral-200 px-6 py-4">
        <Link to="/pokemons" className="text-lg font-bold">
          Pokedex
        </Link>
      </header>

      <main className="mx-auto max-w-5xl px-6 py-8">
        <Routes>
          <Route path="/" element={<Navigate to="/pokemons" replace />} />
          <Route path="/pokemons" element={<PokemonListPage />} />
          <Route path="/pokemons/:id" element={<PokemonDetailsPage />} />
          <Route path="*" element={<NotFoundPage />} />
        </Routes>
      </main>
    </div>
  )
}

export default App
