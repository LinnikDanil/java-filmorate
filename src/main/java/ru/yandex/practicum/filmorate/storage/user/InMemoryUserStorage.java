package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.FriendNotFoundException;
import ru.yandex.practicum.filmorate.exception.UserAlreadyExistException;
import ru.yandex.practicum.filmorate.exception.UserCannotBeHisFriend;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

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
    public User getUserById(int userId) {
        if (!users.containsKey(userId)) {
            throw new UserNotFoundException("Пользователя с таким id не существует.");
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

    @Override
    public Collection<User> getAllFriends(int userId) {
        if (getUserById(userId) == null) {
            throw new UserNotFoundException(String.format("Пользователя с id = %s не существует.", userId));
        }
        List<User> allFriends = new ArrayList<>();
        User user = getUserById(userId);
        for (int friendId : user.getFriends()) {
            allFriends.add(getUserById(friendId));
        }
        return allFriends;
    }

    @Override
    public Integer addToFriends(int userId, int friendId) {
        if (getUserById(userId) == null) {
            throw new UserNotFoundException(String.format("Пользователя с id = %s не существует.", userId));
        }
        if (getUserById(friendId) == null) {
            throw new UserNotFoundException(String.format("Друга с id = %s не существует.", friendId));
        }
        if (getUserById(userId).getFriends().contains(friendId)) {
            throw new UserAlreadyExistException(
                    String.format("У пользователя с id = %s уже есть друг с id = %s", userId, friendId));
        }
        if (friendId == userId) {
            throw new UserCannotBeHisFriend("Пользователь не может стать своим другом");
        }

        getUserById(userId).addFriend(friendId);
        getUserById(friendId).addFriend(userId);
        return getUserById(userId).getFriends().size();
    }

    @Override
    public Integer deleteFromFriends(int userId, int friendId) {
        if (getUserById(userId) == null) {
            throw new UserNotFoundException(String.format("Пользователя с id = %s не существует.", userId));
        }
        if (getUserById(friendId) == null) {
            throw new FriendNotFoundException(String.format("Друга с id = %s не существует.", friendId));
        }
        if (!getUserById(userId).getFriends().contains(friendId)) {
            throw new FriendNotFoundException(
                    String.format("У пользователя с id = %s нет друга с id = %s", userId, friendId));
        }

        getUserById(userId).deleteFriend(friendId);
        getUserById(friendId).deleteFriend(userId);
        return getUserById(userId).getFriends().size();
    }

    @Override
    public Collection<User> getCommonFriends(int userId, int friendId) {
        if (getUserById(userId) == null) {
            throw new UserNotFoundException(String.format("Пользователя с id = %s не существует.", userId));
        }
        if (getUserById(friendId) == null) {
            throw new UserNotFoundException(String.format("Друга с id = %s не существует.", friendId));
        }
        if (getUserById(friendId).getFriends() == null) {
            return Collections.emptyList();
        }

        Set<Integer> userFriends = getUserById(userId).getFriends();
        Set<Integer> friendFriends = getUserById(friendId).getFriends();

        List<User> commonUsers = new ArrayList<>();
        for (Integer user : userFriends) {
            if (friendFriends.contains(user)) {
                commonUsers.add(getUserById(user));
            }
        }
        return commonUsers;
    }

    private Integer getNextId() {
        return ++id;
    }
}
