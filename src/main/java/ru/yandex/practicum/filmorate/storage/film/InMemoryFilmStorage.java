package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.UserAlreadyExistException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmLikes;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

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

    @Override
    public List<Film> getPopularFilms(int count) {
        return getAllFilms()
                .stream()
                .sorted((f1, f2) -> (f2.getLikes().size() - f1.getLikes().size()))
                .limit(count)
                .collect(Collectors.toList());
    }

    public Integer addLike(int filmId, int userId) {
        if (getFilmById(filmId) == null) {
            throw new FilmNotFoundException(String.format("Фильма с id = %s не существует.", filmId));
        }
        if (getFilmById(filmId).getLikes().contains(userId)) {
            throw new UserAlreadyExistException(String.format("Пользователь с id = %s уже лайкал этот фильм.", userId));
        }

        getFilmById(filmId).getLikes().add(new FilmLikes(filmId, userId));
        return getFilmById(filmId).getLikes().size();
    }

    public Integer deleteLike(int filmId, int userId) {
        if (getFilmById(filmId) == null) {
            throw new FilmNotFoundException(String.format("Фильма с id = %s не существует.", filmId));
        }
        if (!getFilmById(filmId).getLikes().contains(userId)) {
            throw new UserNotFoundException(String.format("Пользователь с id = %s не лайкал этот фильм.", userId));
        }

        getFilmById(filmId).getLikes().remove(filmId);
        return getFilmById(filmId).getLikes().size();
    }

    private Integer getNextId() {
        return ++id;
    }

}
