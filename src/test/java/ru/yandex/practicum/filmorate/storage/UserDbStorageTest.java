package ru.yandex.practicum.filmorate.storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.mapper.UserMapper;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@JdbcTest
@AutoConfigureTestDatabase
@Import({UserDbStorage.class, UserMapper.class})
class UserDbStorageTest {

    @Autowired
    private UserDbStorage userDbStorage;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .email("test@mail.ru")
                .login("testLogin")
                .name("Test User")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();
    }

    @Test
    void shouldReturnAllUsers() {
        // Given
        User user1 = createTestUser("login1", "email1");
        User user2 = createTestUser("login2", "email2");

        // When
        Collection<User> users = userDbStorage.getAll();

        // Then
        assertThat(users)
                .hasSize(2)
                .extracting(User::getId)
                .containsExactlyInAnyOrder(user1.getId(), user2.getId());
    }

    @Test
    void shouldSaveUserWithGeneratedId() {
        // When
        User createdUser = userDbStorage.create(testUser);

        // Then
        assertThat(createdUser.getId()).isNotNull();
        assertThat(createdUser)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(testUser);

        Optional<User> retrievedUser = userDbStorage.get(createdUser.getId());
        assertThat(retrievedUser).contains(createdUser);
    }

    @Test
    void shouldUpdateAllUserFields() {
        User createdUser = userDbStorage.create(testUser);

        User updatedUser = new User(
                createdUser.getId(),
                "updated@mail.ru",
                "updatedLogin",
                "Updated Name",
                LocalDate.of(2000, 12, 31)
        );

        User result = userDbStorage.update(updatedUser);

        assertThat(result)
                .usingRecursiveComparison()
                .isEqualTo(updatedUser);
    }

    @Test
    void shouldThrowExceptionForNonExistingUser() {
        User nonExistingUser = new User(
                999L,
                "test@mail.ru",
                "testLogin",
                "Test User",
                LocalDate.of(1990, 1, 1)
        );

        assertThatThrownBy(() -> userDbStorage.update(nonExistingUser))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("999");
    }

    @Test
    void shouldReturnEmptyOptionalForNonExistingId() {
        Optional<User> result = userDbStorage.get(999L);

        assertThat(result).isEmpty();
    }

    private User createTestUser(String login, String email) {
        return userDbStorage.create(
                new User(
                        null,
                        email,
                        login,
                        "Test User",
                        LocalDate.of(1990, 1, 1)
                ));
    }
}
