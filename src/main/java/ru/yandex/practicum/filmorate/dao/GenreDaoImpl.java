package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.FilmGenres;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.Collection;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class GenreDaoImpl implements GenreStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Collection<FilmGenres> getAllGenres() {
        return jdbcTemplate.query("SELECT * FROM genres", FilmGenreRowMapper());
    }

    @Override
    public FilmGenres getGenreById(int genreId) {
        List<FilmGenres> genres = jdbcTemplate.query(
                "SELECT * FROM genres WHERE id = ?", FilmGenreRowMapper(), genreId);
        if (genres.size() != 1) {
            throw new FilmNotFoundException(String.format("Жанра с id = %d не существует", genreId));
        }
        return genres.get(0);
    }

    private RowMapper<FilmGenres> FilmGenreRowMapper() {
        return (rs, rowNum) -> FilmGenres.builder()
                .id(rs.getInt("id"))
                .name(rs.getString("name"))
                .build();
    }
}
