import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import LoginPage from './components/LoginPage';
import RegisterPage from './components/RegisterPage';
import FilmsPage from './components/FilmsPage';
import FilmDetailsPage from './components/FilmDetailsPage';
import ViewedFilmsPage from './components/ViewedFilmsPage';
import HomePage from './components/HomePage';
import PlaylistsPage from "./components/PlaylistsPage";
import PlaylistDetailsPage from "./components/PlaylistDetailsPage";
import RecommendationsPage from "./components/RecommendationsPage";
import RandomFilmPage from "./components/RandomFilmPage"


function App() {
    return (
        <Router>
            <Routes>
                <Route path="/" element={<LoginPage />} />
                <Route path="/register" element={<RegisterPage />} />
                <Route path="/home" element={<HomePage />} />
                <Route path="/films" element={<FilmsPage />} />
                <Route path="/films/:id" element={<FilmDetailsPage />} />
                <Route path="/viewed" element={<ViewedFilmsPage />} />
                <Route path="/my-playlists"      element={<PlaylistsPage />} />
                <Route path="/playlists/:pid"    element={<PlaylistDetailsPage />} />
                <Route path="/recommendations" element={<RecommendationsPage />} />
                <Route path="/random" element={<RandomFilmPage />} />
            </Routes>
        </Router>
    );
}

export default App;
