create table admission_application
(
    AAID              varchar(100) not null
        primary key,
    AAtype            int          null,
    AA_UID            varchar(100) null,
    AA_RID            varchar(100) null,
    AAtime            datetime     null,
    AAlastUpdateTime  datetime     null,
    AAname            varchar(100) null,
    AAinstitution     varchar(100) null,
    AAemail           varchar(100) null,
    AAinterestedAreas varchar(200) null,
    AAhomepage        varchar(200) null,
    AAintroduction    longtext     null,
    AAccept           int          null,
    AOption           varchar(300) null,
    constraint admission_application_AAID_uindex
        unique (AAID)
);

create table collect_records
(
    CRID    varchar(100) not null
        primary key,
    CR_CTID varchar(100) null,
    CR_PID  varchar(100) null,
    constraint collect_records_CRID_uindex
        unique (CRID)
);

create table collected
(
    CTID   varchar(100) not null
        primary key,
    CTname varchar(100) null,
    CT_UID varchar(100) null,
    constraint collected_CTID_uindex
        unique (CTID)
);

create table comment
(
    CID      varchar(100) not null
        primary key,
    C_UID    varchar(100) null,
    C_PID    varchar(100) null,
    Ccontent longtext     null,
    Ctime    datetime     null,
    Clikes   int          null,
    Ctop     tinyint(1)   null,
    constraint comment_CID_uindex
        unique (CID)
);

create table system_tags
(
    STID   varchar(100) not null
        primary key,
    STname varchar(100) null,
    constraint system_tags_STID_uindex
        unique (STID)
);

create table user
(
    UID       varchar(100) not null
        primary key,
    Uname     varchar(75)  null,
    Upassword varchar(75)  null,
    Uemail    varchar(100) null,
    Ubio      longtext     null,
    Utype     varchar(50)  null,
    constraint user_UID_uindex
        unique (UID)
);

