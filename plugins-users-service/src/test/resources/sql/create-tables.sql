--recreate tables--
drop table if exists tbl_todo_item;
drop table if exists tbl_todo_list;

drop table if exists tbl_blog_comment;
drop table if exists tbl_blog_content;
drop table if exists tbl_blog_post;

drop table if exists tbl_login_status;
drop table if exists tbl_account;
drop table if exists tbl_profile;

--create tbl_profile table--
create table if not exists tbl_profile (
  profile_id integer primary key,
  first_name text,
  last_name text,
  email_addr text not null unique,
  phone_num text,
  profile_created_ts text not null default(datetime('now'))
);

--create tbl_account table--
create table if not exists tbl_account (
  account_id integer primary key,
  username text not null unique,
  password text not null,
  account_profile integer not null,
  acc_role text default 'user',
  acc_status text default 'unverified',
  account_created_ts text not null default(datetime('now')),
  foreign key (account_profile) references tbl_profile(profile_id)
);

--create tbl_login_status table--
create table if not exists tbl_login_status (
  fk_account_id integer,
  acc_login_token text,
  lock_expiry_ts text default(datetime('now')),
  acc_status_info text,
  login_attempts integer default 0,
  login_success_ts text,
  status_created_ts text not null default (datetime('now')),
  foreign key (fk_account_id) references tbl_account(account_id)
);

--create tbl_todo_list table--
create table if not exists tbl_todo_list(
  list_id integer primary key,
  list_title text not null,
  list_owner integer not null,
  list_created_ts text not null DEFAULT (datetime('now') ),
  foreign key (list_owner) references tbl_account(account_id)
 );

--create tbl_todo_item table--
create table if not exists tbl_todo_item(
  item_id integer primary key,
  item_name integer not null,
  is_done boolean not null default false,
  fk_list_id integer not null,
  item_created_ts text not null DEFAULT (datetime('now') ),
  foreign key (fk_list_id) references tbl_todo_list(list_id)
);
 
 --create tbl_blog_post table--
create table if not exists tbl_blog_post(
  blog_id integer primary key,
  blog_title text not null,
  blog_summary text,
  blog_author integer not null,
  blog_tags text,
  is_published integer not null default false,
  blog_created_ts text not null DEFAULT (datetime('now') ),
  foreign key (blog_author) references tbl_profile(profile_id)
 );
 
 --create tbl_blog_content table--
create table if not exists tbl_blog_content(
  fk_blog_id integer not null,
  blog_page integer no null default 1,
  blog_text text not null,
  page_created_ts text not null DEFAULT (datetime('now') ),
  unique(fk_blog_id, blog_page) ON CONFLICT REPLACE,
  foreign key (fk_blog_id) references tbl_blog_post(blog_id) ON DELETE CASCADE
 );

--create tbl_comment table-
create table if not exists tbl_blog_comment(
  comment_id integer primary key,
  parent_blog integer not null,
  parent_comment integer default null,
  comment_author integer not null,
  comment_text text not null,
  is_published boolean default false,
  comment_created_ts text not null DEFAULT (datetime('now') ),
  foreign key (comment_author) references tbl_profile(profile_id) ON DELETE CASCADE,
  foreign key (parent_blog) references tbl_blog_post(blog_id) ON DELETE CASCADE,
  foreign key (parent_comment) references tbl_blog_comment(comment_id) ON DELETE CASCADE
 );