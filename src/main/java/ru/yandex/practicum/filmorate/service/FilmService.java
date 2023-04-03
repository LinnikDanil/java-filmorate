package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.UserAlreadyExistException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FilmService {
    private FilmStorage filmStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public Integer addLike(int filmId, int userId) {
        if (filmStorage.getFilmById(filmId) == null) {
            throw new FilmNotFoundException(String.format("Фильма с id = %s не существует.", filmId));
        }
        if (filmStorage.getFilmById(filmId).getLikes().contains(userId)) {
            throw new UserAlreadyExistException(String.format("Пользователь с id = %s уже лайкал этот фильм.", userId));
        }

        filmStorage.getFilmById(filmId).addLike(userId);
        return filmStorage.getFilmById(filmId).getLikes().size();
    }

    public Integer deleteLike(int filmId, int userId) {
        if (filmStorage.getFilmById(filmId) == null) {
            throw new FilmNotFoundException(String.format("Фильма с id = %s не существует.", filmId));
        }
        if (!filmStorage.getFilmById(filmId).getLikes().contains(userId)) {
            throw new UserNotFoundException(String.format("Пользователь с id = %s не лайкал этот фильм.", userId));
        }

        filmStorage.getFilmById(filmId).deleteLike(userId);
        return filmStorage.getFilmById(filmId).getLikes().size();
    }

    public List<Film> getMostPopularFilms(int count) {
        return filmStorage.getAllFilms()
                .stream()
                .sorted((f1,f2) -> (f2.getLikes().size() - f1.getLikes().size()))
                .limit(count)
                .collect(Collectors.toList());
    }
}
