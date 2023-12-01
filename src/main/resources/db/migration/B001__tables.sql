CREATE SEQUENCE hibernate_sequence INCREMENT BY 100;

CREATE TABLE accounts (
    id        bigint not null,
    version   int not null default 0,
    client_id bigint not null,
    account_nr varchar(50) not null,
    currency  varchar(3) not null,
    balance   numeric not null default 0,
    PRIMARY KEY (id),
    CONSTRAINT accounts_uk UNIQUE (account_nr)
);

CREATE INDEX accounts_account_client_id_i on accounts (client_id);

CREATE TABLE transactions (
    id        bigint not null,
    version   int not null default 0,
    created_at timestamp(3) not null,
    source_account_id bigint not null,
    target_account_id bigint not null,
    amount numeric not null,
    currency varchar(3) not null,
    source_currency_rate numeric not null,
    source_amount numeric not null,
    source_balance_after numeric not null,
    target_balance_after numeric not null,
    PRIMARY KEY (id)
);

CREATE INDEX transactions_source_account_id_i on transactions (source_account_id);
CREATE INDEX transactions_target_account_id_i on transactions (target_account_id);
ALTER TABLE transactions
    ADD CONSTRAINT transactions_source_account_id_fk
    FOREIGN KEY (source_account_id) REFERENCES accounts;
ALTER TABLE transactions
    ADD CONSTRAINT transactions_target_account_id_fk
    FOREIGN KEY (target_account_id) REFERENCES accounts;


