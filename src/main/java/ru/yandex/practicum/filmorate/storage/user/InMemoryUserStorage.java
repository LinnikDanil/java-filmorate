package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    private int id = 0;
    private final HashMap<Integer, User> users = new HashMap<>();

    @Override
    public Collection<User> getAllUsers() {
        return users.values();
    }

    @Override
    public User getUserById(int userId){
        if (!users.containsKey(userId)) {
            throw  new UserNotFoundException("Пользователя с таким id не существует.");
        }
        return users.get(userId);
    }

    @Override
    public User createUser(User user) {
        User validateUser = validateUser(user);
        validateUser.setId(getNextId());
        users.put(validateUser.getId(), validateUser);
        log.debug(validateUser.toString());
        return validateUser;
    }

    @Override
    public User updateUser(User user) {
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
            throw new UserNotFoundException("такого id не существует.");
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

    public Integer getNextId() {
        return ++id;
    }
}
