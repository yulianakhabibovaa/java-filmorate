package ru.yandex.practicum.filmorate.storage.rating;

import ru.yandex.practicum.filmorate.model.MpaRating;

import java.util.Collection;
import java.util.Optional;

public interface MpaRatingStorage {
    Collection<MpaRating> getAllRatings();

    Optional<MpaRating> getRating(Integer id);

    boolean contains(Integer id);
}
