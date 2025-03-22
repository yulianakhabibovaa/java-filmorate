package ru.yandex.practicum.filmorate.exception;

public class FilmNotFoundException extends NotFoundException {
    public FilmNotFoundException(Long filmId) {
        super("Фильм с id " + filmId + " не найден");
    }
}
