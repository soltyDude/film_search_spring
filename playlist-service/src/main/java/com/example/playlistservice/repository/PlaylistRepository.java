package com.example.playlistservice.repository;

import com.example.playlistservice.entity.Playlist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlaylistRepository extends JpaRepository<Playlist, Long> {

    // Получить все плейлисты конкретного пользователя
    List<Playlist> findByUserId(Long userId);
}
