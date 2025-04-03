package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FilmValidationTest {
    private FilmService filmService;
    private Film film;

    @BeforeEach
    public void beforeEach() {
        FilmStorage filmStorage = new InMemoryFilmStorage();
        UserStorage userStorage = new InMemoryUserStorage();
        filmService = new FilmService(filmStorage, userStorage);
        film = new Film();
        film.setName("брат");
        film.setDescription("в чем сила?");
        film.setReleaseDate(LocalDate.of(1997, 3, 1));
        film.setDuration(120);
        filmService.saveNewFilm(film);
    }

    @Test
    void nameValidationTest() {
        film.setName("");
        assertThrows(ValidationException.class, () -> filmService.saveNewFilm(film));
        assertEquals(1, filmService.getAllFilms().size());
    }

    @Test
    void descriptionValidationTest() {
        film.setDescription("оченьдлинноеописание".repeat(11));
        makeFailedValidationAssertions();
        film.setDescription("");
        makeFailedValidationAssertions();
        film.setDescription(null);
        makeFailedValidationAssertions();
    }

    @Test
    void dateValidationTest() {
        film.setReleaseDate(LocalDate.of(1895, 12, 27));
        makeFailedValidationAssertions();
        film.setReleaseDate(null);
        makeFailedValidationAssertions();
    }

    @Test
    void durationValidationTest() {
        film.setDuration(0);
        makeFailedValidationAssertions();
        film.setDuration(null);
        makeFailedValidationAssertions();
    }

    private void makeFailedValidationAssertions() {
        assertThrows(ValidationException.class, () -> filmService.saveNewFilm(film));
        assertEquals(1, filmService.getAllFilms().size());
    }
}