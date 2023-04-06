package ru.yandex.practicum.filmorate.exception;

public class UserCannotBeHisFriend extends RuntimeException {
    public UserCannotBeHisFriend(String message) {
        super(message);
    }
}
