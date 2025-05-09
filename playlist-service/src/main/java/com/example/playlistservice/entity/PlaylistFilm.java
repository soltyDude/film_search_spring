package com.example.playlistservice.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

    // PlaylistFilm.java
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "playlist_id")
    @JsonIgnore          // 👉 ломаем цикл
    private Playlist playlist;


    // Здесь мы можем хранить просто filmId (связь с внешней сущностью "Film" из film-service)
    @Column(name = "film_id", nullable = false)
    private Long filmId;

    // Если хотите хранить еще что-то, добавляйте поля
}
