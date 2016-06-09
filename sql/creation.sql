create table logtable(
    DATED                   DATE           NOT NULL,
    LOGGER                  VARCHAR(100)   NOT NULL,
    LEVEL                   VARCHAR(100)   NOT NULL,
    PROCESS                 VARCHAR(1000)  NOT NULL,
    INTERNALRUNTIME         VARCHAR(1000)  NOT NULL,
    MESSAGE                 VARCHAR(1000)  NOT NULL
);

create table if not exists Cleaners (id INTEGER PRIMARY KEY AUTOINCREMENT, name varchar, surname varchar, reference varchar, lastupdate timestamp);