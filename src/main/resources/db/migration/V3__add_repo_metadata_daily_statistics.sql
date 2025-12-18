create table github_repo_topic_statistic_daily
(
    id            bigint auto_increment primary key,
    topic         varchar(255) not null,
    repo_count    int          not null,
    statistic_day datetime(6)  not null,
    created_at    datetime(6)  not null,
    updated_at    datetime(6)  not null,
    constraint UK_topic_day unique (topic, statistic_day)
);

create table github_repo_language_statistic_daily
(
    id            bigint auto_increment primary key,
    language      varchar(255) not null,
    repo_count    int          not null,
    statistic_day datetime(6)  not null,
    created_at    datetime(6)  not null,
    updated_at    datetime(6)  not null,
    constraint UK_language_day unique (language, statistic_day)
);