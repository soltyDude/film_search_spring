package com.example.reviewservice.controller;

import com.example.reviewservice.entity.ViewedMovie;
import com.example.reviewservice.service.ViewedMovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/viewed")
@RequiredArgsConstructor
public class ViewedMovieController {

    private final ViewedMovieService viewedMovieService;

    @PostMapping
    public ResponseEntity<ViewedMovie> addViewedMovie(
            @RequestParam Long userId,
            @RequestParam Long filmId
    ) {
        return ResponseEntity.ok(viewedMovieService.addViewedMovie(userId, filmId));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ViewedMovie>> getViewedMovies(@PathVariable Long userId) {
        return ResponseEntity.ok(viewedMovieService.getViewedMoviesByUser(userId));
    }
}
