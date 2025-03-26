package com.example.filmservice.tmdb;

import com.example.filmservice.service.FilmService;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TMDBScheduler {

    private final TMDBClient tmdbClient;
    private final FilmService filmService;

    @Scheduled(fixedRate = 86400000) // каждый день (24 часа)
    public void fetchPopularMoviesDaily() {
        try {
            JsonObject popularMovies = tmdbClient.fetchPopularMovies();
            JsonArray results = popularMovies.getAsJsonArray("results");

            results.forEach(result -> {
                int apiId = result.getAsJsonObject().get("id").getAsInt();
                filmService.fetchAndSaveFilm(apiId);
            });
        } catch (Exception e) {
            System.err.println("failed to get popular films: " + e.getMessage());
        }
    }
}
