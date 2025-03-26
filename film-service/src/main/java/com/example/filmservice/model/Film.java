package com.example.filmservice.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "film")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Film {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 300)
    private String title;

    @Column(name = "release_date", nullable = false)
    private LocalDate releaseDate;

    @Column(name = "poster_url", nullable = false, columnDefinition = "TEXT")
    private String posterUrl;

    @Column(name = "api_id", unique = true, nullable = false)
    private int apiId;

    @Column(nullable = false)
    private int runtime;

    @Column(name = "api_rating", nullable = false)
    private double apiRating;

    @Column
    private Double rating;

    @Column(name = "api_count", nullable = false)
    private int apiCount;

    @Builder.Default
    @Column(nullable = false)
    private int count = 0;

    @Column(columnDefinition = "TEXT")
    private String overview;
}

