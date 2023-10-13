package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.exception.NotFoundException;

import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    private static final String URL_BASE = "/users";

    @Autowired
    MockMvc mockMvc;

    @Autowired
    UserController userController;

    @BeforeEach
    public void init() {
        userController.newId = 0;
        userController.getAll().clear();
    }

    @Test
    void createSuccess() throws Exception {
        String body = "{" +
                "  \"login\": \"lore\"," +
                "  \"name\": \"Nick Name\"," +
                "  \"email\": \"mail@mail.ru\"," +
                "  \"birthday\": \"1946-08-20\"" +
                "}";

        mockMvc.perform(post(URL_BASE)
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mockMvc.perform(get(URL_BASE)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(List.of(body).toString()));
    }

    @Test
    public void createFailLogin() throws Exception {
        String body = "{" +
                "  \"login\": \"do lore McCull-och\"," +
                "  \"email\": \"yandex@mail.ru\"," +
                "  \"birthday\": \"2446-08-20\"" +
                "}";

        mockMvc.perform(post(URL_BASE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void createFailEmail() throws Exception {
        String body = "{" +
                "  \"login\": \"do lore\"," +
                "  \"name\": \"\"," +
                "  \"email\": \"mail.ru\"," +
                "  \"birthday\": \"1980-08-20\"" +
                "}";

        mockMvc.perform(post(URL_BASE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void createFailBirthday() throws Exception {
        String body = "{" +
                "  \"login\": \"lore\"," +
                "  \"name\": \"\"," +
                "  \"email\": \"test@mail.ru\"," +
                "  \"birthday\": \"2446-08-20\"" +
                "}";

        mockMvc.perform(post(URL_BASE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void createNameEmpty() throws Exception {
        String body = "{" +
                "  \"login\": \"common\"," +
                "  \"email\": \"friend@common.ru\"," +
                "  \"birthday\": \"2000-08-20\"" +
                "}";

        mockMvc.perform(post(URL_BASE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("common"));
    }

    @Test
    public void updateNotFound() throws Exception {
        String body = "{" +
                "  \"login\": \"loreUpdate\"," +
                "  \"name\": \"est radicalising\"," +
                "  \"id\": -1," +
                "  \"email\": \"mail@yandex.ru\"," +
                "  \"birthday\": \"1976-09-20\"" +
                "}";

        assertEquals(NotFoundException.class, Objects.requireNonNull(mockMvc.perform(put(URL_BASE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isNotFound())
                .andReturn().getResolvedException()).getClass());
    }

    @Test
    public void updateSuccess() throws Exception {
        String bodyCreate = "{" +
                "  \"login\": \"create\"," +
                "  \"name\": \"create\"," +
                "  \"email\": \"create@create.ru\"," +
                "  \"birthday\": \"1976-09-20\"" +
                "}";

        String bodyUpdate = "{" +
                "  \"login\": \"update\"," +
                "  \"name\": \"update\"," +
                "  \"id\": 1," +
                "  \"email\": \"update@update.ru\"," +
                "  \"birthday\": \"1976-09-20\"" +
                "}";

        mockMvc.perform(post(URL_BASE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bodyCreate))
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
    public void getAllSuccess() throws Exception {
        String body = "{" +
                "  \"login\": \"create\"," +
                "  \"name\": \"create\"," +
                "  \"email\": \"create@create.ru\"," +
                "  \"birthday\": \"1976-09-20\"" +
                "}";

        mockMvc.perform(post(URL_BASE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk());

        mockMvc.perform(get(URL_BASE)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(List.of(body).toString()));
    }
}