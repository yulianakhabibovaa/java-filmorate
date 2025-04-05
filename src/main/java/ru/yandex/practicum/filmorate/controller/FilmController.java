package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.controller.dto.FilmCreateRequest;
import ru.yandex.practicum.filmorate.controller.dto.FilmDto;
import ru.yandex.practicum.filmorate.controller.dto.FilmUpdateRequest;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {
    private final FilmService filmService;

    @GetMapping
    public Collection<FilmDto> findAll() {
        return filmService.getAllFilms();
    }

    @GetMapping("/{id}")
    public FilmDto getFilmById(@PathVariable Long id) {
        return filmService.getFilm(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FilmDto create(@RequestBody FilmCreateRequest film) {
        return filmService.saveNewFilm(film);
    }

    @PutMapping
    public FilmDto update(@RequestBody FilmUpdateRequest film) {
        return filmService.updateFilm(film);
    }

    @GetMapping("/popular")
    public Collection<FilmDto> getTopFilms(@RequestParam(defaultValue = "10") int count) {
        return filmService.getTopFilms(count);
    }
}

