package ru.yandex.practicum.filmorate.storage.like;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmUserLikeStorage {
    void addLike(Long filmId, Long userId);

    void removeLike(Long filmId, Long userId);

    Collection<Film> getTopFilms(Integer count);
}
