package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private int id = 0;
    private final HashMap<Integer, User> users = new HashMap<>();

    @GetMapping
    public List<User> getAll() {
        return users.values().stream().collect(Collectors.toList());
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        User validateUser = validateUser(user);
        validateUser.setId(setId());
        users.put(validateUser.getId(), validateUser);
        log.debug(validateUser.toString());
        return validateUser;
    }

    @PutMapping
    public User put(@Valid @RequestBody User user) {
        if (users.containsKey(user.getId())) {
            User validateUser = validateUser(user);
            User updateUser = users.get(validateUser.getId());

            updateUser.setEmail(validateUser.getEmail());
            updateUser.setBirthday(validateUser.getBirthday());
            updateUser.setLogin(validateUser.getLogin());
            updateUser.setName(validateUser.getName());

            users.put(updateUser.getId(), updateUser);
            log.debug(updateUser.toString());
            return updateUser;
        } else {
            throw new ValidationException("такого id не существует.");
        }
    }

    public User validateUser(User user) {
        if (user.getEmail().isEmpty() || !user.getEmail().contains("@")) {
            throw new ValidationException("Электронная почта не может быть пустой и должна содержать символ @.");
        } else if (user.getLogin().isBlank()) {
            throw new ValidationException("Логин не может быть пустым и содержать пробелы.");
        } else if (user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("Дата рождения не может быть в будущем.");
        } else if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        return user;
    }

    private int setId() {
        id++;
        return id;
    }
}
