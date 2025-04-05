package ru.yandex.practicum.filmorate.validation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertThrows;

class FilmValidationTest {
    private Film film;

    @BeforeEach
    public void beforeEach() {
        film = new Film();
        film.setName("брат");
        film.setDescription("в чем сила?");
        film.setReleaseDate(LocalDate.of(1997, 3, 1));
        film.setDuration(120);
    }

    @Test
    void nameValidationTest() {
        film.setName("");
        makeFailedValidationAssertions();
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
        assertThrows(ValidationException.class, () -> FilmValidator.validate(film));
    }
}