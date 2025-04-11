package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.mapper.FilmMapper;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Types;
import java.util.Collection;
import java.util.Optional;

@Slf4j
@Repository("filmDbStorage")
@Primary
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {
    private static final String SELECT_ALL_SQL_QUERY = "SELECT " +
            "films.*, " +
            "mpa_ratings.id AS mpa_rating_id, " +
            "mpa_ratings.name AS mpa_rating_name, " +
            "mpa_ratings.description AS mpa_rating_description " +
            "FROM films " +
            "LEFT JOIN mpa_ratings ON films.mpa_rating = mpa_ratings.id " +
            "ORDER BY films.id";
    private static final String SELECT_BY_ID_SQL_QUERY = "SELECT films.*, " +
            "mpa_ratings.id AS mpa_rating_id, " +
            "mpa_ratings.name AS mpa_rating_name, " +
            "mpa_ratings.description AS mpa_rating_description " +
            "FROM films " +
            "LEFT JOIN mpa_ratings ON films.mpa_rating = mpa_ratings.id " +
            "WHERE films.id = ? " +
            "ORDER BY films.id";
    private static final String CREATE_SQL_QUERY = "INSERT INTO films (name, description, release_date, duration, mpa_rating) " +
            "VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE_SQL_QUERY = "UPDATE films " +
            "SET name = ?, " +
            "description = ?, " +
            "release_date = ?, " +
            "duration = ?, " +
            "mpa_rating = ? " +
            "WHERE id = ?";
    private final JdbcTemplate jdbcTemplate;


    @Override
    public Collection<Film> getAll() {
        return jdbcTemplate.query(SELECT_ALL_SQL_QUERY, new FilmMapper());
    }

    @Override
    public Film create(Film film) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement preparedStatement = connection
                    .prepareStatement(CREATE_SQL_QUERY,
                            Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, film.getName());
            preparedStatement.setString(2, film.getDescription());
            preparedStatement.setDate(3, Date.valueOf(film.getReleaseDate()));
            preparedStatement.setInt(4, film.getDuration());
            if (film.getMpa() == null || film.getMpa().getId() == null) {
                preparedStatement.setNull(5, Types.INTEGER);
            } else {
                preparedStatement.setInt(5, film.getMpa().getId());
            }
            return preparedStatement;
        }, keyHolder);

        Long id = keyHolder.getKeyAs(Long.class);
        return get(id).orElseThrow();
    }

    @Override
    public Film update(Film film) {
        if (get(film.getId()).isEmpty()) {
            throw new FilmNotFoundException(film.getId());
        }

        jdbcTemplate.update(UPDATE_SQL_QUERY,
                film.getName(),
                film.getDescription(),
                Date.valueOf(film.getReleaseDate()),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId());
        return get(film.getId()).orElseThrow();
    }

    @Override
    public Optional<Film> get(Long id) {
        return jdbcTemplate.query(SELECT_BY_ID_SQL_QUERY, new FilmMapper(), id)
                .stream()
                .findAny();
    }
}
