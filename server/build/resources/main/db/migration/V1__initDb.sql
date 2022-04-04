create table app_user
(
    id               bigint       not null
        constraint app_user_pkey
            primary key,
    username         varchar(255) not null,
    password         varchar(255),
    cloud_id         varchar(255) not null,
    email            varchar(255) not null,
    application_id   bigint       not null,
    organization_id  bigint       not null,
    created_by       bigint       not null,
    created_at       timestamp    not null,
    last_modified_at timestamp    not null,
    deleted_at       timestamp
);

create table password_reset_request
(
    id               bigint       not null
        constraint password_reset_request_pkey
            primary key,
    email            varchar(255) not null,
    token            varchar(255) not null,
    app_user         bigint       not null,
    created_at       timestamp    not null,
    last_modified_at timestamp    not null,
    deleted_at       timestamp
);

create table persistent_command
(
    id                  bigint       not null
        constraint persistent_command_pkey
            primary key,
    body                text         not null,
    created_at          timestamp    not null,
    created_by          bigint       not null,
    destination_service varchar(255),
    executing_user      text         not null,
    execution_retries   integer      not null,
    idempotency_id      varchar(255)
        constraint persistent_command_idempotency_id_ukey
            unique,
    last_error_message  text,
    last_modified_at    timestamp    not null,
    message_id          varchar(255) not null,
    state               varchar(255) not null,
    tracking_id         varchar(255),
    type                varchar(255),
    version             bigint       not null
);

create table persistent_command_archive
(
    id                  bigint       not null
        constraint persistent_command_archive_pkey
            primary key,
    body                text         not null,
    created_at          timestamp    not null,
    created_by          bigint       not null,
    destination_service varchar(255),
    executing_user      text         not null,
    execution_retries   integer      not null,
    idempotency_id      varchar(255)
        constraint persistent_command_archive_idempotency_id_ukey
            unique,
    last_error_message  text,
    last_modified_at    timestamp    not null,
    message_id          varchar(255) not null,
    state               varchar(255) not null,
    tracking_id         varchar(255),
    type                varchar(255),
    version             bigint       not null
);

create table hibernate_sequences
(
    sequence_name          varchar(255) not null
        constraint hibernate_sequences_pkey
            primary key,
    sequence_next_hi_value bigint
);
