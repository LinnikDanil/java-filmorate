package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.FilmRatingsMPA;

import java.util.Collection;

public interface RatingMpaStorage {
    Collection<FilmRatingsMPA> getAllRatings();

    FilmRatingsMPA getRatingById(int ratingId);
}
