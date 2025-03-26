package com.example.playlistservice.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "playlist_film")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlaylistFilm {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "playlist_id", nullable = false)
    private Playlist playlist;

    // Здесь мы можем хранить просто filmId (связь с внешней сущностью "Film" из film-service)
    @Column(name = "film_id", nullable = false)
    private Long filmId;

    // Если хотите хранить еще что-то, добавляйте поля
}
