package ru.yandex.practicum.filmorate.storage;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.mapper.MpaRatingMapper;
import ru.yandex.practicum.filmorate.storage.rating.MpaRatingDbStorage;

import java.util.Collection;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({MpaRatingDbStorage.class, MpaRatingMapper.class})
class MpaRatingDbStorageTest {

    @Autowired
    private MpaRatingDbStorage mpaRatingStorage;

    @Test
    void getAllRatings_shouldReturnAllRatingsInOrder() {
        Collection<MpaRating> ratings = mpaRatingStorage.getAllRatings();

        assertThat(ratings)
                .hasSize(5)
                .extracting(MpaRating::getName)
                .containsExactly("G", "PG", "PG-13", "R", "NC-17");
    }

    @Test
    void getRating_shouldReturnExistingRating() {
        Optional<MpaRating> rating = mpaRatingStorage.getRating(1);

        assertThat(rating)
                .isPresent()
                .hasValueSatisfying(mpa -> {
                    assertThat(mpa.getId()).isEqualTo(1);
                    assertThat(mpa.getName()).isEqualTo("G");
                    assertThat(mpa.getDescription()).isEqualTo("У фильма нет возрастных ограничений");
                });
    }

    @Test
    void shouldReturnEmptyForNonExistingId() {
        Optional<MpaRating> rating = mpaRatingStorage.getRating(999);
        assertThat(rating).isEmpty();
    }

    @Test
    void shouldReturnTrueForExistingRating() {
        assertThat(mpaRatingStorage.contains(1)).isTrue();
        assertThat(mpaRatingStorage.contains(2)).isTrue();
    }

    @Test
    void shouldReturnFalseForNonExistingRating() {
        assertThat(mpaRatingStorage.contains(999)).isFalse();
    }
}
