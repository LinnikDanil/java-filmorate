package ru.yandex.practicum.filmorate.model;

import lombok.Data;

@Data
public class Friends {
    private int userId;
    private int friendId;
    private int friendshipStatusId;
}
