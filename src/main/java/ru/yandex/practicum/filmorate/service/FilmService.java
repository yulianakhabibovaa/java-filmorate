package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Comparator;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    private static final LocalDate FIRST_FILM_DATE = LocalDate.of(1895, 12, 28);

    public Collection<Film> getAllFilms() {
        return filmStorage.getAll();
    }

    public Film saveNewFilm(Film film) {
        validateFilm(film);
        Film createdFilm = filmStorage.create(film);
        log.debug("был создан фильм: {}", createdFilm);
        return createdFilm;
    }

    public Film updateFilm(Film film) {
        if (film.getId() == null) {
            log.warn("Не был указан Id фильма");
            throw new ValidationException("Id должен быть указан");
        }

        validateFilm(film);
        Film updatedFilm = filmStorage.update(film);
        log.debug("фильм был обновлен: {}", updatedFilm);
        return updatedFilm;
    }

    public void addLike(Long filmId, Long userId) {
        Film film = filmStorage.get(filmId).orElseThrow(() -> new FilmNotFoundException(filmId));
        userStorage.get(userId).orElseThrow(() -> new UserNotFoundException(userId));

        film.getLikes().add(userId);
        filmStorage.update(film);
    }

    public void removeLike(Long filmId, Long userId) {
        Film film = filmStorage.get(filmId).orElseThrow(() -> new FilmNotFoundException(filmId));
        userStorage.get(userId).orElseThrow(() -> new UserNotFoundException(userId));

        film.getLikes().remove(userId);
        filmStorage.update(film);
    }

    public Collection<Film> getTopFilms(int count) {
        return filmStorage.getAll().stream().sorted(Comparator.comparingInt(f -> -f.getLikes().size())).limit(count).toList();
    }

    private void validateFilm(Film film) {
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
