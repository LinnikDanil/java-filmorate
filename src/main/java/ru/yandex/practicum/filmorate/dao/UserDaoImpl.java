package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.FriendNotFoundException;
import ru.yandex.practicum.filmorate.exception.UserAlreadyExistException;
import ru.yandex.practicum.filmorate.exception.UserCannotBeHisFriend;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.sql.*;
import java.util.Collection;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component("userDaoImpl")
public class UserDaoImpl implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public User getUserById(int userId) {
        List<User> users = jdbcTemplate.query("SELECT * FROM users WHERE id = ?", userRowMapper(), userId);
        if (users.size() != 1) {
            throw new UserNotFoundException(String.format("Пользователя с id = %s не существует.", userId));
        }
        return users.get(0);
    }

    @Override
    public Collection<User> getAllUsers() {
        return jdbcTemplate.query("SELECT * FROM users", userRowMapper());
    }

    @Override
    public User createUser(User user) {
        User validateUser = validateUser(user);

        KeyHolder keyHolder = new GeneratedKeyHolder();
        String sql = "INSERT INTO users (email, login, name, birthday) VALUES (?, ?, ?, ?)";

        jdbcTemplate.update(connection -> {
            PreparedStatement preparedStatement = connection.prepareStatement(sql, new String[]{"id"});
            preparedStatement.setString(1, validateUser.getEmail());
            preparedStatement.setString(2, validateUser.getLogin());
            preparedStatement.setString(3, validateUser.getName());
            preparedStatement.setDate(4, Date.valueOf(validateUser.getBirthday()));
            return preparedStatement;
        }, keyHolder);
        validateUser.setId(keyHolder.getKey().intValue());

        log.info("Добавлен пользователь: " + validateUser);
        return validateUser;
    }

    @Override
    public User updateUser(User user) {
        if (isExistUser(user.getId())) {
            String sql = "UPDATE USERS SET EMAIL=?, LOGIN=?, NAME=?, BIRTHDAY=? WHERE ID=?";
            User validateUser = validateUser(user);
            jdbcTemplate.update(sql,
                    validateUser.getEmail(),
                    validateUser.getLogin(),
                    validateUser.getName(),
                    validateUser.getBirthday(),
                    validateUser.getId());

            log.info("Пользователь обновлён: " + validateUser);
            return validateUser;
        } else {
            throw new UserNotFoundException(
                    String.format("Пользователя с id = %s не существует. Обновление не удалось", user.getId()));
        }
    }

    @Override
    public Collection<User> getAllFriends(int userId) {
        if (isExistUser(userId)) {
            return jdbcTemplate.query(
                    "SELECT u.* FROM FRIENDS f JOIN USERS u ON f.friend_id = u.ID WHERE f.user_id = ?",
                    userRowMapper(), userId);
        } else throw new UserNotFoundException(String.format("Пользователя с id = %s не существует.", userId));
    }

    @Override
    public Integer addToFriends(int userId, int friendId) {
        if (userId != friendId) {
            if (isExistUser(userId)) {
                if (isExistUser(friendId)) {

                    //Если у друга есть этот пользователь в друзьях, то запрос уже отправлен и ошибка
                    String sqlFriendsAlreadyExist =
                            "SELECT COUNT(*) AS count FROM FRIENDS f WHERE USER_ID = ? AND FRIEND_ID = ?";
                    Integer count = jdbcTemplate.queryForObject(sqlFriendsAlreadyExist,
                            countRowMapper(), userId, friendId);
                    if (count == 1) {
                        throw new UserAlreadyExistException(String.format(
                                "Пользователь с id = %s уже подписан на пользователя с id = %s.", friendId, userId));
                    }

                    //Если у пользователя есть этот друг в друзьях, то дружба становится подтверждённой и у друга +друг
                    count = jdbcTemplate.queryForObject(sqlFriendsAlreadyExist, countRowMapper(), friendId, userId);
                    Integer idFriendshipStatus;
                    String status;
                    //Если есть, то статус подтверждённая
                    if (count == 1) {
                        idFriendshipStatus = jdbcTemplate.queryForObject(
                                "SELECT id FROM FRIENDSHIP_STATUS WHERE NAME = 'подтверждённая';", idRowMapper());
                        status = "Подтверждённая";
                        if (idFriendshipStatus == null) {
                            throw new RuntimeException(
                                    "Статуса подтверждённая не существует. " +
                                            "Проверьте правильность заполнения БД при запуске приложения.");
                        }
                    } else { //Иначе не подтверждённая
                        idFriendshipStatus = jdbcTemplate.queryForObject(
                                "SELECT id FROM FRIENDSHIP_STATUS WHERE NAME = 'неподтверждённая';", idRowMapper());
                        status = "Неподтверждённая";
                        if (idFriendshipStatus == null) {
                            throw new RuntimeException(
                                    "Статуса неподтверждённая не существует. " +
                                            "Проверьте правильность заполнения БД при запуске приложения.");
                        }
                    }
                    //Добавляем пользователя в друзья
                    String sqlAddUserToFriends =
                            "INSERT INTO PUBLIC.FRIENDS (USER_ID, FRIEND_ID, FRIENDSHIP_STATUS_ID) VALUES(?, ?, ?)";
                    jdbcTemplate.update(sqlAddUserToFriends, userId, friendId, idFriendshipStatus);
                    log.info(String.format(
                            "Пользователь с id = %s добавлен в друзья к пользователю с id = %s со статусом = %s.",
                            friendId, userId, status));

                    //Выводим количество подписок друга:
                    return getCountFriends(userId);
                } else throw new FriendNotFoundException(String.format("Друга с id = %s не существует.", userId));
            } else throw new UserNotFoundException(String.format("Пользователя с id = %s не существует.", userId));
        } else throw new UserCannotBeHisFriend("Пользователь не может быть своим другом");
    }

    @Override
    public Integer deleteFromFriends(int userId, int friendId) {
        if (isExistUser(userId)) {
            if (isExistUser(friendId)) {
                jdbcTemplate.update("DELETE FROM FRIENDS WHERE USER_ID=? AND FRIEND_ID=?", userId, friendId);
                return getCountFriends(userId);
            } else throw new FriendNotFoundException(String.format("Друга с id = %s не существует.", userId));
        } else throw new UserNotFoundException(String.format("Пользователя с id = %s не существует.", userId));
    }

    @Override
    public Collection<User> getCommonFriends(int userId, int friendId) {
        if (isExistUser(userId)) {
            if (isExistUser(friendId)) {
                String sqlGetCommonFriends = "SELECT * FROM USERS " +
                                            "WHERE id IN (SELECT f1.FRIEND_ID " +
                                            "FROM FRIENDS f1 LEFT JOIN FRIENDS f2 ON f1.FRIEND_ID = f2.FRIEND_ID " +
                                            "WHERE F1.USER_ID = ? AND F2.USER_ID = ?)";
                return jdbcTemplate.query(sqlGetCommonFriends, userRowMapper(), userId, friendId);
            } else throw new FriendNotFoundException(String.format("Друга с id = %s не существует.", userId));
        } else throw new UserNotFoundException(String.format("Пользователя с id = %s не существует.", userId));
    }

    private Integer getCountFriends(int friendId) {
        Integer countFriends = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) AS COUNT FROM FRIENDS WHERE USER_ID = ?", countRowMapper(), friendId);
        if (countFriends == null) {
            throw new RuntimeException("Непредвиденная ошибка в методе getCountFriends.");
        }
        return countFriends;
    }

    private RowMapper<Integer> idRowMapper() {
        return ((rs, rowNum) -> rs.getInt("id"));
    }

    private RowMapper<Integer> countRowMapper() {
        return ((rs, rowNum) -> rs.getInt("count"));
    }

    private RowMapper<User> userRowMapper() {
        return (rs, rowNum) -> User.builder()
                .id(rs.getInt("id"))
                .email(rs.getString("email"))
                .login(rs.getString("login"))
                .name(rs.getString("name"))
                .birthday(rs.getDate("birthday").toLocalDate())
                .build();
    }

    private boolean isExistUser(int userId) {
        List<User> users = jdbcTemplate.query("SELECT * FROM users WHERE id = ?", userRowMapper(), userId);
        if (users.size() != 1) {
            return false;
        }
        return true;
    }
}
