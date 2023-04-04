package ru.yandex.practicum.filmorate.storage.film;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class InMemoryFilmStorageTest extends FilmStorageTest<InMemoryFilmStorage> {

    @Override
    @BeforeEach
    void createStorage() {
        filmStorage = new InMemoryFilmStorage();
    }

    @Test
    void createOk() throws ValidationException {
        Film film = Film.builder()
                .name("test 1")
                .description("test 1 description")
                .releaseDate(LocalDate.of(2000, 10, 10))
                .duration(3)
                .build();

        assertEquals(filmStorage.createFilm(film), film);
    }

    @Test
    void createWithEmptyName() {
        Film film = Film.builder()
                .name("")
                .description("test 1 description")
                .releaseDate(LocalDate.of(2000, 10, 10))
                .duration(3)
                .build();

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> filmStorage.createFilm(film)
        );
        assertEquals(exception.getMessage(), "Название фильма не может быть пустым.");
    }

    @Test
    void createWithDescription201elements() {
        Film film = Film.builder()
                .name("test 1")
                .description("1".repeat(201))
                .releaseDate(LocalDate.of(2000, 10, 10))
                .duration(3)
                .build();

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> filmStorage.createFilm(film)
        );
        assertEquals(exception.getMessage(), "Максимальная длина описания - 200 символов.");
    }

    @Test
    void createWithDateBefore1895() {
        Film film = Film.builder()
                .name("test 1")
                .description("test 1 description")
                .releaseDate(LocalDate.of(1884, 10, 10))
                .duration(3)
                .build();

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> filmStorage.createFilm(film)
        );
        assertEquals(exception.getMessage(), "Дата релиза — не раньше 28 декабря 1895 года.");
    }

    @Test
    void createWithDurationIsNegative() {
        Film film = Film.builder()
                .name("test 1")
                .description("test 1 description")
                .releaseDate(LocalDate.of(2000, 10, 10))
                .duration(-1)
                .build();

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> filmStorage.createFilm(film)
        );
        assertEquals(exception.getMessage(), "Продолжительность фильма должна быть положительной.");
    }
}