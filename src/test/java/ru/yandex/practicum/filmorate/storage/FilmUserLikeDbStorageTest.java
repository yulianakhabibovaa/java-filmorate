package ru.yandex.practicum.filmorate.storage;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.friendship.UserFriendshipDbStorage;
import ru.yandex.practicum.filmorate.storage.mapper.UserMapper;

import java.time.LocalDate;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

@JdbcTest
@AutoConfigureTestDatabase
@Import({UserFriendshipDbStorage.class, UserMapper.class})
class FilmUserLikeDbStorageTest {

    @Autowired
    private UserFriendshipDbStorage friendshipStorage;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private Long user1;
    private Long user2;
    private Long user3;

    @BeforeEach
    void setUp() {
        user1 = createUser("user1@mail.ru", "login1").getId();
        user2 = createUser("user2@mail.ru", "login2").getId();
        user3 = createUser("user3@mail.ru", "login3").getId();
    }

    @AfterEach
    void reset() {
        jdbcTemplate.update("DELETE FROM user_friendships WHERE user_id = ? OR user_id = ? OR user_id = ?", user1, user2, user3);
        jdbcTemplate.update("DELETE FROM users WHERE id = ? OR id = ? OR id = ?", user1, user2, user3);
    }

    private User createUser(String email, String login) {
        jdbcTemplate.update(
                "INSERT INTO users (email, login, name, birthday) VALUES (?, ?, ?, ?)",
                email,
                login,
                "Test User",
                LocalDate.of(2000, 1, 1)
        );
        return jdbcTemplate.queryForObject(
                "SELECT * FROM users WHERE email = ?",
                new UserMapper(),
                email
        );
    }

    @Test
    void shouldAddFriendship() {
        friendshipStorage.addFriend(user1, user2);
        Collection<User> friends = friendshipStorage.getFriends(user1);
        assertThat(friends)
                .hasSize(1)
                .extracting(User::getId)
                .containsExactly(user2);
    }

    @Test
    void removeFriend_shouldDeleteFriendship() {
        friendshipStorage.addFriend(user1, user2);
        friendshipStorage.removeFriend(user1, user2);
        Collection<User> friends = friendshipStorage.getFriends(user1);
        assertThat(friends).isEmpty();
    }

    @Test
    void getFriends_shouldReturnEmptyListForNoFriends() {
        Collection<User> friends = friendshipStorage.getFriends(user1);
        assertThat(friends).isEmpty();
    }

    @Test
    void getMutualFriends_shouldFindCommonFriends() {
        friendshipStorage.addFriend(user1, user3);
        friendshipStorage.addFriend(user2, user3);
        Collection<User> mutual = friendshipStorage.getMutualFriends(user1, user2);
        assertThat(mutual)
                .hasSize(1)
                .extracting(User::getId)
                .containsExactly(user3);
    }

    @Test
    void getMutualFriends_shouldReturnEmptyForNoCommon() {
        friendshipStorage.addFriend(user1, user2);
        Collection<User> mutual = friendshipStorage.getMutualFriends(user1, user3);
        assertThat(mutual).isEmpty();
    }

    @Test
    void getMutualFriends_shouldHandleNonExistingUsers() {
        assertThatCode(() -> friendshipStorage.getMutualFriends(999L, 888L))
                .doesNotThrowAnyException();
    }
}
