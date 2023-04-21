create table gbu
(
    id         varchar(128) null,
    longitude  double       null,
    latitude   double       null,
    dt         timestamp(3) null,
    taskId     varchar(128) null,
    taskStatus int          null,
    v          double       null
);

create table ts
(
    type       text   null,
    id         text   null,
    longitude  double null,
    latitude   int    null,
    dt         text   null,
    v          double null,
    taskId     text   null,
    taskStatus int    null
);

create table `match`
(
    taskId          text null,
    startTaskStatus int  null,
    endTaskStatus   int  null
);

