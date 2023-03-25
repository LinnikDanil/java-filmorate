package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Data
@AllArgsConstructor
public class Film {
    private int id;

    @NotBlank(message = "Название фильма не может быть пустым.")
    private String name;

    @Size(max = 200, message = "Описание не может превышать 200 символов.")
    private String description;

    //@BirthdayFilmsValidation - не получилось сделать свою аннотацию, если вдруг есть какая-то ссылочка
    //почитать про это, будет круто  =)
    @NotNull
    private LocalDate releaseDate;

    @Positive
    private long duration; //Duration не проходит тесты, почитал в пачке, говорят ставить лонг
}
