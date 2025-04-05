package ru.yandex.practicum.filmorate.storage.friendship;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.mapper.UserMapper;

import java.util.Collection;

@Slf4j
@Repository
@RequiredArgsConstructor
public class UserFriendshipDbStorage implements UserFriendshipStorage {
    private static final String SELECT_ALL_BY_ID_SQL_QUERY = "SELECT users.* " +
            "FROM users " +
            "INNER JOIN user_friendships ON users.id = user_friendships.friend_id " +
            "WHERE user_friendships.user_id = ?";
    private static final String SELECT_COMMON_FRIENDS_SQL_QUERY = "SELECT users.* " +
            "FROM users " +
            "INNER JOIN user_friendships AS f1 ON users.id = f1.friend_id " +
            "INNER JOIN user_friendships AS f2 ON users.id = f2.friend_id " +
            "WHERE f1.user_id = ? AND f2.user_id = ?";
    private static final String INSERT_SQL_QUERY = "INSERT INTO user_friendships (user_id, friend_id) " +
            "VALUES (?, ?)";
    private static final String DELETE_SQL_QUERY = "DELETE " +
            "FROM user_friendships " +
            "WHERE user_id = ? AND friend_id = ?";
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void addFriend(Long userId, Long friendId) {
        try {
            jdbcTemplate.update(INSERT_SQL_QUERY, userId, friendId);
        } catch (DuplicateKeyException e) {
            log.warn("Пользователь {} уже добавил в друзья пользователя {}", userId, friendId);
        } catch (DataIntegrityViolationException e) {
            log.error("Попытка подружить несуществующих пользователей: {}", e.getMessage());
            throw new NotFoundException("Одного из указанных пользователей не существует");
        }
    }

    @Override
    public void removeFriend(Long userId, Long friendId) {
        jdbcTemplate.update(DELETE_SQL_QUERY, userId, friendId);
    }

    @Override
    public Collection<User> getFriends(Long userId) {
        return jdbcTemplate.query(SELECT_ALL_BY_ID_SQL_QUERY, new UserMapper(), userId);
    }

    @Override
    public Collection<User> getMutualFriends(Long firstUserId, Long secondUserId) {
        return jdbcTemplate.query(SELECT_COMMON_FRIENDS_SQL_QUERY, new UserMapper(), firstUserId, secondUserId);
    }
}
