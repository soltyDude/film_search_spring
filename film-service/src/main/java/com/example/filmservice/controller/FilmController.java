package com.example.filmservice.controller;

import com.example.filmservice.model.Film;
import com.example.filmservice.service.FilmService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/films")
public class FilmController {

    private final FilmService filmService;

    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @PostMapping
    public Film saveOrUpdateFilm(@RequestBody Film film) {
        return filmService.saveOrUpdateFilm(film);
    }

    @GetMapping("/paginated")
    public Page<Film> getFilmsPaginated(@RequestParam int page, @RequestParam int size) {
        return filmService.getFilmsPaginated(page, size);
    }

    @PostMapping("/fetch/{apiId}")
    public Film fetchAndSaveFilm(@PathVariable int apiId) {
        return filmService.fetchAndSaveFilm(apiId);
    }

    @GetMapping("/{filmId}/details")
    public Map<String, Object> getFilmDetails(@PathVariable int filmId) {
        return filmService.getFilmDetailsById(filmId);
    }

    @GetMapping("/search")
    public Page<Film> searchFilms(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return filmService.searchFilms(query, page, size);
    }

    @GetMapping("/search/simple")
    public List<Film> searchFilmsSimple(@RequestParam("query") String query) {
        return filmService.searchAndSaveFilmsByTitle(query);
    }

    @GetMapping("/random")
    public Film getRandomFilm() {
        return filmService.getRandomFilm();
    }

    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable int id) {
        return filmService.getFilmById(id);
    }

    @GetMapping("/api/{apiId}")
    public Film getFilmByApiId(@PathVariable int apiId) {
        return filmService.getFilmByApiId(apiId);
    }

    @GetMapping
    public List<Film> getAllFilms() {
        return filmService.getAllFilms();
    }

    @DeleteMapping("/{id}")
    public void deleteFilm(@PathVariable int id) {
        filmService.deleteFilm(id);
    }

    @PutMapping("/{id}/rating")
    public ResponseEntity<Void> updateRating(@PathVariable Long id, @RequestBody Map<String, Object> payload) {
        Double rating = (Double) payload.get("rating");
        Integer count = (Integer) payload.get("count");
        filmService.updateRatingAndCount(id, rating, count);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/exists/{id}")
    public ResponseEntity<Boolean> filmExists(@PathVariable int id) {
        boolean exists = filmService.filmExistsById(id);
        return ResponseEntity.ok(exists);
    }
}
