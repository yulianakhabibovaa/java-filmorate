package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class FilmControllerTest {
    private FilmController filmController;
    private Film film;

    @BeforeEach
    public void beforeEach() {
        filmController = new FilmController();
        film = new Film();
        film.setName("брат");
        film.setDescription("в чем сила?");
        film.setReleaseDate(LocalDate.of(1997, 03, 01));
        film.setDuration(120);
        filmController.create(film);
    }

    @Test
    void nameValidationTest() {
        film.setName("");
        assertThrows(ValidationException.class, () -> filmController.create(film));
        assertEquals(1, filmController.findAll().size());
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
        assertThrows(ValidationException.class, () -> filmController.create(film));
        assertEquals(1, filmController.findAll().size());
    }
}