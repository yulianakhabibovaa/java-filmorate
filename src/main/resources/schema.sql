CREATE TABLE IF NOT EXISTS mpa_ratings (
    id INTEGER PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS genres (
    id INTEGER PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS films (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(255),
    release_date DATE,
    duration INTEGER,
    mpa_rating INTEGER REFERENCES MPA_RATINGS (id)
);

CREATE TABLE IF NOT EXISTS users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    email VARCHAR(255) NOT NULL UNIQUE,
    login VARCHAR(255) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    birthday DATE
);

CREATE TABLE IF NOT EXISTS film_genres (
    film_id BIGINT NOT NULL,
    genre_id INTEGER NOT NULL,
    FOREIGN KEY (film_id) REFERENCES films (id),
    FOREIGN KEY (genre_id) REFERENCES genres (id),
    PRIMARY KEY (film_id, genre_id)
);

CREATE TABLE IF NOT EXISTS film_user_likes (
    film_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    FOREIGN KEY (film_id) REFERENCES films (id),
    FOREIGN KEY (user_id) REFERENCES users (id),
    PRIMARY KEY (film_id, user_id)
);

CREATE TABLE IF NOT EXISTS user_friendships (
    user_id BIGINT NOT NULL,
    friend_id BIGINT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users (id),
    FOREIGN KEY (friend_id) REFERENCES users (id),
    PRIMARY KEY (user_id, friend_id)
);
