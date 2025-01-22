package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class UserControllerTest {
    private UserController userController;
    private User user;

    @BeforeEach
    public void beforeEach() {
        userController = new UserController();
        user = new User();
        user.setEmail("danila-bagrov@ya.ru");
        user.setLogin("danya853");
        user.setName("Dan4ik");
        user.setBirthday(LocalDate.of(1971, 12, 27));
        userController.create(user);
    }

    @Test
    void emailValidationTest() {
        user.setEmail("");
        assertThrows(ValidationException.class, () -> userController.create(user));
        assertEquals(1, userController.findAll().size());
        user.setEmail("danila-bagrov");
        assertThrows(ValidationException.class, () -> userController.create(user));
        assertEquals(1, userController.findAll().size());
    }

    @Test
    void loginValidationTest() {
        user.setLogin("");
        assertThrows(ValidationException.class, () -> userController.create(user));
        assertEquals(1, userController.findAll().size());
        user.setLogin("l o g i n");
        assertThrows(ValidationException.class, () -> userController.create(user));
        assertEquals(1, userController.findAll().size());
    }

    @Test
    void birthdayValidationTest() {
        user.setBirthday(LocalDate.now().plusDays(1));
        assertThrows(ValidationException.class, () -> userController.create(user));
        assertEquals(1, userController.findAll().size());
    }
}
