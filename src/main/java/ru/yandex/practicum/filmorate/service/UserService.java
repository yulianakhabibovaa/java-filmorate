package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;

    public Collection<User> getAllUsers() {
        return userStorage.getAll();
    }

    public User createUser(User user) {
        validateUser(user);
        if (user.getName() == null) {
            user.setName(user.getLogin());
        }
        User createdUser = userStorage.create(user);
        log.debug("был создан пользователь: {}", createdUser);
        return createdUser;
    }

    public User updateUser(User user) {
        if (user.getId() == null) {
            log.warn("Не был указан Id пользователя");
            throw new ValidationException("Id должен быть указан");
        }

        validateUser(user);
        User updatedUser = userStorage.update(user);
        log.debug("пользователь был обновлен: {}", updatedUser);
        return updatedUser;
    }

    public Collection<User> getFriends(Long userId) {
        User user = userStorage.get(userId).orElseThrow(() -> new UserNotFoundException(userId));
        return user.getFriends().stream()
                .map(userStorage::get)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toSet());
    }

    public void addFriend(Long userId, Long friendId) {
        User user = userStorage.get(userId).orElseThrow(() -> new UserNotFoundException(userId));
        User friend = userStorage.get(friendId).orElseThrow(() -> new UserNotFoundException(userId));

        user.getFriends().add(friendId);
        friend.getFriends().add(userId);

        userStorage.update(user);
        userStorage.update(friend);
    }

    public void removeFriend(Long userId, Long friendId) {
        User user = userStorage.get(userId).orElseThrow(() -> new UserNotFoundException(userId));
        User friend = userStorage.get(friendId).orElseThrow(() -> new UserNotFoundException(userId));

        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);

        userStorage.update(user);
        userStorage.update(friend);
    }

    public Set<User> getCommonFriends(Long userId, Long otherUserId) {
        User user = userStorage.get(userId).orElseThrow(() -> new UserNotFoundException(userId));
        User otherUser = userStorage.get(otherUserId).orElseThrow(() -> new UserNotFoundException(otherUserId));

        Set<Long> commonFriends = new HashSet<>(user.getFriends());
        commonFriends.retainAll(otherUser.getFriends());

        return commonFriends.stream()
                .map(userStorage::get)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toSet());
    }

    private void validateUser(User user) {
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
