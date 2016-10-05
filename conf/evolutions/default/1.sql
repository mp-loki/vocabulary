# --- !Ups

create sequence people_seq;
create sequence words_seq START 100;

create table "people" (
  "id" bigint not null default nextval('people_seq') primary key,
  "name" varchar not null,
  "age" int not null
);


create table "words" (
  "id" bigint not null default nextval('words_seq') primary key,
  "logos" varchar not null,
  "partOfSpeech" varchar not null,
  "language" varchar not null
);

# --- !Downs

drop table if exists people;
drop table if exists words;
drop sequence if exists people_seq;
drop sequence if exists words_seq;