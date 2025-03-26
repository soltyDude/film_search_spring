package com.example.filmservice.service;

import com.example.filmservice.tmdb.TMDBClient;
import com.example.filmservice.model.Film;
import com.example.filmservice.repository.FilmRepository;
import com.example.filmservice.util.TMDBApiUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class FilmService {

    private final FilmRepository filmRepository;
    private final TMDBClient tmdbClient; // Инжектируем TMDBClient

    public FilmService(FilmRepository filmRepository, TMDBClient tmdbClient) {
        this.filmRepository = filmRepository;
        this.tmdbClient = tmdbClient;
    }


    @Transactional
    public Film fetchAndSaveFilm(int apiId) {
        try {
            JsonObject movieDetails = tmdbClient.fetchMovieDetails(apiId);

            Film film = Film.builder()
                    .apiId(apiId)
                    .title(movieDetails.get("title").getAsString())
                    .releaseDate(LocalDate.parse(movieDetails.get("release_date").getAsString()))
                    .posterUrl("https://image.tmdb.org/t/p/w500" + movieDetails.get("poster_path").getAsString())
                    .runtime(movieDetails.get("runtime").getAsInt())
                    .apiRating(movieDetails.get("vote_average").getAsDouble())
                    .apiCount(movieDetails.get("vote_count").getAsInt())
                    .overview(movieDetails.get("overview").getAsString())
                    .build();

            return saveOrUpdateFilm(film);
        } catch (Exception e) {
            throw new RuntimeException("failed to get film from TMDB: " + e.getMessage());
        }
    }

    public Map<String, Object> getFilmDetailsById(int filmId) {
        return filmRepository.findById(filmId).map(film -> {
            Map<String, Object> details = new HashMap<>();
            details.put("title", film.getTitle());
            details.put("overview", film.getOverview());
            details.put("release_date", film.getReleaseDate());
            details.put("poster_url", film.getPosterUrl());
            details.put("api_rating", film.getApiRating());
            details.put("rating", film.getRating());
            return details;
        }).orElse(null);
    }

    public Film getRandomFilm() {
        return filmRepository.findRandomFilm().orElse(null);
    }


    public Page<Film> getFilmsPaginated(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return filmRepository.findAll(pageable);
    }

    @Transactional
    public Film saveOrUpdateFilm(Film film) {
        filmRepository.findByApiId(film.getApiId()).ifPresent(existingFilm -> {
            film.setId(existingFilm.getId());
        });
        return filmRepository.save(film);
    }

    public Film getFilmByApiId(int apiId) {
        return filmRepository.findByApiId(apiId).orElse(null);
    }

    public Film getFilmById(int id) {
        return filmRepository.findById(id).orElse(null);
    }

    public List<Film> getAllFilms() {
        return filmRepository.findAll();
    }

    public void deleteFilm(int id) {
        filmRepository.deleteById(id);
    }

    public Page<Film> searchFilms(String query, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Film> films = filmRepository.findByTitleContainingIgnoreCase(query, pageable);

        // Если фильмы найдены в БД, возвращаем их
        if (!films.isEmpty()) {
            return films;
        }

        // Если фильмов нет в БД, загружаем их из TMDB API
        JsonObject jsonObject = TMDBApiUtil.fetchMoviesByQuery(query);
        JsonArray results = jsonObject.getAsJsonArray("results");

        for (int i = 0; i < results.size(); i++) {
            JsonObject movie = results.get(i).getAsJsonObject();
            int apiId = movie.get("id").getAsInt();
            fetchAndSaveFilm(apiId); // Сохраняем фильм в БД
        }

        // Повторно ищем в БД после загрузки
        return filmRepository.findByTitleContainingIgnoreCase(query, pageable);
    }

    public void updateRatingAndCount(Long filmId, Double rating, Integer count) {
        Film film = filmRepository.findById(Math.toIntExact(filmId))
                .orElseThrow(() -> new RuntimeException("Film not found"));
        film.setRating(rating);
        film.setCount(count);
        filmRepository.save(film);
    }

    public boolean filmExistsById(int id) {
        return filmRepository.existsById(id);
    }

    @Transactional
    public List<Film> searchAndSaveFilmsByTitle(String query) {
        try {
            JsonObject jsonObject = tmdbClient.searchMovies(query);
            JsonArray results = jsonObject.getAsJsonArray("results");
            List<Film> films = new ArrayList<>();

            for (int i = 0; i < results.size(); i++) {
                JsonObject movie = results.get(i).getAsJsonObject();
                int apiId = movie.get("id").getAsInt();

                Film film = filmRepository.findByApiId(apiId).orElseGet(() -> {
                    try {
                        // дополнительный запрос для получения runtime
                        JsonObject detailedMovie = tmdbClient.fetchMovieDetails(apiId);

                        Film detailedFilm = Film.builder()
                                .apiId(apiId)
                                .title(detailedMovie.get("title").getAsString())
                                .releaseDate(detailedMovie.get("release_date").isJsonNull() ?
                                        LocalDate.now() : LocalDate.parse(detailedMovie.get("release_date").getAsString()))
                                .posterUrl(detailedMovie.get("poster_path").isJsonNull() ?
                                        "" : "https://image.tmdb.org/t/p/w500" + detailedMovie.get("poster_path").getAsString())
                                .runtime(detailedMovie.get("runtime").getAsInt())
                                .apiRating(detailedMovie.get("vote_average").getAsDouble())
                                .apiCount(detailedMovie.get("vote_count").getAsInt())
                                .overview(detailedMovie.get("overview").isJsonNull() ? "" : detailedMovie.get("overview").getAsString())
                                .build();

                        return filmRepository.save(detailedFilm);
                    } catch (Exception e) {
                        throw new RuntimeException("failed to get film details " + e.getMessage());
                    }
                });

                films.add(film);
            }

            return films;

        } catch (Exception e) {
            throw new RuntimeException("error searching film  " + e.getMessage());
        }
    }


}
