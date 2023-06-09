package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.IncorrectParameterException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import javax.validation.Valid;
import java.util.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/films")
public class FilmController {
    @Qualifier("filmDaoImpl")
    private final FilmStorage filmStorage;

    @GetMapping
    public Collection<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    @GetMapping("/popular")
    public Collection<Film> getPopularFilms(@RequestParam(defaultValue = "10") Integer count) {
        if (count == null || count < 1) {
            throw new IncorrectParameterException("count");
        }
        return filmStorage.getPopularFilms(count);
    }

    @GetMapping("/{filmId}")
    public Film getFilmById(@PathVariable Integer filmId) {
        return filmStorage.getFilmById(filmId);
    }

    @PostMapping
    public Film createFilm(@Valid @RequestBody Film film) {
        return filmStorage.createFilm(film);
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        return filmStorage.updateFilm(film);
    }

    @PutMapping("/{filmId}/like/{userId}")
    public Integer addLikeFilm(@PathVariable Integer filmId, @PathVariable Integer userId) {
        return filmStorage.addLike(filmId, userId);
    }

    @DeleteMapping("/{filmId}/like/{userId}")
    public Integer deleteLikeFilm(@PathVariable Integer filmId, @PathVariable Integer userId) {
        return filmStorage.deleteLike(filmId, userId);
    }

}
