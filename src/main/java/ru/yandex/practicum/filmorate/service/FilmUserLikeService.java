package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.like.FilmUserLikeStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

@Service
@RequiredArgsConstructor
public class FilmUserLikeService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final FilmUserLikeStorage likeStorage;

    public void addLike(Long filmId, Long userId) {
        validatePresence(filmId, userId);

        likeStorage.addLike(filmId, userId);
    }

    public void removeLike(Long filmId, Long userId) {
        validatePresence(filmId, userId);

        likeStorage.removeLike(filmId, userId);
    }

    private void validatePresence(Long filmId, Long userId) {
        if (filmStorage.get(filmId).isEmpty()) {
            throw new FilmNotFoundException(filmId);
        }
        if (userStorage.get(userId).isEmpty()) {
            throw new UserNotFoundException(userId);
        }
    }
}
