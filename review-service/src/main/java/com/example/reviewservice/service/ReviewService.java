package com.example.reviewservice.service;

import com.example.reviewservice.entity.Review;
import com.example.reviewservice.entity.ViewedMovie;
import com.example.reviewservice.kafka.KafkaProducer;
import com.example.reviewservice.repository.ReviewRepository;
import com.example.reviewservice.repository.ViewedMovieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final RestTemplate restTemplate;
    private final ViewedMovieRepository viewedMovieRepository;
    private final KafkaProducer kafkaProducer;

    @Value("${gateway.serviceName}")
    private String gatewayServiceName;

    public Review createReview(Review review) {
        if (reviewRepository.findByUserIdAndFilmId(review.getUserId(), review.getFilmId()).isPresent()) {
            throw new IllegalArgumentException("Review already exists");
        }

        if (!isUserExists(review.getUserId()) || !isFilmExists(review.getFilmId())) {
            throw new IllegalArgumentException("User or film not found");
        }

        if (!viewedMovieRepository.existsByUserIdAndFilmId(review.getUserId(), review.getFilmId())) {
            throw new IllegalArgumentException("The film was not viewed by the user");
        }

        Review savedReview = reviewRepository.save(review);

        ViewedMovie viewedMovie = viewedMovieRepository
                .findByUserIdAndFilmId(review.getUserId(), review.getFilmId())
                .orElseThrow(() -> new RuntimeException("Viewed movie not found"));

        viewedMovie.setReviewId(savedReview.getId());
        viewedMovieRepository.save(viewedMovie);

        sendRatingUpdateToKafka(savedReview.getFilmId());
        return savedReview;
    }

    public Review updateReview(Review review) {
        Optional<Review> existing = reviewRepository.findById(review.getId());
        if (existing.isEmpty()) {
            throw new IllegalArgumentException("Review not found with id: " + review.getId());
        }

        Review updated = reviewRepository.save(review);

        sendRatingUpdateToKafka(updated.getFilmId());
        return updated;
    }

    public void deleteReview(Long id) {
        Optional<Review> existing = reviewRepository.findById(id);
        if (existing.isEmpty()) return;

        Long filmId = existing.get().getFilmId();
        reviewRepository.deleteById(id);

        sendRatingUpdateToKafka(filmId);
    }

    public List<Review> getReviewsByFilm(Long filmId) {
        return reviewRepository.findByFilmId(filmId);
    }

    // как уебан, но не хочу делать запрос в фильм сервис, потом исправить
    private void sendRatingUpdateToKafka(Long filmId) {
        List<Review> reviews = reviewRepository.findByFilmId(filmId);
        double average = reviews.stream().mapToInt(Review::getRating).average().orElse(0.0);
        int count = reviews.size();

        kafkaProducer.sendRatingUpdate(filmId, average, count);
    }

    private boolean isUserExists(Long userId) {
        String url = "http://" + gatewayServiceName + "/user-service/users/exists/" + userId;
        try {
            ResponseEntity<Boolean> response = restTemplate.getForEntity(url, Boolean.class);
            return (response.getStatusCode() == HttpStatus.OK) && Boolean.TRUE.equals(response.getBody());
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isFilmExists(Long filmId) {
        String url = "http://" + gatewayServiceName + "/film-service/films/exists/" + filmId;
        try {
            ResponseEntity<Boolean> response = restTemplate.getForEntity(url, Boolean.class);
            return (response.getStatusCode() == HttpStatus.OK) && Boolean.TRUE.equals(response.getBody());
        } catch (Exception e) {
            return false;
        }
    }
}