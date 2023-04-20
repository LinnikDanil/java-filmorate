package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.FilmGenres;

import java.util.Collection;

public interface GenreStorage {
    Collection<FilmGenres> getAllGenres();

    FilmGenres getGenreById(int genreId);
}
