CREATE SEQUENCE hibernate_sequence
    INCREMENT BY 100
    START WITH 1000;

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

CREATE TABLE transfers (
    id        bigint not null,
    version   int not null default 0,
    created_at timestamp(3) not null,
    source_account_id bigint not null,
    target_account_id bigint not null,
    amount numeric not null,
    currency varchar(3) not null,
    source_currency_rate numeric not null,
    target_currency_rate numeric not null,
    PRIMARY KEY (id)
);

CREATE INDEX transfers_source_account_id_i on transfers (source_account_id);
CREATE INDEX transfers_target_account_id_i on transfers (target_account_id);
ALTER TABLE transfers
    ADD CONSTRAINT transfers_source_account_id_fk
    FOREIGN KEY (source_account_id) REFERENCES accounts;
ALTER TABLE transfers
    ADD CONSTRAINT transfers_target_account_id_fk
    FOREIGN KEY (target_account_id) REFERENCES accounts;

CREATE TABLE transactions (
    id        bigint not null,
    version   int not null default 0,
    created_at timestamp(3) not null,
    account_id bigint not null,
    tx_type varchar(1) not null,
    amount numeric not null,
    balance_after numeric not null,
    transfer_id bigint not null,
    PRIMARY KEY (id),
    CONSTRAINT transactions_tx_type_ch CHECK ( tx_type in ('D','C') )
);

CREATE INDEX transactions_account_id_i on transactions (account_id);
ALTER TABLE transactions
    ADD CONSTRAINT transactions_account_id_fk
    FOREIGN KEY (account_id) REFERENCES accounts;
CREATE INDEX transactions_transfer_id_i on transactions (transfer_id);
ALTER TABLE transactions
    ADD CONSTRAINT transactions_transfer_id_fk
    FOREIGN KEY (transfer_id) REFERENCES transfers;

