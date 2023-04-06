package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FriendNotFoundException;
import ru.yandex.practicum.filmorate.exception.UserAlreadyExistException;
import ru.yandex.practicum.filmorate.exception.UserCannotBeHisFriend;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;

@Service
public class UserService {
    private UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public List<User> getAllFriends(int userId) {
        if (userStorage.getUserById(userId) == null) {
            throw new UserNotFoundException(String.format("Пользователя с id = %s не существует.", userId));
        }
        List<User> allFriends = new ArrayList<>();
        User user = userStorage.getUserById(userId);
        for (int friendId : user.getFriends()) {
            allFriends.add(userStorage.getUserById(friendId));
        }
        return allFriends;
    }

    public Integer addToFriends(int userId, int friendId) {
        if (userStorage.getUserById(userId) == null) {
            throw new UserNotFoundException(String.format("Пользователя с id = %s не существует.", userId));
        }
        if (userStorage.getUserById(friendId) == null) {
            throw new UserNotFoundException(String.format("Друга с id = %s не существует.", friendId));
        }
        if (userStorage.getUserById(userId).getFriends().contains(friendId)) {
            throw new UserAlreadyExistException(
                    String.format("У пользователя с id = %s уже есть друг с id = %s", userId, friendId));
        }
        if (friendId == userId) {
            throw new UserCannotBeHisFriend("Пользователь не может стать своим другом");
        }

        userStorage.getUserById(userId).addFriend(friendId);
        userStorage.getUserById(friendId).addFriend(userId);
        return userStorage.getUserById(userId).getFriends().size();
    }

    public Integer deleteFromFriends(int userId, Integer friendId) {
        if (userStorage.getUserById(userId) == null) {
            throw new UserNotFoundException(String.format("Пользователя с id = %s не существует.", userId));
        }
        if (userStorage.getUserById(friendId) == null) {
            throw new FriendNotFoundException(String.format("Друга с id = %s не существует.", friendId));
        }
        if (!userStorage.getUserById(userId).getFriends().contains(friendId)) {
            throw new FriendNotFoundException(
                    String.format("У пользователя с id = %s нет друга с id = %s", userId, friendId));
        }

        userStorage.getUserById(userId).deleteFriend(friendId);
        userStorage.getUserById(friendId).deleteFriend(userId);
        return userStorage.getUserById(userId).getFriends().size();
    }

    public Collection<User> getCommonFriends(Integer userId, Integer friendId) {
        if (userStorage.getUserById(userId) == null) {
            throw new UserNotFoundException(String.format("Пользователя с id = %s не существует.", userId));
        }
        if (userStorage.getUserById(friendId) == null) {
            throw new UserNotFoundException(String.format("Друга с id = %s не существует.", friendId));
        }
        if (userStorage.getUserById(friendId).getFriends() == null) {
            return Collections.emptyList();
        }

        Set<Integer> userFriends = userStorage.getUserById(userId).getFriends();
        Set<Integer> friendFriends = userStorage.getUserById(friendId).getFriends();

        List<User> commonUsers = new ArrayList<>();
        for (Integer user : userFriends) {
            if (friendFriends.contains(user)) {
                commonUsers.add(userStorage.getUserById(user));
            }
        }
        return commonUsers;
    }
}
