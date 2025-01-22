package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private final Map<Long, Film> films = new HashMap<>();
    private static final LocalDate FIRST_FILM_DATE = LocalDate.of(1895, 12, 28);

    @GetMapping
    public Collection<Film> findAll() {
        return films.values();
    }

    @PostMapping
    public Film create(@RequestBody Film film) {
        validate(film);
        film.setId(getNextId());
        films.put(film.getId(), film);
        log.debug("был создан фильм: {}", film);
        return film;
    }

    @PutMapping
    public Film update(@RequestBody Film newFilm) {
        if (newFilm.getId() == null) {
            log.warn("Не был указан Id фильма");
            throw new ValidationException("Id должен быть указан");
        }

        validate(newFilm);
        if (films.containsKey(newFilm.getId())) {
            Film oldFilm = films.get(newFilm.getId());
            oldFilm.setName(newFilm.getName());
            oldFilm.setDescription(newFilm.getDescription());
            oldFilm.setReleaseDate(newFilm.getReleaseDate());
            oldFilm.setDuration(newFilm.getDuration());
            log.debug("фильм был обновлен: {}", oldFilm);
            return oldFilm;
        }
        log.warn("Фильм с id = {} не найден", newFilm.getId());
        throw new NotFoundException("Фильм с id = " + newFilm.getId() + " не найден");
    }

    private long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    private void validate(Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            log.warn("Название не прошло валидацию: {}", film.getName());
            throw new ValidationException("Название не может быть пустым");
        }
        if (film.getDescription() == null || film.getDescription().isBlank() || film.getDescription().length() > 200) {
            log.warn("Описание не прошло валидацию: {}", film.getDescription());
            throw new ValidationException("Описание превышает 200 символов");
        }
        if (film.getReleaseDate() == null || film.getReleaseDate().isBefore(FIRST_FILM_DATE)) {
            log.warn("Дата релиза не прошла валидацию: {}", film.getReleaseDate());
            throw new ValidationException("дата релиза не может быть раньше 28 декабря 1895 года");
        }
        if (film.getDuration() == null || film.getDuration() <= 0) {
            log.warn("Продолжительность фильма не прошла валидацию {}", film.getDuration());
            throw new ValidationException("Продолжительность фильма должна быть положительным числом");
        }
    }

}

