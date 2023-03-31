package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private int id = 0;
    private final HashMap<Integer, Film> films = new HashMap<>();

    @GetMapping
    public List<Film> getAll() {
        return films.values().stream().collect(Collectors.toList());
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        validateFilm(film);
        film.setId(setId());
        films.put(film.getId(), film);
        log.debug(film.toString());
        return film;
    }

    @PutMapping
    public Film put(@Valid @RequestBody Film film) {
        validateFilm(film);

        if (films.containsKey(film.getId())) {
            Film updateFilm = films.get(film.getId());
            updateFilm.setName(film.getName());
            updateFilm.setDescription(film.getDescription());
            updateFilm.setReleaseDate(film.getReleaseDate());
            updateFilm.setDuration(film.getDuration());
            films.put(film.getId(), updateFilm);
            log.debug(updateFilm.toString());
            return updateFilm;
        } else {
            throw new ValidationException("такого id не существует.");
        }
    }

    public boolean validateFilm(@Valid Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            throw new ValidationException("Название фильма не может быть пустым.");
        } else if (film.getDescription().length() > 200) {
            throw new ValidationException("Максимальная длина описания - 200 символов.");
        } else if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ValidationException("Дата релиза — не раньше 28 декабря 1895 года.");
        } else if (film.getDuration() < 0) {
            throw new ValidationException("Продолжительность фильма должна быть положительной.");
        }
        return true;
    }

    private int setId() {
        id++;
        return id;
    }
}
