package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class FilmControllerTest {

    private static final String URL_BASE = "/films";

    @Autowired
    MockMvc mockMvc;

    @Autowired
    FilmController filmController;

    @Autowired
    UserController userController;

    @BeforeEach
    public void init() {
        filmController.filmService.getAll().clear();
        filmController.filmService.resetId();
    }

    public void createDefaultUser() {
        User user = User.builder()
                .login("login")
                .name("name")
                .birthday(LocalDate.of(2000, 1, 1))
                .friends(new HashSet<>())
                .build();

        userController.userService.create(user);
    }

    public void createDefaultFilm() {
        Film film = Film.builder()
                .name("name")
                .description("description")
                .releaseDate(LocalDate.of(2020, 1, 1))
                .duration(100L)
                .likesUser(new HashSet<>())
                .build();

        filmController.filmService.create(film);
    }

    public void addDefaultLike() {
        filmController.addLike(1, 1);
    }

    @Test
    void createSuccess() throws Exception {
        String body = "{" +
                "  \"name\": \"nisei usermod\"," +
                "  \"description\": \"radicalising\"," +
                "  \"releaseDate\": \"1967-03-25\"," +
                "  \"duration\": 100" +
                "}";

        mockMvc.perform(post(URL_BASE)
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void createFailName() throws Exception {
        String body = "{" +
                "  \"name\": \"\"," +
                "  \"description\": \"Description\"," +
                "  \"releaseDate\": \"1900-03-25\"," +
                "  \"duration\": 200" +
                "}";

        mockMvc.perform(post(URL_BASE)
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createFailDescription() throws Exception {
        String body = "{" +
                "  \"name\": \"Film name\"," +
                "  \"description\": \" " + "a".repeat(201) + "\"," +
                "  \"releaseDate\": \"1900-03-25\"," +
                "  \"duration\": 200" +
                "}";

        mockMvc.perform(post(URL_BASE)
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createFailReleaseDate() throws Exception {
        String body = "{" +
                "  \"name\": \"Name\"," +
                "  \"description\": \"Description\"," +
                "  \"releaseDate\": \"1890-03-25\"," +
                "  \"duration\": 200" +
                "}";

        mockMvc.perform(post(URL_BASE)
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createFailDuration() throws Exception {
        String body = "{" +
                "  \"name\": \"Name\"," +
                "  \"description\": \"Description\"," +
                "  \"releaseDate\": \"1980-03-25\"," +
                "  \"duration\": -200" +
                "}";

        mockMvc.perform(post(URL_BASE)
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateSuccess() throws Exception {
        createDefaultFilm();

        String bodyUpdate = "{" +
                "  \"id\": 1," +
                "  \"name\": \"Film Updated\"," +
                "  \"releaseDate\": \"1999-12-12\"," +
                "  \"description\": \"New film update description\"," +
                "  \"duration\": 100" +
                "}";

        mockMvc.perform(put(URL_BASE)
                        .content(bodyUpdate)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void updateNotFound() throws Exception {
        String body = "{" +
                "  \"id\": -1," +
                "  \"name\": \"Film Updated\"," +
                "  \"releaseDate\": \"1989-04-17\"," +
                "  \"description\": \"New film update description\"," +
                "  \"duration\": 190" +
                "}";

        assertEquals(NotFoundException.class, Objects.requireNonNull(mockMvc.perform(put(URL_BASE)
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn().getResolvedException()).getClass());
    }

    @Test
    void getAllSuccess() throws Exception {
        createDefaultFilm();

        assertNotNull(mockMvc.perform(get(URL_BASE)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .toString());
    }

    @Test
    public void getFilmByIdSuccess() throws Exception {
        createDefaultFilm();

        mockMvc.perform(get(URL_BASE + "/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"));
    }

    @Test
    public void getFilmByIdNotFound() throws Exception {
        mockMvc.perform(get(URL_BASE + "/-1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void addLikeSuccess() throws Exception {
        createDefaultFilm();
        createDefaultUser();

        mockMvc.perform(put(URL_BASE + "/1/like/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void deleteLikeSuccess() throws Exception {
        createDefaultUser();
        createDefaultFilm();
        addDefaultLike();

        mockMvc.perform(delete(URL_BASE + "/1/like/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void deleteLikeNotFound() throws Exception {
        mockMvc.perform(delete(URL_BASE + "/-1/like/-1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getTop1LikeFilmSuccess() throws Exception {
        createDefaultFilm();
        createDefaultFilm();
        createDefaultUser();
        addDefaultLike();

        mockMvc.perform(get(URL_BASE + "/popular?count=1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("1"));
    }
}