package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.*;
import java.time.LocalDate;

@Data
@AllArgsConstructor
public class User {
    private int id;

    @NotNull(message = "Электронная почта не может быть пустой.")
    @Email(message = "Неверный формат электронной почты.")
    private String email;

    @NotBlank(message = "Логин не может быть пустым и содержать пробелы.")
    private String login;

    private String name;

    @PastOrPresent(message = "дата рождения не может быть в будущем.")
    private LocalDate birthday;
}
