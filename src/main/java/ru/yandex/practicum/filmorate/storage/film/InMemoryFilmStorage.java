package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {

    private Integer id = 0;
    private final HashMap<Integer, Film> films = new HashMap<>();

    @Override
    public Collection<Film> getAllFilms() {
        return films.values();
    }

    public Film getFilmById(int filmId) {
        if (!films.containsKey(filmId)) {
            throw new FilmNotFoundException("Фильма с таким id не существует.");
        }
        return films.get(filmId);
    }

    @Override
    public Film createFilm(Film film) {
        validateFilm(film);
        film.setId(getNextId());
        films.put(film.getId(), film);
        log.debug(film.toString());
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
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
            throw new FilmNotFoundException("такого id не существует.");
        }
    }

    public boolean validateFilm(Film film) {
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

    public Integer getNextId() {
        return ++id;
    }

}
