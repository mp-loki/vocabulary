# --- !Ups
create sequence profiles_seq;

create table "profiles" (
  "id" bigint not null default nextval('profiles_seq') primary key,
  "email" varchar not null unique,
  "name" varchar not null,
  "nativeLang" varchar
);
# --- !Downs

drop table  if exists "profiles";
drop sequence if exists "profiles_seq";