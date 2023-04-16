package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.FilmRatingsMPA;
import ru.yandex.practicum.filmorate.storage.RatingMpaStorage;

import java.util.Collection;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class MpaDaoImpl implements RatingMpaStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Collection<FilmRatingsMPA> getAllRatings() {
        return jdbcTemplate.query("SELECT * FROM film_ratings_mpa", filmRatingRowMapper());
    }

    @Override
    public FilmRatingsMPA getRatingById(int ratingId) {
        List<FilmRatingsMPA> ratings = jdbcTemplate.query(
                "SELECT * FROM film_ratings_mpa WHERE mpa_id = ?", filmRatingRowMapper(), ratingId);
        if (ratings.size() != 1) {
            throw new FilmNotFoundException(String.format("MPA с id = %d не существует", ratingId));
        }
        return ratings.get(0);
    }

    private RowMapper<FilmRatingsMPA> filmRatingRowMapper() {
        return (rs, rowNum) -> FilmRatingsMPA.builder()
                .id(rs.getInt("mpa_id"))
                .name(rs.getString("mpa_name"))
                .build();
    }
}
