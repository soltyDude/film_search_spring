package com.example.reviewservice.repository;

import com.example.reviewservice.entity.ViewedMovie;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ViewedMovieRepository extends JpaRepository<ViewedMovie, Long> {

    boolean existsByUserIdAndFilmId(Long userId, Long filmId);

    Optional<ViewedMovie> findByUserIdAndFilmId(Long userId, Long filmId);

    List<ViewedMovie> findAllByUserId(Long userId);
}
