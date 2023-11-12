--Рейтинги
INSERT INTO MPA("name", "description")
SELECT 'G', 'Нет возрастных ограничений' WHERE NOT EXISTS (SELECT 1 FROM MPA WHERE "name" = 'G');
INSERT INTO MPA("name", "description")
SELECT 'PG', 'Рекомендуется присутствие родителей' WHERE NOT EXISTS (SELECT 1 FROM MPA WHERE "name" = 'PG');
INSERT INTO MPA("name", "description")
SELECT 'PG-13', 'Детям до 13 лет просмотр не желателен' WHERE NOT EXISTS (SELECT 1 FROM MPA WHERE "name" = 'PG-13');
INSERT INTO MPA("name", "description")
SELECT 'R', 'Лицам до 17 лет обязательно присутствие взрослого' WHERE NOT EXISTS (SELECT 1 FROM MPA WHERE "name" = 'R');
INSERT INTO MPA("name", "description")
SELECT 'NC-17', 'Лицам до 18 лет просмотр запрещен' WHERE NOT EXISTS (SELECT 1 FROM MPA WHERE "name" = 'NC-17');

--Жанры
INSERT INTO "genre" ("code", "name", "description")
SELECT 'comedy', 'Комедия', 'Комедия' WHERE NOT EXISTS (SELECT 1 FROM "genre" WHERE "code" = 'comedy');
INSERT INTO "genre" ("code", "name", "description")
SELECT 'drama', 'Драма', 'Драма' WHERE NOT EXISTS (SELECT 1 FROM "genre" WHERE "code" = 'drama');
INSERT INTO "genre" ("code", "name", "description")
SELECT 'cartoon', 'Мультфильм', 'Мультфильм' WHERE NOT EXISTS (SELECT 1 FROM "genre" WHERE "code" = 'cartoon');
INSERT INTO "genre" ("code", "name", "description")
SELECT 'thriller', 'Триллер', 'Триллер' WHERE NOT EXISTS (SELECT 1 FROM "genre" WHERE "code" = 'thriller');
INSERT INTO "genre" ("code", "name", "description")
SELECT 'documentary', 'Документальный', 'Документальный фильм' WHERE NOT EXISTS (SELECT 1 FROM "genre" WHERE "code" = 'documentary');
INSERT INTO "genre" ("code", "name", "description")
SELECT 'action', 'Боевик', 'Боевик, экшен' WHERE NOT EXISTS (SELECT 1 FROM "genre" WHERE "code" = 'action');

--Статус заявок в друзья
INSERT INTO "status_type" ("code", "name", "description")
      select 'Not_Approved', 'Не подтверждено', 'Заявки на дружбу не подтверждена получателем' WHERE NOT EXISTS (SELECT 1 FROM "status_type" WHERE "code" = 'Not_Approved')
union select 'Approved', 'Подтверждено', 'Заявки на дружбу подтверждена получателем' WHERE NOT EXISTS (SELECT 1 FROM "status_type" WHERE "code" = 'Approved');