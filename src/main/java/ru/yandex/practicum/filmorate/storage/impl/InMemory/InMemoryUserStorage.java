package ru.yandex.practicum.filmorate.storage.impl.InMemory;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.user.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

@Slf4j
@Component
public class InMemoryUserStorage extends InMemoryBaseStorage<User> implements UserStorage {
}