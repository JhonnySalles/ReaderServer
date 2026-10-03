import { BrowserRouter, Routes, Route } from 'react-router-dom';
import { MainLayout } from './components/layout/MainLayout';
import { Home } from './pages/Home/Home';
import { Books } from './pages/Books/Books';
import { Mangas } from './pages/Mangas/Mangas';

export function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<MainLayout />}>
          <Route index element={<Home />} />
          <Route path="books" element={<Books />} />
          <Route path="mangas" element={<Mangas />} />
        </Route>
      </Routes>
    </BrowserRouter>
  );
}

export default App;
