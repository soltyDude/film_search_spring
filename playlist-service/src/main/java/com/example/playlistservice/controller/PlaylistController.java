package com.example.playlistservice.controller;

import com.example.playlistservice.entity.Playlist;
import com.example.playlistservice.service.PlaylistService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/playlists")
@RequiredArgsConstructor
public class PlaylistController {

    private final PlaylistService playlistService;

    // 1) Создать плейлист
    @PostMapping
    public ResponseEntity<Playlist> createPlaylist(
            @RequestParam Long userId,
            @RequestParam String playlistName
    ) {
        Playlist created = playlistService.createPlaylist(userId, playlistName);
        return ResponseEntity.ok(created);
    }

    // 2) Получить все плейлисты пользователя
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Playlist>> getUserPlaylists(@PathVariable Long userId) {
        List<Playlist> playlists = playlistService.getUserPlaylists(userId);
        return ResponseEntity.ok(playlists);
    }

    // 3) Получить детали одного плейлиста
    @GetMapping("/{playlistId}")
    public ResponseEntity<Playlist> getPlaylistDetails(@PathVariable Long playlistId) {
        return playlistService.getPlaylistDetails(playlistId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 4) Удалить плейлист
    @DeleteMapping("/{playlistId}")
    public ResponseEntity<Void> deletePlaylist(@PathVariable Long playlistId) {
        playlistService.deletePlaylist(playlistId);
        return ResponseEntity.noContent().build();
    }

    // 5) Добавить фильм в плейлист
    @PostMapping("/{playlistId}/films")
    public ResponseEntity<?> addFilm(@PathVariable Long playlistId, @RequestParam Long filmId) {
        boolean added = playlistService.addFilmToPlaylist(playlistId, filmId);
        if (!added) {
            return ResponseEntity.badRequest().body("Film already in playlist");
        }
        return ResponseEntity.ok().build();
    }

    // 6) Удалить фильм из плейлиста
    @DeleteMapping("/{playlistId}/films/{filmId}")
    public ResponseEntity<?> removeFilm(
            @PathVariable Long playlistId,
            @PathVariable Long filmId
    ) {
        boolean removed = playlistService.removeFilmFromPlaylist(playlistId, filmId);
        if (!removed) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }
}
