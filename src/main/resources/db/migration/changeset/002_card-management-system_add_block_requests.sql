create table if not exists card_block_requests (
    id UUID primary key,
    card_id UUID not null,
    username varchar(50) not null,
    datetime timestamp,
    status varchar(10) not null
)