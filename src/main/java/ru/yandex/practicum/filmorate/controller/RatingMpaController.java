package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.FilmRatingsMPA;
import ru.yandex.practicum.filmorate.storage.RatingMpaStorage;

import java.util.Collection;

@RestController
@RequiredArgsConstructor
@RequestMapping("/mpa")
public class RatingMpaController {
    private final RatingMpaStorage mpaStorage;

    @GetMapping
    public Collection<FilmRatingsMPA> getAllMpa() {
        return mpaStorage.getAllRatings();
    }

    @GetMapping("/{id}")
    public FilmRatingsMPA getMpaById(@PathVariable Integer id) {
        return mpaStorage.getRatingById(id);
    }
}
