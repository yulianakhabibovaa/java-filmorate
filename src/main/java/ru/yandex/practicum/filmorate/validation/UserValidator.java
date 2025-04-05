package ru.yandex.practicum.filmorate.validation;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

@Slf4j
@UtilityClass
public class UserValidator {

    public void validate(User user) {
        if (user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            log.warn("Имейл не прошел валидацию: {}", user.getEmail());
            throw new ValidationException("Имейл должен быть указан и начинаться с @");
        }

        if (user.getLogin() == null || user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            log.warn("Логин не прошел валидацию: {}", user.getLogin());
            throw new ValidationException("Логин не должен быть пустым и содержать пробелы");
        }

        if (user.getBirthday() == null || user.getBirthday().isAfter(LocalDate.now())) {
            log.warn("Дата рождения не прошла валидацию: {}", user.getBirthday());
            throw new ValidationException("Укажите правильно дату рождения");
        }
    }
}
