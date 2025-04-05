package ru.yandex.practicum.filmorate.controller.dto;

import lombok.Data;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.time.LocalDate;
import java.util.Set;

@Data
public class FilmCreateRequest {
    private String name;
    private String description;
    private LocalDate releaseDate;
    private Integer duration;
    private MpaRating mpa;
    private Set<Genre> genres;
}
