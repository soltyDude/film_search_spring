package com.example.reviewservice.service;

import com.example.reviewservice.entity.ViewedMovie;
import com.example.reviewservice.repository.ViewedMovieRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ViewedMovieService {

    private final ViewedMovieRepository viewedMovieRepository;

    public ViewedMovie addViewedMovie(Long userId, Long filmId) {
        log.info("AAAAAAAAAAAAAAA === Начало метода addViewedMovie === userId={}, filmId={}", userId, filmId);

        if (viewedMovieRepository.existsByUserIdAndFilmId(userId, filmId)) {
            log.warn("AAAAAAAAAAAAAAA === Фильм уже в списке просмотренных! userId={}, filmId={}", userId, filmId);
            throw new IllegalArgumentException("Film is already in watched list");
        }

        log.info("AAAAAAAAAAAAAAA === Фильм НЕ найден в просмотренных, создаём запись для userId={}, filmId={}", userId, filmId);

        ViewedMovie viewedMovie = ViewedMovie.builder()
                .userId(userId)
                .filmId(filmId)
                .build();

        ViewedMovie saved = viewedMovieRepository.save(viewedMovie);

        log.info("AAAAAAAAAAAAAAA === Просмотренный фильм сохранён! ID новой записи = {} для userId={}, filmId={}",
                saved.getId(), userId, filmId);

        return saved;
    }

    public List<ViewedMovie> getViewedMoviesByUser(Long userId) {
        log.info("AAAAAAAAAAAAAAA === Получение просмотренных фильмов для userId={}", userId);
        List<ViewedMovie> list = viewedMovieRepository.findAllByUserId(userId);
        log.info("AAAAAAAAAAAAAAA === Найдено {} просмотренных фильмов для userId={}", list.size(), userId);
        return list;
    }
}
