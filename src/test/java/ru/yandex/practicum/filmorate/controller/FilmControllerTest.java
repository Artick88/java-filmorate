package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.exception.ValidationException;

import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class FilmControllerTest {

    private static final String URL_BASE = "/films";

    @Autowired
    MockMvc mockMvc;

    @Autowired
    FilmController filmController;

    @BeforeEach
    public void init() {
        filmController.id = 0;
        filmController.getAll().clear();
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

        mockMvc.perform(get(URL_BASE)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(List.of(body).toString()));
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
                "  \"description\": \"Пятеро друзей ( комик-группа «Шарло»), приезжают в город Бризуль. Здесь они хотят разыскать господина Огюста Куглова, который задолжал им деньги, а именно 20 миллионов. о Куглов, который за время «своего отсутствия», стал кандидатом Коломбани.\"," +
                "    \"releaseDate\": \"1900-03-25\"," +
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
        String bodyCreate = "{" +
                "  \"name\": \"Film created\"," +
                "  \"releaseDate\": \"1999-04-17\"," +
                "  \"description\": \"film update description\"," +
                "  \"duration\": 190" +
                "}";

        String bodyUpdate = "{" +
                "  \"id\": 1," +
                "  \"name\": \"Film Updated\"," +
                "  \"releaseDate\": \"1999-12-12\"," +
                "  \"description\": \"New film update description\"," +
                "  \"duration\": 100" +
                "}";

        mockMvc.perform(post(URL_BASE)
                        .content(bodyCreate)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mockMvc.perform(put(URL_BASE)
                        .content(bodyUpdate)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mockMvc.perform(get(URL_BASE)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(List.of(bodyUpdate).toString()));
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

        assertEquals(ValidationException.class, Objects.requireNonNull(mockMvc.perform(put(URL_BASE)
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn().getResolvedException()).getClass());
    }

    @Test
    void getAllSuccess() throws Exception {
        String body = "{" +
                "  \"name\": \"Film Updated\"," +
                "  \"releaseDate\": \"1989-04-17\"," +
                "  \"description\": \"New film update description\"," +
                "  \"duration\": 190" +
                "}";

        mockMvc.perform(post(URL_BASE)
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mockMvc.perform(get(URL_BASE)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(List.of(body).toString()));
    }
}