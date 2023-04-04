package ru.yandex.practicum.filmorate.storage.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryUserStorageTest extends UserStorageTest<InMemoryUserStorage> {

    @Override
    @BeforeEach
    void createStorage() {
        userStorage = new InMemoryUserStorage();
    }

    @Test
    void validateOk() throws ValidationException {
        User user = User.builder()
                .email("test@mail.ru")
                .login("login")
                .name("name")
                .birthday(LocalDate.of(2001, 05, 10))
                .build();

        assertEquals(userStorage.createUser(user), user);
    }

    @Test
    void validateWithEmptyEmail() {
        User user = User.builder()
                .email("")
                .login("login")
                .name("name")
                .birthday(LocalDate.of(2001, 05, 10))
                .build();

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> userStorage.createUser(user)
        );
        assertEquals(exception.getMessage(),
                "Электронная почта не может быть пустой и должна содержать символ @.");
    }

    @Test
    void validateWithWrongEmail() {
        User user = User.builder()
                .email("badEmail.ru")
                .login("login")
                .name("name")
                .birthday(LocalDate.of(2001, 05, 10))
                .build();

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> userStorage.createUser(user)
        );
        assertEquals(exception.getMessage(),
                "Электронная почта не может быть пустой и должна содержать символ @.");
    }

    @Test
    void validateWithEmptyLogin() {
        User user = User.builder()
                .email("test@mail.ru")
                .login("")
                .name("name")
                .birthday(LocalDate.of(2001, 05, 10))
                .build();

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> userStorage.createUser(user)
        );
        assertEquals(exception.getMessage(), "Логин не может быть пустым и содержать пробелы.");
    }

    @Test
    void validateWithFutureBirthday() {
        User user = User.builder()
                .email("test@mail.ru")
                .login("login")
                .name("name")
                .birthday(LocalDate.of(2031, 05, 10))
                .build();

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> userStorage.createUser(user)
        );
        assertEquals(exception.getMessage(), "Дата рождения не может быть в будущем.");
    }

    @Test
    void createEmptyName() throws ValidationException {
        User user = User.builder()
                .email("test@mail.ru")
                .login("login")
                .name("")
                .birthday(LocalDate.of(2001, 05, 10))
                .build();
        User user2 = User.builder()
                .id(1)
                .email("test@mail.ru")
                .login("login")
                .name("login")
                .birthday(LocalDate.of(2001, 05, 10))
                .build();

        assertEquals(userStorage.createUser(user), user2);
    }
}