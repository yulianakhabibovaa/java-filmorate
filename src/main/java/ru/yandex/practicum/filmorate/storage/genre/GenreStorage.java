package ru.yandex.practicum.filmorate.storage.genre;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface GenreStorage {
    Collection<Genre> getAllGenres();

    Optional<Genre> getGenre(Integer id);

    Collection<Genre> getFilmGenres(Long filmId);

    Map<Long, Collection<Genre>> getGenresForFilms(Collection<Long> filmIds);

    void addFilmGenres(Long filmId, Set<Integer> genreIds);

    void updateGenres(Long filmId, Set<Integer> genreIds);

    Collection<Integer> checkNonExistentGenreIds(Set<Integer> inputIds);
}
