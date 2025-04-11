package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.rating.MpaRatingStorage;

import java.util.Collection;

@Slf4j
@Service
@RequiredArgsConstructor
public class MpaRatingService {
    private final MpaRatingStorage ratingDbStorage;

    public Collection<MpaRating> getAllRatings() {
        return ratingDbStorage.getAllRatings();
    }

    public MpaRating getRatingById(Integer id) {
        return ratingDbStorage.getRating(id)
                .orElseThrow(() -> new NotFoundException("Рейтинга с таким id не существует"));
    }
}
