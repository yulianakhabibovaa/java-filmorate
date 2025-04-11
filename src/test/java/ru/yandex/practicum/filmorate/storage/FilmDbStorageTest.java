package ru.yandex.practicum.filmorate.storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.mapper.FilmMapper;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@JdbcTest
@AutoConfigureTestDatabase
@Import({FilmDbStorage.class, FilmMapper.class})
class FilmDbStorageTest {

    @Autowired
    private FilmDbStorage filmDbStorage;

    private Film testFilm;

    @BeforeEach
    void setUp() {
        testFilm = Film.builder()
                .name("Test Film")
                .description("Test Description")
                .releaseDate(LocalDate.of(2023, 1, 1))
                .duration(120)
                .mpa(new MpaRating(1, null, null))
                .build();
    }

    @Test
    void shouldReturnAllFilms() {
        Film film1 = filmDbStorage.create(testFilm);
        Film film2 = filmDbStorage.create(Film.builder()
                .name("Test Film 2")
                .description("Test Description")
                .releaseDate(LocalDate.of(2023, 1, 1))
                .duration(120)
                .mpa(new MpaRating(1, "G", "General Audiences"))
                .build());

        Collection<Film> films = filmDbStorage.getAll();

        assertThat(films)
                .hasSize(2)
                .extracting(Film::getId)
                .containsExactlyInAnyOrder(film1.getId(), film2.getId());
    }

    @Test
    void shouldSaveFilm() {
        Film createdFilm = filmDbStorage.create(testFilm);

        assertThat(createdFilm.getId()).isNotNull();
        assertThat(createdFilm)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .ignoringFields("mpa.name")
                .ignoringFields("mpa.description")
                .isEqualTo(testFilm);

        Optional<Film> retrievedFilm = filmDbStorage.get(createdFilm.getId());
        assertThat(retrievedFilm).isPresent();
        assertThat(retrievedFilm.get()).isEqualTo(createdFilm);
    }

    @Test
    void shouldHandleNullMpaRating() {
        Film filmWithoutMpa = filmDbStorage.create(Film.builder()
                .name("Test Film")
                .description("Test Description")
                .releaseDate(LocalDate.of(2023, 1, 1))
                .duration(120)
                .build());

        Film createdFilm = filmDbStorage.create(filmWithoutMpa);

        Optional<Film> retrievedFilm = filmDbStorage.get(createdFilm.getId());
        assertThat(retrievedFilm).isPresent();
        assertThat(retrievedFilm.get().getMpa()).isNull();
    }

    @Test
    void shouldUpdateFilmFields() {
        Film createdFilm = filmDbStorage.create(testFilm);
        Film updatedFilm = Film.builder()
                .id(createdFilm.getId())
                .name("Updated Name")
                .releaseDate(LocalDate.of(2023, 1, 1))
                .description("Updated Description")
                .duration(150)
                .mpa(new MpaRating(2, null, null))
                .build();

        Film result = filmDbStorage.update(updatedFilm);

        assertThat(result)
                .usingRecursiveComparison()
                .ignoringFields("mpa.name")
                .ignoringFields("mpa.description")
                .isEqualTo(updatedFilm);

        Film retrievedFilm = filmDbStorage.get(createdFilm.getId()).get();
        assertThat(retrievedFilm).usingRecursiveComparison()
                .ignoringFields("mpa.name")
                .ignoringFields("mpa.description")
                .isEqualTo(updatedFilm);
    }

    @Test
    void shouldThrowExceptionWhenFilmNotFound() {
        Film nonExistingFilm = Film.builder()
                .id(999L)
                .name("Test Film")
                .description("Test Description")
                .releaseDate(LocalDate.of(2023, 1, 1))
                .duration(120)
                .build();

        assertThatThrownBy(() -> filmDbStorage.update(nonExistingFilm))
                .isInstanceOf(FilmNotFoundException.class)
                .hasMessageContaining("999");
    }

    @Test
    void shouldReturnEmptyOptionalForNonExistingId() {
        Optional<Film> result = filmDbStorage.get(999L);

        assertThat(result).isEmpty();
    }

    @Test
    void get_shouldReturnFilmWithCorrectMpaRating() {
        Film createdFilm = filmDbStorage.create(testFilm);

        Optional<Film> retrievedFilm = filmDbStorage.get(createdFilm.getId());

        assertThat(retrievedFilm).isPresent();
        assertThat(retrievedFilm.get().getMpa())
                .isNotNull()
                .extracting(MpaRating::getId, MpaRating::getName)
                .containsExactly(1, "G");
    }
}
