package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.user.User;
import ru.yandex.practicum.filmorate.model.user.UserFriends;
import ru.yandex.practicum.filmorate.storage.impl.InMemory.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

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
    @Autowired
    InMemoryUserStorage inMemoryUserStorage;

    @BeforeEach
    public void init() {
        inMemoryUserStorage.resetId();
        userController.userService.getAll().clear();
    }

    public void createDefaultUser() {
        User user = User.builder()
                .name("name")
                .birthday(LocalDate.of(1997, 1, 1))
                .login("login")
                .name("name")
                .friends(new HashSet<>())
                .build();

        userController.userService.create(user);
    }

    public void addDefaultFriend() {
        createDefaultUser();
        createDefaultUser();

        userController.userService.addFriend(1, 2);
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
        createDefaultUser();

        String bodyUpdate = "{" +
                "  \"login\": \"update\"," +
                "  \"name\": \"update\"," +
                "  \"id\": 1," +
                "  \"email\": \"update@update.ru\"," +
                "  \"birthday\": \"1976-09-20\"" +
                "}";

        mockMvc.perform(put(URL_BASE)
                        .content(bodyUpdate)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void getAllSuccess() throws Exception {
        createDefaultUser();

        mockMvc.perform(get(URL_BASE)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("1"))
                .andExpect(jsonPath("$[0].name").value("name"))
                .andExpect(jsonPath("$[0].login").value("login"))
                .andExpect(jsonPath("$[0].friends").isEmpty());
    }

    @Test
    public void getUserByIdSuccess() throws Exception {
        createDefaultUser();

        mockMvc.perform(get(URL_BASE + "/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void getUserByIdNotFound() throws Exception {
        mockMvc.perform(get(URL_BASE + "/-1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void addFriendSuccess() throws Exception {
        createDefaultUser();
        createDefaultUser();

        mockMvc.perform(put(URL_BASE + "/1/friends/2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        assertFalse(userController.userService.getUserById(1).getFriends().isEmpty());
    }

    @Test
    public void addFriendNotFound() throws Exception {
        mockMvc.perform(put(URL_BASE + "/-1/friends/-2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void deleteFriendSuccess() throws Exception {
        addDefaultFriend();

        mockMvc.perform(delete(URL_BASE + "/1/friends/2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        assertTrue(userController.userService.getUserById(1).getFriends().isEmpty());
        assertTrue(userController.userService.getUserById(2).getFriends().isEmpty());
    }

    @Test
    public void deleteFriendNotFound() throws Exception {
        mockMvc.perform(delete(URL_BASE + "/-1/friends/-2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getFriendsSuccess() throws Exception {
        addDefaultFriend();

        mockMvc.perform(get(URL_BASE + "/1/friends")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("2"));
    }

    @Test
    public void getFriendsEmpty() throws Exception {
        createDefaultUser();

        mockMvc.perform(get(URL_BASE + "/1/friends")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    public void getCommonFriendsEmpty() throws Exception {
        createDefaultUser();
        createDefaultUser();

        mockMvc.perform(get(URL_BASE + "/1/friends/common/2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    public void getCommonFriendsSuccess() throws Exception {
        addDefaultFriend();

        User user = User.builder()
                .name("name")
                .login("login")
                .birthday(LocalDate.of(2000, 1, 1))
                .friends(Set.of(UserFriends.builder()
                        .userId(2)
                        .build()))
                .build();

        userController.userService.create(user);

        mockMvc.perform(get(URL_BASE + "/1/friends/common/3")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("2"));
    }
}