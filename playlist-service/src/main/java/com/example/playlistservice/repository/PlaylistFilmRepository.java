package com.example.playlistservice.repository;

import com.example.playlistservice.entity.PlaylistFilm;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PlaylistFilmRepository extends JpaRepository<PlaylistFilm, Long> {

    // Проверить, не добавлен ли уже фильм в плейлист
    Optional<PlaylistFilm> findByPlaylistIdAndFilmId(Long playlistId, Long filmId);
}
