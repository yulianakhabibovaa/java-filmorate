package ru.yandex.practicum.filmorate.exception;

public class UserNotFoundException extends NotFoundException {
    public UserNotFoundException(Long userId) {
        super("Пользователь с id " + userId + " не найден");
    }
}
