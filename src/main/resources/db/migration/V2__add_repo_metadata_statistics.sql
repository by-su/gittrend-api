create table github_repo_topic_statistic_hourly
(
    id             bigint auto_increment primary key,
    topic          varchar(255) not null,
    repo_count     int          not null,
    statistic_hour datetime(6)  not null,
    created_at     datetime(6)  not null,
    updated_at     datetime(6)  not null,
    constraint UK_topic_hour unique (topic, statistic_hour)
);

create table github_repo_language_statistic_hourly
(
    id             bigint auto_increment primary key,
    language       varchar(255) not null,
    repo_count     int          not null,
    statistic_hour datetime(6)  not null,
    created_at     datetime(6)  not null,
    updated_at     datetime(6)  not null,
    constraint UK_language_hour unique (language, statistic_hour)
);