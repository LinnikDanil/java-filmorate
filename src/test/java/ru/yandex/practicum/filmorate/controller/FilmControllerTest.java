package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class FilmControllerTest {
    FilmController filmController = new FilmController();

    @Test
    void validateOk() throws ValidationException {
        Film film = new Film(
                1,
                "test 1",
                "test 1",
                LocalDate.of(2000, 10, 10),
                3
        );

        assertEquals(filmController.validateFilm(film),true);
    }

    @Test
    void validateWithEmptyName() {
        Film film = new Film(
                1,
                "   ",
                "test 1",
                LocalDate.of(2000, 10, 10),
                3
        );

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> filmController.validateFilm(film)
        );
        assertEquals(exception.getMessage(), "Название фильма не может быть пустым.");
    }

    @Test
    void validateWithDescription201elements() {
        Film film = new Film(
                1,
                "test 1",
                "1".repeat(201),
                LocalDate.of(2000, 10, 10),
                3
        );

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> filmController.validateFilm(film)
        );
        assertEquals(exception.getMessage(), "Максимальная длина описания - 200 символов.");
    }

    @Test
    void validateWithDateBefore1895() {
        Film film = new Film(
                1,
                "test 1",
                "test 1",
                LocalDate.of(1884, 10, 10),
                3
        );

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> filmController.validateFilm(film)
        );
        assertEquals(exception.getMessage(), "Дата релиза — не раньше 28 декабря 1895 года.");
    }

    @Test
    void validateWithDurationIsNegative() {
        Film film = new Film(
                1,
                "test 1",
                "test 1",
                LocalDate.of(2000, 10, 10),
                -1
        );

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> filmController.validateFilm(film)
        );
        assertEquals(exception.getMessage(), "Продолжительность фильма должна быть положительной.");
    }




}