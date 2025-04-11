package ru.yandex.practicum.filmorate.storage;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.mapper.GenreMapper;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;

@JdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({GenreDbStorage.class, GenreMapper.class, FilmDbStorage.class})
class GenreDbStorageTest {
    private Long filmId;

    @Autowired
    private GenreDbStorage genreDbStorage;

    @Autowired
    private FilmDbStorage filmDbStorage;

    @Autowired JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        filmId = filmDbStorage.create(Film.builder()
                .name("Test Film")
                .description("Test Description")
                .releaseDate(LocalDate.of(2023, 1, 1))
                .duration(120)
                .mpa(new MpaRating(1, null, null))
                .build()).getId();
    }

    @AfterEach
    void reset() {
        jdbcTemplate.update("DELETE FROM film_genres WHERE film_id = ?", filmId);
        jdbcTemplate.update("DELETE FROM films WHERE id = ?", filmId);
    }

    @Test
    void shouldReturnAllGenres() {
        Collection<Genre> genres = genreDbStorage.getAllGenres();

        assertThat(genres)
                .hasSize(6)
                .extracting(Genre::getName)
                .containsExactly("Комедия", "Драма", "Мультфильм", "Триллер", "Документальный", "Боевик");
    }

    @Test
    void shouldReturnExistingGenre() {
        Optional<Genre> genre = genreDbStorage.getGenre(1);

        assertThat(genre)
                .isPresent()
                .hasValueSatisfying(g ->
                        assertThat(g.getName()).isEqualTo("Комедия"));
    }

    @Test
    void shouldReturnEmptyForNonExistingId() {
        Optional<Genre> genre = genreDbStorage.getGenre(999);

        assertThat(genre).isEmpty();
    }

    @Test
    void shouldReturnFilmGenres() {
        genreDbStorage.addFilmGenres(filmId, Set.of(1, 2));

        Collection<Genre> genres = genreDbStorage.getFilmGenres(filmId);

        assertThat(genres)
                .hasSize(2)
                .extracting(Genre::getId)
                .containsExactly(1, 2);
    }

    @Test
    void shouldInsertNewGenres() {

        genreDbStorage.addFilmGenres(filmId, Set.of(1));

        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM film_genres WHERE film_id = ?",
                Integer.class,
                filmId
        );
        assertThat(count).isEqualTo(1);
    }

    @Test
    void shouldReplaceExistingGenres() {
        genreDbStorage.addFilmGenres(filmId, Set.of(1));

        genreDbStorage.updateGenres(filmId, Set.of(2));

        Collection<Genre> genres = genreDbStorage.getFilmGenres(filmId);
        assertThat(genres)
                .hasSize(1)
                .extracting(Genre::getId)
                .containsExactly(2);
    }

    @Test
    void checkNonExistentGenreIds_shouldReturnMissingIds() {
        Set<Integer> inputIds = Set.of(1, 77, 100);

        Collection<Integer> missingIds = genreDbStorage.checkNonExistentGenreIds(inputIds);

        assertThat(missingIds)
                .hasSize(2)
                .contains(77, 100);
    }

    @Test
    void checkNonExistentGenreIds_shouldReturnEmptyForExistingIds() {
        Set<Integer> inputIds = Set.of(1, 2);

        Collection<Integer> missingIds = genreDbStorage.checkNonExistentGenreIds(inputIds);

        assertThat(missingIds).isEmpty();
    }
}