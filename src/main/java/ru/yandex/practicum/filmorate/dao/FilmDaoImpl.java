package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.UserAlreadyExistException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component("filmDaoImpl")
public class FilmDaoImpl implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Film getFilmById(int filmId) {
        List<Film> films = jdbcTemplate.query("SELECT * FROM films f JOIN film_ratings_mpa mpa " +
                "ON f.rating_mpa_id = mpa.mpa_id WHERE id = ?", this::filmRowMapper, filmId);
        if (films.size() != 1) {
            throw new FilmNotFoundException(String.format("Фильма с id = %s не существует.", filmId));
        }
        return films.get(0);
    }

    @Override
    public Collection<Film> getAllFilms() {
        return jdbcTemplate.query("SELECT * FROM films f " +
                "JOIN film_ratings_mpa mpa ON f.rating_mpa_id = mpa.mpa_id", this::filmRowMapper);
    }

    @Override
    public Collection<Film> getPopularFilms(int count) {
        String sql = "SELECT * FROM films f JOIN film_ratings_mpa mpa ON f.rating_mpa_id = mpa.mpa_id " +
                "WHERE f.ID IN (SELECT f.ID FROM FILMS f LEFT JOIN FILM_LIKES fl ON f.ID = fl.FILM_ID " +
                "GROUP BY f.ID ORDER BY COUNT(fl.USER_ID) DESC LIMIT ?)";
        return jdbcTemplate.query(sql, this::filmRowMapper, count);
    }

    @Override
    public Film createFilm(Film film) {
        validateFilm(film);

        KeyHolder keyHolder = new GeneratedKeyHolder();
        String sql = "INSERT INTO FILMS " +
                "(NAME, DESCRIPTION, RELEASE_DATE, DURATION, RATING_MPA_ID) VALUES(?, ?, ?, ?, ?)";

        jdbcTemplate.update(con -> {
            PreparedStatement preparedStatement = con.prepareStatement(sql, new String[]{"id"});
            preparedStatement.setString(1, film.getName());
            preparedStatement.setString(2, film.getDescription());
            preparedStatement.setDate(3, Date.valueOf(film.getReleaseDate()));
            preparedStatement.setLong(4, film.getDuration());
            preparedStatement.setInt(5, film.getMpa().getId());
            return preparedStatement;
        }, keyHolder);
        film.setId(keyHolder.getKey().intValue());

        addGenresRelation(film);
        setGenresToFilm(film);

        log.info("Добавлен фильм: " + film);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        validateFilm(film);

        if (isExistFilm(film.getId())) {
            String sql = "UPDATE FILMS " +
                    "SET NAME=?, DESCRIPTION=?, RELEASE_DATE=?, DURATION=?, RATING_MPA_ID=? WHERE ID=?";
            jdbcTemplate.update(sql,
                    film.getName(),
                    film.getDescription(),
                    film.getReleaseDate(),
                    film.getDuration(),
                    film.getMpa().getId(),
                    film.getId());

            removeGenre(film.getId());
            addGenresRelation(film);
            setGenresToFilm(film);

            log.info("Фильм обновлён: " + film);
            return film;
        } else {
            throw new FilmNotFoundException(
                    String.format("Фильма с id = %s не существует. Обновление не удалось", film.getId()));
        }
    }

    @Override
    public Integer addLike(int filmId, int userId) {
        if (!isExistFilm(filmId)) {
            throw new FilmNotFoundException(String.format("Фильма с id = %s не существует.", filmId));
        }
        if (getFilmById(filmId).getLikes().contains(userId)) {
            throw new UserAlreadyExistException(String.format("Пользователь с id = %s уже лайкал этот фильм.", userId));
        }
        if (!isExistUser(userId)) {
            throw new UserNotFoundException(String.format("Пользователя с id = %s не существует.", userId));
        }

        jdbcTemplate.update("INSERT INTO FILM_LIKES (FILM_ID, USER_ID) VALUES(?, ?)", filmId, userId);

        Film updateLikesFilm = getFilmById(filmId);
        updateLikesFilm.getLikes().add(new FilmLikes(filmId, userId));

        return getCountLikes(filmId);
    }

    @Override
    public Integer deleteLike(int filmId, int userId) {
        if (!isExistFilm(filmId)) {
            throw new FilmNotFoundException(String.format("Фильма с id = %s не существует.", filmId));
        }
        if (getFilmById(filmId).getLikes().contains(userId)) {
            throw new UserAlreadyExistException(String.format("Пользователь с id = %s уже лайкал этот фильм.", userId));
        }
        if (!isExistUser(userId)) {
            throw new UserNotFoundException(String.format("Пользователя с id = %s не существует.", userId));
        }

        jdbcTemplate.update("DELETE FROM FILM_LIKES WHERE FILM_ID=? AND USER_ID=?;", filmId, userId);

        Film updateLikesFilm = getFilmById(filmId);
        updateLikesFilm.getLikes().remove(filmId);

        return getCountLikes(filmId);
    }

    private Film filmRowMapper(ResultSet rs, int rowNum) throws SQLException {
        Film film = Film.builder()
                .id(rs.getInt("id"))
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .releaseDate(rs.getDate("release_date").toLocalDate())
                .duration(rs.getLong("duration"))
                .mpa(new FilmRatingsMPA(rs.getInt("mpa_id"), rs.getString("mpa_name")))
                .build();
        setGenresToFilm(film);
        return film;
    }

    private boolean isExistFilm(int filmId) {
        List<Film> films = jdbcTemplate.query("SELECT * FROM films f JOIN film_ratings_mpa mpa " +
                "ON f.rating_mpa_id = mpa.mpa_id WHERE f.id = ?", this::filmRowMapper, filmId);
        if (films.size() != 1) {
            return false;
        }
        return true;
    }

    private RowMapper<Integer> countRowMapper() {
        return ((rs, rowNum) -> rs.getInt("count"));
    }

    private Integer getCountLikes(int filmId) {
        Integer countLikes = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) AS COUNT FROM FILM_LIKES WHERE FILM_ID = ?", countRowMapper(), filmId);
        if (countLikes == null) {
            throw new RuntimeException("Непредвиденная ошибка в методе getCountLikes.");
        }
        return countLikes;
    }

    private boolean isExistUser(int userId) {
        Integer userIsExist = jdbcTemplate.queryForObject("SELECT COUNT(*) AS COUNT FROM users WHERE id = ?",
                countRowMapper(), userId);
        if (userIsExist != 1) {
            return false;
        }
        return true;
    }

    private void addGenresRelation(Film film) {
        if (!film.getGenres().isEmpty()) {
            for (FilmGenres genre : film.getGenres()) {
                String sql = "MERGE INTO FILM_GENRES (FILM_ID, GENRE_ID) KEY (FILM_ID, GENRE_ID) VALUES (?, ?)";
                jdbcTemplate.update(sql, film.getId(), genre.getId());
            }
        }
    }

    private void setGenresToFilm(Film film) {
        List<FilmGenres> genres = jdbcTemplate.query("SELECT fg.GENRE_ID, g.NAME FROM FILM_GENRES fg " +
                        "JOIN GENRES g ON g.ID = fg.GENRE_ID WHERE fg.FILM_ID = ? ORDER BY fg.GENRE_ID",
                (rs, rowNum) -> new FilmGenres(rs.getInt("genre_id"),
                        rs.getString("name")), film.getId());

        film.getGenres().clear();
        for (FilmGenres genre : genres) {
            film.getGenres().add(genre);
        }
    }

    private void removeGenre(int filmId) {
        jdbcTemplate.update("DELETE FROM FILM_GENRES WHERE FILM_ID=?", filmId);
    }
}
