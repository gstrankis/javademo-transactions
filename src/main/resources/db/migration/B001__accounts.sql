CREATE TABLE accounts (
    id        bigint not null,
    version   int not null default 0,
    client_id bigint not null,
    currency  varchar(3) not null,
    balance   numeric not null default 0,
    PRIMARY KEY (id)
);

CREATE SEQUENCE accounts_seq INCREMENT BY 50;
