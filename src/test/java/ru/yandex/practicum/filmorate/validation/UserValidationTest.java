package ru.yandex.practicum.filmorate.validation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertThrows;

class UserValidationTest {
    private User user;

    @BeforeEach
    public void beforeEach() {
        user = new User();
        user.setEmail("danila-bagrov@ya.ru");
        user.setLogin("danya853");
        user.setName("Dan4ik");
        user.setBirthday(LocalDate.of(1971, 12, 27));
    }

    @Test
    void emailValidationTest() {
        user.setEmail(null);
        makeFailedValidationAssertions();
        user.setEmail("");
        makeFailedValidationAssertions();
        user.setEmail("       ");
        makeFailedValidationAssertions();
        user.setEmail("danila-bagrov");
        makeFailedValidationAssertions();
    }

    @Test
    void loginValidationTest() {
        user.setLogin(null);
        makeFailedValidationAssertions();
        user.setLogin("");
        makeFailedValidationAssertions();
        user.setLogin("       ");
        makeFailedValidationAssertions();
        user.setLogin("l o g i n");
        makeFailedValidationAssertions();
    }

    @Test
    void birthdayValidationTest() {
        user.setBirthday(LocalDate.now().plusDays(1));
        makeFailedValidationAssertions();
    }

    private void makeFailedValidationAssertions() {
        assertThrows(ValidationException.class, () -> UserValidator.validate(user));
    }
}
