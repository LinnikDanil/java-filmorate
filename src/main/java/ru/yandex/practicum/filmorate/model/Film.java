package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Builder
@Data
public class Film {
    private int id;

    @NotBlank(message = "Название фильма не может быть пустым.")
    private String name;

    @Size(max = 200, message = "Описание не может превышать 200 символов.")
    private String description;

    @NotNull
    private LocalDate releaseDate;

    @Positive
    private long duration; //Duration не проходит тесты, почитал в пачке, говорят ставить лонг

    final private Set<Integer> likes = new HashSet<>();

    public void addLike(int filmId) {
        likes.add(filmId);
    }

    public void deleteLike(int filmId) {
        likes.remove(filmId);
    }
}
