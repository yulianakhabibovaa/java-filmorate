package ru.yandex.practicum.filmorate.storage.like;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.mapper.FilmMapper;

import java.util.Collection;

@Slf4j
@Repository
@RequiredArgsConstructor
public class FilmUserLikeDbStorage implements FilmUserLikeStorage {
    private static final String INSERT_SQL_QUERY = "INSERT INTO film_user_likes (film_id, user_id) " +
            "VALUES (?, ?)";
    private static final String DELETE_SQL_QUERY = "DELETE FROM film_user_likes " +
            "WHERE film_id = ? AND user_id = ?";
    private static final String SELECT_TOP_LIKED_FILMS_SQL_QUERY = "SELECT films.*, " +
            "mpa_ratings.id AS mpa_rating_id, " +
            "mpa_ratings.name AS mpa_rating_name, " +
            "mpa_ratings.description AS mpa_rating_description " +
            "FROM films " +
            "LEFT JOIN mpa_ratings ON films.mpa_rating = mpa_ratings.id " +
            "LEFT JOIN film_user_likes ON films.id = film_user_likes.film_id " +
            "GROUP BY films.id " +
            "ORDER BY COUNT(film_user_likes.user_id) DESC, films.id " +
            "LIMIT ?";
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void addLike(Long filmId, Long userId) {
        jdbcTemplate.update(INSERT_SQL_QUERY, filmId, userId);
    }

    @Override
    public void removeLike(Long filmId, Long userId) {
        jdbcTemplate.update(DELETE_SQL_QUERY, filmId, userId);
    }

    @Override
    public Collection<Film> getTopFilms(Integer count) {
        return jdbcTemplate.query(SELECT_TOP_LIKED_FILMS_SQL_QUERY, new FilmMapper(), count);
    }
}
