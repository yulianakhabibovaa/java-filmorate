package ru.yandex.practicum.filmorate.validation;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

@Slf4j
@UtilityClass
public class FilmValidator {

    private static final LocalDate FIRST_FILM_DATE = LocalDate.of(1895, 12, 28);

    public void validate(Film film) {
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
