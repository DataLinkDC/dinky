create table category
(
    sub_category_id      bigint       not null
        primary key,
    parent_category_name varchar(255) null
) collate = utf8_unicode_ci;

