package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {

    UserController userController = new UserController();

    @Test
    void validateOk() throws ValidationException {
        User user = new User(
                1,
                "test@mail.ru",
                "login",
                "name",
                LocalDate.of(2001, 05, 10)
        );

        assertEquals(userController.validateUser(user), user);
    }

    @Test
    void validateWithEmptyEmail() {
        User user = new User(
                1,
                "",
                "login",
                "name",
                LocalDate.of(2001, 05, 10)
        );

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> userController.validateUser(user)
        );
        assertEquals(exception.getMessage(),
                "Электронная почта не может быть пустой и должна содержать символ @.");
    }

    @Test
    void validateWithWrongEmail() {
        User user = new User(
                1,
                "badEmail.ru",
                "login",
                "name",
                LocalDate.of(2001, 05, 10)
        );

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> userController.validateUser(user)
        );
        assertEquals(exception.getMessage(),
                "Электронная почта не может быть пустой и должна содержать символ @.");
    }

    @Test
    void validateWithEmptyLogin() {
        User user = new User(
                1,
                "badEmail@mail.ru",
                "",
                "name",
                LocalDate.of(2001, 05, 10)
        );

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> userController.validateUser(user)
        );
        assertEquals(exception.getMessage(), "Логин не может быть пустым и содержать пробелы.");
    }

    @Test
    void validateWithFutureBirthday() {
        User user = new User(
                1,
                "badEmail@mail.ru",
                "login",
                "name",
                LocalDate.of(2031, 05, 10)
        );

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> userController.validateUser(user)
        );
        assertEquals(exception.getMessage(), "Дата рождения не может быть в будущем.");
    }

    @Test
    void validateEmptyName() throws ValidationException {
        User user = new User(
                1,
                "test@mail.ru",
                "login",
                "",
                LocalDate.of(2001, 05, 10)
        );
        User user2 = new User(
                1,
                "test@mail.ru",
                "login",
                "login",
                LocalDate.of(2001, 05, 10)
        );

        assertEquals(userController.validateUser(user), user2);
    }
}