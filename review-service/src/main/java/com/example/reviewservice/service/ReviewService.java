package com.example.reviewservice.service;

import com.example.reviewservice.entity.Review;
import com.example.reviewservice.entity.ViewedMovie;
import com.example.reviewservice.kafka.KafkaProducer;
import com.example.reviewservice.repository.ReviewRepository;
import com.example.reviewservice.repository.ViewedMovieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ViewedMovieRepository viewedMovieRepository;
    private final KafkaProducer kafkaProducer;
    private final RestTemplate restTemplate;   // Bean уже есть (@LoadBalanced оставлять не обязательно)

    /* ----------------‑‑‑ CRUD ‑‑‑---------------- */

    public Review createReview(Review review) {

        reviewRepository.findByUserIdAndFilmId(review.getUserId(), review.getFilmId())
                .ifPresent(r -> { throw new IllegalArgumentException("Review already exists"); });

        if (!userExists(review.getUserId()))
            throw new IllegalArgumentException("User not found");

        if (!filmExists(review.getFilmId()))
            throw new IllegalArgumentException("Film not found");

        if (!viewedMovieRepository.existsByUserIdAndFilmId(review.getUserId(), review.getFilmId()))
            throw new IllegalArgumentException("The film was not viewed by the user");

        Review saved = reviewRepository.save(review);

        viewedMovieRepository.findByUserIdAndFilmId(review.getUserId(), review.getFilmId())
                .ifPresent(vm -> {
                    vm.setReviewId(saved.getId());
                    viewedMovieRepository.save(vm);
                });

        sendRatingUpdateToKafka(saved.getFilmId());
        return saved;
    }

    public Review updateReview(Review review) {
        Review existing = reviewRepository.findById(review.getId())
                .orElseThrow(() -> new IllegalArgumentException("Review not found"));
        existing.setRating(review.getRating());
        existing.setReviewText(review.getReviewText());

        Review updated = reviewRepository.save(existing);
        sendRatingUpdateToKafka(updated.getFilmId());
        return updated;
    }

    public void deleteReview(Long id) {
        reviewRepository.findById(id).ifPresent(r -> {
            reviewRepository.deleteById(id);
            sendRatingUpdateToKafka(r.getFilmId());
        });
    }

    public List<Review> getReviewsByFilm(Long filmId) {
        return reviewRepository.findByFilmId(filmId);
    }

    /* ----------------‑‑‑ helpers ‑‑‑---------------- */

    private void sendRatingUpdateToKafka(Long filmId) {
        List<Review> reviews = reviewRepository.findByFilmId(filmId);
        double avg = reviews.stream().mapToInt(Review::getRating).average().orElse(0.0);
        kafkaProducer.sendRatingUpdate(filmId, avg, reviews.size());
    }

    /* ----- Здесь только прямые вызовы контейнеров ----- */

    private boolean userExists(Long userId) {
        try {
            //  user‑service слушает на 8082 (см. docker‑compose)
            String url = "http://user-service:8082/users/exists/" + userId;
            Boolean body = restTemplate.getForObject(url, Boolean.class);
            return Boolean.TRUE.equals(body);
        } catch (Exception ex) {
            return false;
        }
    }

    private boolean filmExists(Long filmId) {
        try {
            //  film‑service слушает на 8080
            String url = "http://film-service:8080/films/exists/" + filmId;
            Boolean body = restTemplate.getForObject(url, Boolean.class);
            return Boolean.TRUE.equals(body);
        } catch (Exception ex) {
            return false;
        }
    }
}
