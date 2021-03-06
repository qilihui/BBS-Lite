create table article
(
    id BIGINT auto_increment primary key,
    title varchar(50),
    description text,
    creator bigint,
    tag varchar(256),
    top tinyint default 0,
    topic varchar(10),
    category varchar(10),

    user_avatar_url varchar(100),
    user_name varchar(20),

    follow_count bigint default 0,
    view_count bigint default 0,
    like_count bigint default 0,
    comment_count bigint default 0,

    status tinyint default 1,
    gmt_create bigint,
    gmt_modified bigint
);