package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.service.FilmUserLikeService;

@RestController
@RequestMapping("/films/{filmId}/like/{userId}")
@RequiredArgsConstructor
public class FilmUserLikeController {
    private final FilmUserLikeService likeService;

    @PutMapping()
    public void addLike(@PathVariable Long filmId, @PathVariable Long userId) {
        likeService.addLike(filmId, userId);
    }

    @DeleteMapping()
    public void removeLike(@PathVariable Long filmId, @PathVariable Long userId) {
        likeService.removeLike(filmId, userId);
    }
}
