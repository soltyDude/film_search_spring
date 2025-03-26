package com.example.playlistservice.service;

import com.example.playlistservice.entity.Playlist;
import com.example.playlistservice.entity.PlaylistFilm;
import com.example.playlistservice.repository.PlaylistFilmRepository;
import com.example.playlistservice.repository.PlaylistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PlaylistService {

    private final PlaylistRepository playlistRepository;
    private final PlaylistFilmRepository playlistFilmRepository;

    // Создать плейлист
    public Playlist createPlaylist(Long userId, String playlistName) {
        Playlist playlist = Playlist.builder()
                .userId(userId)
                .name(playlistName)
                .build();
        return playlistRepository.save(playlist);
    }

    // Получить плейлисты по userId
    public List<Playlist> getUserPlaylists(Long userId) {
        return playlistRepository.findByUserId(userId);
    }

    // Получить детали одного плейлиста
    public Optional<Playlist> getPlaylistDetails(Long playlistId) {
        return playlistRepository.findById(playlistId);
    }

    // Удалить плейлист
    public void deletePlaylist(Long playlistId) {
        playlistRepository.deleteById(playlistId);
    }

    // Добавить фильм в плейлист
    public boolean addFilmToPlaylist(Long playlistId, Long filmId) {
        // Сначала проверим, существует ли запись
        Optional<PlaylistFilm> existing = playlistFilmRepository.findByPlaylistIdAndFilmId(playlistId, filmId);
        if (existing.isPresent()) {
            // Уже есть
            return false;
        }
        // Проверяем, есть ли сам плейлист
        Optional<Playlist> plOpt = playlistRepository.findById(playlistId);
        if (plOpt.isEmpty()) {
            throw new IllegalArgumentException("Playlist not found with id: " + playlistId);
        }

        // Создаём связь
        PlaylistFilm pf = PlaylistFilm.builder()
                .playlist(plOpt.get())
                .filmId(filmId)
                .build();
        playlistFilmRepository.save(pf);
        return true;
    }

    // Удалить фильм из плейлиста
    public boolean removeFilmFromPlaylist(Long playlistId, Long filmId) {
        // Находим связь
        Optional<PlaylistFilm> existing = playlistFilmRepository.findByPlaylistIdAndFilmId(playlistId, filmId);
        if (existing.isEmpty()) {
            // нет такой записи
            return false;
        }
        playlistFilmRepository.delete(existing.get());
        return true;
    }
}
