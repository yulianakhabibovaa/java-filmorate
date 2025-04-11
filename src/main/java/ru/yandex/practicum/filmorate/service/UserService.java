package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.friendship.UserFriendshipStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.validation.UserValidator;

import java.util.Collection;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;
    private final UserFriendshipStorage friendshipStorage;

    public Collection<User> getAllUsers() {
        return userStorage.getAll();
    }

    public User createUser(User user) {
        UserValidator.validate(user);
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

        UserValidator.validate(user);
        User updatedUser = userStorage.update(user);
        log.debug("пользователь был обновлен: {}", updatedUser);
        return updatedUser;
    }

    public Collection<User> getFriends(Long userId) {
        User user = userStorage.get(userId).orElseThrow(() -> new UserNotFoundException(userId));
        return friendshipStorage.getFriends(user.getId());
    }

    public void addFriend(Long userId, Long friendId) {
        validateExistence(userId, friendId);
        friendshipStorage.addFriend(userId, friendId);
    }

    public void removeFriend(Long userId, Long friendId) {
        validateExistence(userId, friendId);
        friendshipStorage.removeFriend(userId, friendId);
    }

    public Collection<User> getCommonFriends(Long userId, Long otherUserId) {
        validateExistence(userId, otherUserId);
        return friendshipStorage.getMutualFriends(userId, otherUserId);
    }

    private void validateExistence(Long userId, Long otherUserId) {
        if (userStorage.get(userId).isEmpty()) {
            throw new UserNotFoundException(userId);
        }
        if (userStorage.get(otherUserId).isEmpty()) {
            throw new UserNotFoundException(otherUserId);
        }
    }
}
