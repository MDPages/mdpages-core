create table folder
(
    is_root bit    not null,
    id      bigint not null
        primary key
);

create table md_object
(
    id          bigint auto_increment primary key,
    full_path   varchar(2048) not null,
    name        varchar(64)   not null,
    object_type int           not null,
    parent_id   bigint        null,
    constraint uk_md_object_full_path unique (full_path) using hash,
    constraint fk_md_object_parent__folder foreign key (parent_id) references folder (id)
);

alter table folder
    add constraint fk_folder__md_object
        foreign key (id) references md_object (id);

create table folder_children
(
    folder_id   bigint not null,
    children_id bigint not null,
    primary key (folder_id, children_id),
    constraint uk_folder_children_children_id
        unique (children_id),
    constraint fk_folder_children__md_object foreign key (children_id) references md_object (id),
    constraint fk_folder_children__folder foreign key (folder_id) references folder (id)
);

create table page
(
    content     text     not null,
    created     datetime not null,
    last_edited datetime not null,
    id          bigint   not null
        primary key,
    constraint fk_page__md_object foreign key (id) references md_object (id)
);

create table user
(
    id       bigint auto_increment
        primary key,
    email    varchar(64)  null,
    password varchar(160) not null,
    username varchar(32)  not null,
    constraint uk_user_username unique (username)
);

create table permission
(
    id                bigint auto_increment
        primary key,
    permission_target int    null,
    permission_type   int    not null,
    scope_id          bigint not null,
    user_id           bigint null,
    constraint fk_permission_scope__folder
        foreign key (scope_id) references folder (id),
    constraint fk_permission_user__user
        foreign key (user_id) references user (id)
);

create table refresh_token
(
    id              bigint auto_increment
        primary key,
    creation_date   datetime(6)  not null,
    expiration_date datetime(6)  not null,
    token           varchar(255) not null,
    user_id         bigint       null,
    constraint uk_refresh_token_token
        unique (token),
    constraint fk_refresh_token__user
        foreign key (user_id) references user (id)
);

