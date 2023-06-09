package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import javax.validation.Valid;
import java.util.Collection;


@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    @Qualifier("userDaoImpl")
    private final UserStorage userStorage;

    @GetMapping
    public Collection<User> getAll() {
        return userStorage.getAllUsers();
    }

    @GetMapping("/{userId}")
    public User getUserById(@PathVariable Integer userId) {
        return userStorage.getUserById(userId);
    }

    @GetMapping("{userId}/friends/common/{friendId}")
    public Collection<User> getCommonFriends(@PathVariable Integer userId, @PathVariable Integer friendId) {
        return userStorage.getCommonFriends(userId, friendId);
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        return userStorage.createUser(user);
    }

    @PutMapping
    public User put(@Valid @RequestBody User user) {
        return userStorage.updateUser(user);
    }

    @GetMapping("/{userId}/friends")
    public Collection<User> getAllFriends(@PathVariable Integer userId) {
        return userStorage.getAllFriends(userId);
    }

    @PutMapping("/{userId}/friends/{friendId}")
    public Integer addFriend(@PathVariable Integer userId, @PathVariable Integer friendId) {
        return userStorage.addToFriends(userId, friendId);
    }

    @DeleteMapping("/{userId}/friends/{friendId}")
    public Integer deleteFriend(@PathVariable Integer userId, @PathVariable Integer friendId) {
        return userStorage.deleteFromFriends(userId, friendId);
    }
}
