CREATE TABLE "film" (
  "id" integer PRIMARY KEY,
  "name" varchar,
  "description" text,
  "release_date" date,
  "duration" integer,
  "MPA_id" integer,
  "created_at" timestamp
);

CREATE TABLE "MPA" (
  "id" integer PRIMARY KEY,
  "code" varchar,
  "description" text,
  "created_at" timestamp
);

CREATE TABLE "genre" (
  "id" integer PRIMARY KEY,
  "code" varchar,
  "name" varchar,
  "description" text,
  "created_at" timestamp
);

CREATE TABLE "film_genre" (
  "id" integer PRIMARY KEY,
  "film_id" integer,
  "genre_id" integer,
  "created_at" timestamp
);

CREATE TABLE "film_likes" (
  "id" integer PRIMARY KEY,
  "film_id" integer,
  "user_id" integer,
  "created_at" timestamp
);

CREATE TABLE "user" (
  "id" integer PRIMARY KEY,
  "email" varchar,
  "login" varchar,
  "name" varchar,
  "birthday" date,
  "created_at" timestamp
);

CREATE TABLE "user_friend" (
  "id" integer PRIMARY KEY,
  "user_from_id" integer,
  "user_to_id" integer,
  "status_id" integer,
  "created_at" timestamp
);

CREATE TABLE "status_friends_type" (
  "id" integer PRIMARY KEY,
  "code" varchar,
  "name" varchar,
  "description" text,
  "created_at" timestamp
);

ALTER TABLE "film" ADD FOREIGN KEY ("MPA_id") REFERENCES "MPA" ("id");

ALTER TABLE "film_genre" ADD FOREIGN KEY ("film_id") REFERENCES "film" ("id");

ALTER TABLE "film_genre" ADD FOREIGN KEY ("genre_id") REFERENCES "genre" ("id");

ALTER TABLE "film_likes" ADD FOREIGN KEY ("film_id") REFERENCES "film" ("id");

ALTER TABLE "film_likes" ADD FOREIGN KEY ("user_id") REFERENCES "user" ("id");

ALTER TABLE "user_friend" ADD FOREIGN KEY ("user_from_id") REFERENCES "user" ("id");

ALTER TABLE "user_friend" ADD FOREIGN KEY ("user_to_id") REFERENCES "user" ("id");

ALTER TABLE "user_friend" ADD FOREIGN KEY ("status_id") REFERENCES "status_friends_type" ("id");