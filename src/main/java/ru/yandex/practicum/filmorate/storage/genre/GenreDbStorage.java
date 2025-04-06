package ru.yandex.practicum.filmorate.storage.genre;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.mapper.GenreMapper;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class GenreDbStorage implements GenreStorage {
    private static final String SELECT_ALL_SQL_QUERY = "SELECT * FROM genres ORDER BY id";
    private static final String SELECT_BY_ID_SQL_QUERY = "SELECT * FROM genres WHERE id = ?";
    private static final String SELECT_GENRES_BY_FILM_ID = "SELECT genres.* " +
            "FROM film_genres " +
            "LEFT JOIN genres ON film_genres.genre_id = genres.id " +
            "WHERE film_id = ? " +
            "ORDER BY genres.id";
    private static final String INSERT_FILM_GENRE = "INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)";
    private static final String DELETE_FILM_GENRES = "DELETE FROM film_genres WHERE film_id = ?";
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Collection<Genre> getAllGenres() {
        return jdbcTemplate.query(SELECT_ALL_SQL_QUERY, new GenreMapper());
    }

    @Override
    public Optional<Genre> getGenre(Integer id) {
        return jdbcTemplate.query(SELECT_BY_ID_SQL_QUERY, new GenreMapper(), id)
                .stream().findAny();
    }

    @Override
    public Collection<Genre> getFilmGenres(Long filmId) {
        return jdbcTemplate.query(SELECT_GENRES_BY_FILM_ID, new GenreMapper(), filmId);
    }

    @Override
    public Map<Long, Collection<Genre>> getGenresForFilms(Collection<Long> filmIds) {
        if (filmIds.isEmpty()) {
            return Collections.emptyMap();
        }

        String query = "SELECT film_genres.film_id, genres.id AS genre_id, genres.name AS genre_name " +
                "FROM film_genres " +
                "JOIN genres ON film_genres.genre_id = genres.id " +
                "WHERE film_genres.film_id IN (" +
                String.join(",", Collections.nCopies(filmIds.size(), "?")) + ")";

        Map<Long, Collection<Genre>> result = new HashMap<>();

        jdbcTemplate.query(query, rs -> {
            Long filmId = rs.getLong("film_id");
            Genre genre = new Genre(
                    rs.getInt("genre_id"),
                    rs.getString("genre_name")
            );

            result.computeIfAbsent(filmId, k -> new ArrayList<>())
                    .add(genre);
        }, filmIds.toArray());

        return result;
    }

    @Override
    public void addFilmGenres(Long filmId, Set<Integer> genreIds) {
        if (genreIds == null || genreIds.isEmpty()) {
            return;
        }

        jdbcTemplate.batchUpdate(INSERT_FILM_GENRE, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(@NonNull PreparedStatement ps, int i) throws SQLException {
                Integer genreId = (Integer) genreIds.toArray()[i];
                ps.setLong(1, filmId);
                ps.setInt(2, genreId);
            }

            @Override
            public int getBatchSize() {
                return genreIds.size();
            }
        });
    }

    @Override
    @Transactional
    public void updateGenres(Long filmId, Set<Integer> genreIds) {
        jdbcTemplate.update(DELETE_FILM_GENRES, filmId);
        addFilmGenres(filmId, genreIds);
    }

    @Override
    public Collection<Integer> checkNonExistentGenreIds(Set<Integer> inputIds) {
        if (inputIds.isEmpty()) {
            return Collections.emptyList();
        }

        String values = inputIds.stream()
                .map(id -> "(" + id + ")")
                .collect(Collectors.joining(", "));

        String sql = "SELECT input_ids.id " +
                "FROM (VALUES " + values + ") AS input_ids(id) " +
                "LEFT JOIN genres ON genres.id = input_ids.id " +
                "WHERE genres.id IS NULL";

        return jdbcTemplate.query(sql, (rs, rowNum) -> rs.getInt("id"));
    }
}
