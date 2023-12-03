package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.BaseEntity;
import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.model.user.User;
import ru.yandex.practicum.filmorate.model.user.UserFriends;
import ru.yandex.practicum.filmorate.storage.*;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

import static ru.yandex.practicum.filmorate.util.StatusFriends.NOT_APPROVED;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserStorage userStorage;
    private final UserFriendsStorage userFriendsStorage;
    private final StatusStorage statusStorage;
    private final FilmLikesStorage filmLikesStorage;
    private final FilmStorage filmStorage;
    private final MpaService mpaService;
    private final GenreService genreService;

    @Transactional
    public User create(User user) {
        log.info("Create user {}", user);
        if (user.getName() == null || user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
        return userStorage.create(user);
    }

    @Transactional
    public User update(User user) {
        log.info("Update user {}", user);
        validateFindUserById(user.getId());
        if (user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        return userStorage.update(user);
    }

    @Transactional
    public List<User> getAll() {
        log.debug("Get all users");
        return userStorage.getAll().stream()
                .peek(user -> user.setFriends(userFriendsStorage.getFriendsByUserId(user.getId())))
                .collect(Collectors.toList());
    }

    @Transactional
    public void addFriend(Integer id, Integer friendId) {
        log.info("Add user {} friend {}", id, friendId);
        validateFindUserById(id);
        validateFindUserById(friendId);

        Integer statusIdNotApproved = statusStorage.getByCode(NOT_APPROVED.toString()).getId();

        userFriendsStorage.addFriend(id, friendId, statusIdNotApproved);
    }

    @Transactional
    public void deleteFriend(Integer id, Integer friendId) {
        log.info("Delete user {} friend {}", id, friendId);
        validateFindUserById(id);
        validateFindUserById(friendId);

        //TODO: сделать обновление статуса
        userFriendsStorage.deleteFriend(id, friendId);
        userFriendsStorage.deleteFriend(friendId, id);
    }

    @Transactional
    public List<User> getFriends(Integer id) {
        log.debug("Get friends, user {}", id);
        validateFindUserById(id);

        return userFriendsStorage.getFriendsByUserId(id).stream()
                .map(user -> userStorage.getById(user.getUser().getId()))
                .collect(Collectors.toList());
    }

    @Transactional
    public List<User> getCommonFriends(Integer id, Integer otherId) {
        log.debug("User {} get mutual friend {}", id, otherId);
        validateFindUserById(id);
        validateFindUserById(otherId);

        Set<Integer> friends = userFriendsStorage.getFriendsByUserId(id).stream()
                .map(UserFriends::getUser)
                .map(BaseEntity::getId)
                .collect(Collectors.toSet());
        Set<Integer> otherFriends = userFriendsStorage.getFriendsByUserId(otherId).stream()
                .map(UserFriends::getUser)
                .map(BaseEntity::getId)
                .collect(Collectors.toSet());

        if (friends.isEmpty() || otherFriends.isEmpty()) {
            return new ArrayList<>();
        }
        return friends.stream()
                .filter(otherFriends::contains)
                .map(userStorage::getById)
                .collect(Collectors.toList());
    }

    @Transactional
    public User getUserById(Integer id) {
        log.debug("Get user by id {}", id);
        validateFindUserById(id);
        User user = userStorage.getById(id);
        user.setFriends(userFriendsStorage.getFriendsByUserId(user.getId()));
        return user;
    }

    public List<Film> getFilmRecommendations(Integer userId) {
        validateFindUserById(userId);
        Set<Integer> filmsLike = filmLikesStorage.getFilmUserLikes(userId);
        Map<Integer, List<Integer>> allFilmsLike = filmLikesStorage.getAll();
        allFilmsLike.remove(userId);

        Map<Integer, Integer> countMatchFilmsUser = allFilmsLike.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey,
                        entry -> (int) entry.getValue().stream().filter(filmsLike::contains).count()));

        if (countMatchFilmsUser.isEmpty()) {
            return new ArrayList<>();
        }

        Integer recommendationUserId = Collections.max(countMatchFilmsUser.entrySet(), Map.Entry.comparingByValue()).getKey();

        return allFilmsLike.get(recommendationUserId).stream()
                .filter(filmId -> !filmsLike.contains(filmId))
                .map(filmStorage::getById)
                .peek(film -> film.setMpa(mpaService.getById(film.getMpa().getId())))
                .peek(film -> film.setGenres(genreService.genreStorage.getGenresByFilmId(film.getId())))
                .peek(film -> film.setLikesUser(filmLikesStorage.getUserLikesFilm(film.getId())))
                .collect(Collectors.toList());
    }

    public void validateFindUserById(Integer id) {
        //TODO: не определился как правильно сделать, мои варианты либо обернуть в Optional, либо перехватывать искл.
        try {
            if (userStorage.getById(id) == null) {
                throw new NotFoundException(String.format("Не найден пользователь с ид %d", id), id);
            }
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException(String.format("Не найден пользователь с ид %d", id), id);
        }
    }
}