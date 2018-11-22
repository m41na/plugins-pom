--create profiles--
insert into tbl_users (user_id, first_name, last_name, email_addr, phone_num, user_created_ts)
select 1, 'Admin', 'User', 'admin.user@host.com', '123-456-7890', datetime('now')
where not exists (select * from tbl_users where user_id = 1);

insert into tbl_users (user_id, first_name, last_name, email_addr, phone_num, user_created_ts)
select 2, 'Guest', 'User', 'guest.user@host.com', '987-654-3210', datetime('now')
where not exists (select * from tbl_users where user_id = 2);