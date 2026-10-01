create table accounts(
account_id uuid primary key,
"name" varchar(100) not null,
status varchar(32) not null,
total_amount_rub numeric(19,2),
updated_at timestamptz not null
);

create table orders(
order_id uuid primary key,
account_id uuid not null,
instrument_id uuid not null,
direction varchar(8) not null,
quantity bigint not null,
order_type varchar(16) not null,
price numeric(19,9),
status varchar(32) not null,
updated_at timestamptz not null,

constraint fk_orders_account
foreign key (account_id) references accounts (account_id)
);

create index idx_orders_acount_id on orders (account_id);