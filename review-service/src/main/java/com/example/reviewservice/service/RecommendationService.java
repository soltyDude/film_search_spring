package com.example.reviewservice.service;

import com.example.reviewservice.entity.ViewedMovie;
import com.example.reviewservice.repository.ReviewRepository;
import com.example.reviewservice.repository.ViewedMovieRepository;
import com.example.reviewservice.tmdb.TMDBClient;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final ViewedMovieRepository viewedMovieRepository;
    private final ReviewRepository reviewRepository;
    private final TMDBClient tmdbClient;

    public List<Map<String, String>> getRecommendations(int userId) {
        List<Map<String, String>> recommendations = new ArrayList<>();

        List<ViewedMovie> viewedMovies = viewedMovieRepository.findAllByUserId((long) userId);

        for (ViewedMovie viewedMovie : viewedMovies) {
            reviewRepository.findByUserIdAndFilmId((long) userId, viewedMovie.getFilmId()).ifPresent(review -> {
                int ratingWeight = ratingToWeight(review.getRating());
                int timeWeight = calculateTimeWeight(LocalDate.from(viewedMovie.getViewedAt()));

                if (ratingWeight > 0 && (within1_5Years(LocalDate.from(viewedMovie.getViewedAt())) || review.getRating() >= 9)) {
                    int similarCount = ratingWeight + timeWeight;
                    similarCount = Math.max(similarCount, 1); // Хотя бы 1 рекомендация

                    fetchAndAddSimilarMovies(recommendations, viewedMovie.getFilmId(), similarCount);
                }
            });
        }

        return recommendations;
    }

    private boolean within1_5Years(LocalDate viewedAt) {
        return ChronoUnit.MONTHS.between(viewedAt, LocalDate.now()) <= 18;
    }

    private int calculateTimeWeight(LocalDate viewedAt) {
        long monthsSinceViewed = ChronoUnit.MONTHS.between(viewedAt, LocalDate.now());
        if (monthsSinceViewed < 6) return 3;
        if (monthsSinceViewed < 12) return 2;
        if (monthsSinceViewed < 18) return 1;
        return 0;
    }

    private int ratingToWeight(int rating) {
        switch (rating) {
            case 10: return 6;
            case 9:  return 5;
            case 8:  return 4;
            case 7:  return 3;
            case 6:  return 2;
            case 5:  return 1;
            default: return 0;
        }
    }

    private void fetchAndAddSimilarMovies(List<Map<String, String>> recommendations, Long filmId, int count) {
        try {
            JsonObject response = tmdbClient.fetchSimilarMovies(filmId.intValue());
            JsonArray results = response.getAsJsonArray("results");

            int actualCount = Math.min(count, results.size());
            for (int i = 0; i < actualCount; i++) {
                JsonObject movie = results.get(i).getAsJsonObject();
                String movieId = movie.get("id").getAsString();

                if (recommendations.stream().noneMatch(m -> m.get("id").equals(movieId))) {
                    Map<String, String> movieData = new HashMap<>();
                    movieData.put("id", movieId);
                    movieData.put("title", movie.get("title").getAsString());
                    movieData.put("poster_url", movie.get("poster_path").isJsonNull() ? "" :
                            "https://image.tmdb.org/t/p/w500" + movie.get("poster_path").getAsString());
                    recommendations.add(movieData);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
