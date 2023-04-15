CREATE TABLE IF NOT EXISTS users (
	id SERIAL NOT NULL PRIMARY KEY,
	email varchar(128) NOT NULL,
	login varchar(128) NOT NULL,
	name varchar(128),
	birthday DATE NOT NULL
);

CREATE TABLE IF NOT EXISTS friendship_status (
  id SERIAL NOT NULL PRIMARY KEY,
  name VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS friends (
  user_id INTEGER NOT NULL REFERENCES users(id),
  friend_id INTEGER NOT NULL REFERENCES users(id),
  friendship_status_id INTEGER NOT NULL REFERENCES friendship_status(id),
  PRIMARY KEY (user_id, friend_id)
);

CREATE TABLE IF NOT EXISTS film_ratings_mpa (
  id SERIAL NOT NULL PRIMARY KEY,
  name VARCHAR(16) NOT NULL
);

CREATE TABLE IF NOT EXISTS films (
	id SERIAL NOT NULL PRIMARY KEY,
	name varchar(256) NOT NULL,
	description varchar NOT NULL,
	release_date DATE NOT NULL,
	duration bigint NOT NULL,
	rating_mpa_id INTEGER NOT NULL REFERENCES film_ratings_mpa(id)
);

CREATE TABLE IF NOT EXISTS genres (
  id SERIAL NOT NULL PRIMARY KEY,
  name VARCHAR(128) NOT NULL
);

CREATE TABLE IF NOT EXISTS film_genres (
  film_id INTEGER NOT NULL REFERENCES films(id),
  genre_id INTEGER NOT NULL REFERENCES genres(id)
);

CREATE TABLE IF NOT EXISTS film_likes (
  film_id INTEGER NOT NULL REFERENCES films(id),
  user_id INTEGER NOT NULL REFERENCES users(id)
);

