package com.example.filmservice.repository;

import com.example.filmservice.model.Film;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Repository
public interface FilmRepository extends JpaRepository<Film, Integer> {
    Optional<Film> findByApiId(int apiId);

    @Query(value = "SELECT * FROM film ORDER BY RANDOM() LIMIT 1", nativeQuery = true)
    Optional<Film> findRandomFilm();

    Page<Film> findAll(Pageable pageable);

    Page<Film> findByTitleContainingIgnoreCase(String title, Pageable pageable);

}
