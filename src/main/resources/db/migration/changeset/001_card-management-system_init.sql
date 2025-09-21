create table if not exists users (
                       id UUID primary key,
                       username varchar(50) not null unique,
                       email varchar(50) not null unique,
                       password varchar(255) not null,
                       role varchar(5) not null
);

create table if not exists cards (
                       id UUID primary key,
                       card_number varchar(50) not null,
                       card_holder varchar(50) not null,
                       expiry_date varchar(5) not null,
                       status varchar(10) not null,
                       balance numeric(15, 2) default 0 not null,
                       user_id UUID,
                       constraint fk_card_user foreign key (user_id) references users(id) on delete cascade
);