package com.example.reviewservice.service;

import com.example.reviewservice.entity.ViewedMovie;
import com.example.reviewservice.repository.ViewedMovieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ViewedMovieService {

    private final ViewedMovieRepository viewedMovieRepository;

    public ViewedMovie addViewedMovie(Long userId, Long filmId) {
        if (viewedMovieRepository.existsByUserIdAndFilmId(userId, filmId)) {
            throw new IllegalArgumentException("ilm is already in whatched list");
        }

        ViewedMovie viewedMovie = ViewedMovie.builder()
                .userId(userId)
                .filmId(filmId)
                .build();

        return viewedMovieRepository.save(viewedMovie);
    }

    public List<ViewedMovie> getViewedMoviesByUser(Long userId) {
        return viewedMovieRepository.findAllByUserId(userId);
    }
}
