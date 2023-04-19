package ru.yandex.practicum.filmorate.storage.user;

import org.junit.jupiter.api.BeforeEach;
import ru.yandex.practicum.filmorate.storage.UserStorage;

class UserStorageTest<T extends UserStorage> {

    protected T userStorage;

    @BeforeEach
    void createStorage() {
        System.out.println("Тест UserStorage начался");
    }
}