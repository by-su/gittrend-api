create table github_event
(
    is_public           bit          not null,
    actor_id            bigint       not null,
    created_at          datetime(6)  not null,
    event_id            bigint       null,
    id                  bigint auto_increment primary key,
    push_id             bigint       not null,
    repo_id             bigint       not null,
    actor_avatar_url    varchar(255) null,
    actor_display_login varchar(255) null,
    actor_login         varchar(255) not null,
    actor_url           varchar(255) not null,
    before_sha          varchar(255) null,
    git_ref             varchar(255) not null,
    head_sha            varchar(255) null,
    repo_name           varchar(255) not null,
    repo_url            varchar(255) not null,
    type                varchar(255) not null,
    constraint UKpl6lg6nvcf9dc2byav8qojfr3
        unique (event_id)
);

create table github_event_statistic_daily
(
    id            bigint auto_increment primary key,
    created_at    datetime(6)  not null,
    event_count   int          not null,
    event_type    varchar(255) not null,
    statistic_day datetime(6)  not null,
    updated_at    datetime(6)  not null,
    constraint UKln8yb1tfw8t9alt6ejvqfrg3r
        unique (event_type, statistic_day)
);

create table github_event_statistic_hourly
(
    id             bigint auto_increment primary key,
    created_at     datetime(6)  not null,
    event_count    int          not null,
    event_type     varchar(255) not null,
    statistic_hour datetime(6)  not null,
    updated_at     datetime(6)  not null
);

create table github_repo_metadata
(
    id               bigint auto_increment primary key,
    fork             bit          not null,
    fork_count       int          null,
    network_count    int          null,
    open_issue_count int          null,
    star_count       int          null,
    subscriber_count int          null,
    watcher_count    int          null,
    created_at       datetime(6)  null,
    pushed_at        datetime(6)  null,
    repo_id          bigint       null,
    updated_at       datetime(6)  null,
    description      varchar(500) null,
    language         varchar(255) null,
    license_key      varchar(255) null,
    license_name     varchar(255) null,
    name             varchar(255) null,
    owner            varchar(255) null,
    visibility       varchar(255) null,
    topics           json         null,
    constraint UKlx3b3l1kmomhplr5u6ob0ex1e
        unique (repo_id)
);

