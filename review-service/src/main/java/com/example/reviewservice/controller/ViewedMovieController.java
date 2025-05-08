package com.example.reviewservice.controller;

import com.example.reviewservice.entity.ViewedMovie;
import com.example.reviewservice.service.ViewedMovieService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/viewed")
@RequiredArgsConstructor
@Slf4j
public class ViewedMovieController {

    private final ViewedMovieService viewedMovieService;

    @PostMapping
    public ResponseEntity<ViewedMovie> addViewedMovie(
            @RequestParam Long userId,
            @RequestParam Long filmId
    ) {
        log.info("BBBBBBBBBBBBBBBB === Получен POST запрос на добавление просмотренного фильма userId={}, filmId={}", userId, filmId);
        ViewedMovie vm = viewedMovieService.addViewedMovie(userId, filmId);
        log.info("BBBBBBBBBBBBBBBB === Просмотренный фильм успешно добавлен! ID={}", vm.getId());
        return ResponseEntity.ok(vm);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ViewedMovie>> getViewedMovies(@PathVariable Long userId) {
        log.info("BBBBBBBBBBBBBBBB === Получен GET запрос на получение просмотренных фильмов userId={}", userId);
        List<ViewedMovie> list = viewedMovieService.getViewedMoviesByUser(userId);
        log.info("BBBBBBBBBBBBBBBB === Отправлено {} просмотренных фильмов", list.size());
        return ResponseEntity.ok(list);
    }
}
