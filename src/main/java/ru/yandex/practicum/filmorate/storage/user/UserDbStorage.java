package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.mapper.UserMapper;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Collection;
import java.util.Optional;

@Slf4j
@Repository("userDbStorage")
@Primary
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {
    private static final String SELECT_ALL_SQL_QUERY = "SELECT * FROM users";
    private static final String SELECT_BY_ID_SQL_QUERY = "SELECT * FROM users " +
            "WHERE id = ?";
    private static final String UPDATE_SQL_QUERY = "UPDATE users " +
            "SET email = ?, " +
            "login = ?, " +
            "name = ?, " +
            "birthday = ? " +
            "WHERE id = ?";
    private static final String CREATE_SQL_QUERY = "INSERT INTO users (email, login, name, birthday) " +
            "VALUES (?, ?, ?, ?)";
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Collection<User> getAll() {
        return jdbcTemplate.query(SELECT_ALL_SQL_QUERY, new UserMapper());
    }

    @Override
    public User create(User user) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement preparedStatement = connection
                    .prepareStatement(CREATE_SQL_QUERY,
                            Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, user.getEmail());
            preparedStatement.setString(2, user.getLogin());
            preparedStatement.setString(3, user.getName());
            preparedStatement.setDate(4, Date.valueOf(user.getBirthday()));
            return preparedStatement;
        }, keyHolder);

        Long id = keyHolder.getKeyAs(Long.class);
        return get(id).orElseThrow();
    }

    @Override
    public User update(User user) {
        if (get(user.getId()).isEmpty()) {
            throw  new UserNotFoundException(user.getId());
        }

        jdbcTemplate.update(UPDATE_SQL_QUERY,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                Date.valueOf(user.getBirthday()),
                user.getId());
        return get(user.getId()).orElseThrow();
    }

    @Override
    public Optional<User> get(Long id) {
        return jdbcTemplate.query(SELECT_BY_ID_SQL_QUERY, new UserMapper(), id).stream().findAny();
    }
}
