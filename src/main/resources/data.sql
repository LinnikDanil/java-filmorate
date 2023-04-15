INSERT INTO genres (name)
SELECT * FROM (
    VALUES ('Комедия'),
           ('Драма'),
           ('Мультфильм'),
           ('Триллер'),
           ('Документальный'),
           ('Боевик')
)
WHERE NOT EXISTS (
    SELECT name FROM genres
    WHERE name IN ('Комедия',
                   'Драма',
                   'Мультфильм',
                   'Триллер',
                   'Документальный',
                   'Боевик')
);


INSERT INTO film_ratings_mpa (name)
SELECT * FROM (
    VALUES ('G'),
           ('PG'),
           ('PG-13'),
           ('R'),
           ('NC-17')
)
WHERE NOT EXISTS (
    SELECT name FROM film_ratings_mpa
    WHERE name IN ('G',
                   'PG',
                   'PG-13',
                   'R',
                   'NC-17')
);


INSERT INTO friendship_status (name)
SELECT * FROM (
    VALUES ('неподтверждённая'),
           ('подтверждённая')
)
WHERE NOT EXISTS (
    SELECT name FROM friendship_status
    WHERE name IN ('неподтверждённая',
                   'подтверждённая')
);