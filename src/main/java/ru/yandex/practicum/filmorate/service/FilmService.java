package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.controller.dto.FilmCreateRequest;
import ru.yandex.practicum.filmorate.controller.dto.FilmDto;
import ru.yandex.practicum.filmorate.controller.dto.FilmUpdateRequest;
import ru.yandex.practicum.filmorate.converter.FilmConverter;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.like.FilmUserLikeStorage;
import ru.yandex.practicum.filmorate.storage.rating.MpaRatingStorage;
import ru.yandex.practicum.filmorate.validation.FilmValidator;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Collections.emptySet;
import static ru.yandex.practicum.filmorate.converter.FilmConverter.mapToFilm;
import static ru.yandex.practicum.filmorate.converter.FilmConverter.mapToFilmDto;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final FilmUserLikeStorage likeStorage;
    private final GenreStorage genreStorage;
    private final MpaRatingStorage mpaStorage;

    public Collection<FilmDto> getAllFilms() {
        List<FilmDto> films = filmStorage.getAll().stream().map(FilmConverter::mapToFilmDto).toList();
        setFilmGenres(films);
        return films;
    }

    public FilmDto getFilm(Long id) {
        FilmDto film = mapToFilmDto(filmStorage.get(id).orElseThrow(() -> new FilmNotFoundException(id)));
        setFilmGenres(film);
        return film;
    }

    public FilmDto saveNewFilm(FilmCreateRequest request) {
        Film film = mapToFilm(request);
        FilmValidator.validate(film);
        validateMpaRating(film.getMpa().getId());
        Set<Integer> genreIds = getGenreIds(request.getGenres());
        validateGenres(genreIds);

        Film createdFilm = filmStorage.create(film);
        log.debug("был создан фильм: {}", createdFilm);
        genreStorage.updateGenres(createdFilm.getId(), genreIds);
        FilmDto result = mapToFilmDto(createdFilm);
        setFilmGenres(result);
        return result;
    }

    public FilmDto updateFilm(FilmUpdateRequest request) {
        Film film = mapToFilm(request);
        if (film.getId() == null) {
            log.warn("Не был указан Id фильма");
            throw new ValidationException("Id должен быть указан");
        }
        FilmValidator.validate(film);
        validateMpaRating(film.getMpa().getId());
        Set<Integer> genreIds = getGenreIds(request.getGenres());
        validateGenres(genreIds);

        Film updatedFilm = filmStorage.update(film);
        log.debug("фильм был обновлен: {}", updatedFilm);
        genreStorage.updateGenres(updatedFilm.getId(), genreIds);
        FilmDto result = mapToFilmDto(updatedFilm);
        setFilmGenres(result);
        return result;
    }

    public Collection<FilmDto> getTopFilms(int count) {
        List<FilmDto> result = likeStorage.getTopFilms(count).stream().map(FilmConverter::mapToFilmDto).toList();
        setFilmGenres(result);
        return result;
    }

    private void setFilmGenres(FilmDto film) {
        film.setGenres(genreStorage.getFilmGenres(film.getId()));
    }

    private void setFilmGenres(Collection<FilmDto> films) {
        films.forEach(this::setFilmGenres);
    }

    private void validateMpaRating(Integer mpaId) {
        if (!mpaStorage.contains(mpaId)) {
            throw new NotFoundException("Не найден рейтинг mpa с id " + mpaId);
        }
    }

    private void validateGenres(Set<Integer> genreIds) {
        if (genreIds == null || genreIds.isEmpty()) {
            return;
        }

        Collection<Integer> nonExistentGenres = genreStorage.checkNonExistentGenreIds(genreIds);
        if (!nonExistentGenres.isEmpty()) {
            throw new NotFoundException("Не найдены жанры фильмов с id: " + nonExistentGenres);
        }
    }

    private Set<Integer> getGenreIds(Set<Genre> genres) {
        return genres == null
                ? emptySet()
                : genres.stream().map(Genre::getId).collect(Collectors.toSet());
    }
}
