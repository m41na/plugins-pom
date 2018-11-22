--recreate tables--
drop table if exists tbl_users;

--create tbl_users table--
create table if not exists tbl_users (
  user_id integer primary key,
  first_name text,
  last_name text,
  email_addr text not null unique,
  phone_num text,
  user_created_ts text not null default(datetime('now'))
);