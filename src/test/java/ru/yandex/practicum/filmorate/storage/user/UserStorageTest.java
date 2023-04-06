package ru.yandex.practicum.filmorate.storage.user;

import org.junit.jupiter.api.BeforeEach;

class UserStorageTest<T extends UserStorage> {

    protected T userStorage;

    @BeforeEach
    void createStorage() {
        System.out.println("Тест UserStorage начался");
    }
}