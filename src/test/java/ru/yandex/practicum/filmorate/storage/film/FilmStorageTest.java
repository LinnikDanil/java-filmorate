package ru.yandex.practicum.filmorate.storage.film;

import org.junit.jupiter.api.BeforeEach;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

abstract class FilmStorageTest<T extends FilmStorage> {
    protected T filmStorage;

    @BeforeEach
    void createStorage() {
        System.out.println("Тест FilmStorage начался");
    }

}