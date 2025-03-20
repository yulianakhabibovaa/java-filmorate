package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final Map<Long, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> findAll() {
        return users.values();
    }

    @PostMapping
    public User create(@RequestBody User user) {

        validate(user);
        if (user.getName() == null) {
            user.setName(user.getLogin());
        }

        user.setId(getNextId());
        users.put(user.getId(), user);
        log.debug("был создан пользователь: " + user);
        return user;
    }

    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);

        return ++currentMaxId;
    }

    @PutMapping
    public User update(@RequestBody User newUser) {

        if (newUser.getId() == null) {
            log.warn("Не был указан Id пользователя");
            throw new ValidationException("Id должен быть указан");
        }

        validate(newUser);
        if (users.containsKey(newUser.getId())) {
            User oldUser = users.get(newUser.getId());

            if (newUser.getName() == null) {
                oldUser.setName(newUser.getLogin());
            } else {
                oldUser.setName(newUser.getName());
            }

            oldUser.setLogin(newUser.getLogin());
            oldUser.setEmail(newUser.getEmail());
            oldUser.setBirthday((newUser.getBirthday()));

            log.debug("пользователь был обновлен: {}", oldUser);
            return oldUser;
        }
        log.warn("Пользователь с таким Id " + newUser.getId() + " не найден");
        throw new NotFoundException("Пользователь с таким Id " + newUser.getId() + " не найден");
    }

    private void validate(User user) {
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
