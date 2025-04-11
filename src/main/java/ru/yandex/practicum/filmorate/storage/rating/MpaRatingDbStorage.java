package ru.yandex.practicum.filmorate.storage.rating;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.mapper.MpaRatingMapper;

import java.util.Collection;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MpaRatingDbStorage implements MpaRatingStorage {
    private static final String SELECT_ALL_SQL_QUERY = "SELECT * FROM mpa_ratings ORDER BY id";
    private static final String SELECT_BY_ID_SQL_QUERY = "SELECT * FROM mpa_ratings WHERE id = ?";
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Collection<MpaRating> getAllRatings() {
        return jdbcTemplate.query(SELECT_ALL_SQL_QUERY, new MpaRatingMapper());
    }

    @Override
    public Optional<MpaRating> getRating(Integer id) {
        return jdbcTemplate.query(SELECT_BY_ID_SQL_QUERY, new MpaRatingMapper(), id)
                .stream().findAny();
    }

    @Override
    public boolean contains(Integer id) {
        return getRating(id).isPresent();
    }
}
