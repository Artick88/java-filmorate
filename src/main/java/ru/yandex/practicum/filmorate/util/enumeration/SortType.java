package ru.yandex.practicum.filmorate.util.enumeration;

import ru.yandex.practicum.filmorate.exception.NoSuchEnumException;

public enum SortType {
    LIKES, YEAR;

    public static SortType fromStringIgnoreCase(String data) {
        if (data != null) {
            for (SortType sortType : SortType.values()) {
                if (data.equalsIgnoreCase(sortType.toString())) {
                    return sortType;
                }
            }
        }
        throw new NoSuchEnumException("Не найдено перечисление", data);
    }
}
