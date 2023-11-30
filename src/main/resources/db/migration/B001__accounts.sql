CREATE TABLE accounts (
    id        bigint not null,
    version   int not null default 0,
    client_id bigint,
    currency  varchar(3),
    balance   numeric,
    PRIMARY KEY (id)
);

CREATE SEQUENCE accounts_seq INCREMENT BY 50;
