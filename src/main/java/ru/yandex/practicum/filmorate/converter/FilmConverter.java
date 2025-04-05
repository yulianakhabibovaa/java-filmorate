package ru.yandex.practicum.filmorate.converter;

import lombok.experimental.UtilityClass;
import ru.yandex.practicum.filmorate.controller.dto.FilmCreateRequest;
import ru.yandex.practicum.filmorate.controller.dto.FilmDto;
import ru.yandex.practicum.filmorate.controller.dto.FilmUpdateRequest;
import ru.yandex.practicum.filmorate.model.Film;

@UtilityClass
public class FilmConverter {
    public static Film mapToFilm(FilmCreateRequest request) {
        return Film.builder()
                .name(request.getName())
                .description(request.getDescription())
                .duration(request.getDuration())
                .releaseDate(request.getReleaseDate())
                .mpa(request.getMpa())
                .build();
    }

    public static Film mapToFilm(FilmUpdateRequest request) {
        return Film.builder()
                .id(request.getId())
                .name(request.getName())
                .description(request.getDescription())
                .duration(request.getDuration())
                .releaseDate(request.getReleaseDate())
                .mpa(request.getMpa())
                .build();
    }

    public static FilmDto mapToFilmDto(Film film) {
        return FilmDto.builder()
                .id(film.getId())
                .name(film.getName())
                .description(film.getDescription())
                .duration(film.getDuration())
                .releaseDate(film.getReleaseDate())
                .mpa(film.getMpa())
                .build();
    }
}
