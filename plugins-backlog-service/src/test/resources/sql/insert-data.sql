--create profiles--
insert into tbl_profile (profile_id, first_name, last_name, email_addr, phone_num, profile_created_ts)
select 1, 'Admin', 'User', 'admin.user@host.com', '', datetime('now')
where not exists (select * from tbl_profile where profile_id = 1);

insert into tbl_profile (profile_id, first_name, last_name, email_addr, phone_num, profile_created_ts)
select 2, 'Guest', 'User', 'guest.user@host.com', '', datetime('now')
where not exists (select * from tbl_profile where profile_id = 2);

--create accounts--
insert into tbl_account (account_id, username, password, acc_role, acc_status, account_profile, account_created_ts)
select 1, 'admin', 'p455w0rd', 'admin', 'active', 1, datetime('now')
where not exists (select * from tbl_account where account_id = 1);

insert into tbl_account (account_id, username, password, acc_role, acc_status, account_profile, account_created_ts)
select 2, 'guest', 'p455w0rd', 'guest', 'active', 2, datetime('now')
where not exists (select * from tbl_account where account_id = 2);

--create login_status--
insert into tbl_login_status (fk_account_id, acc_login_token, login_attempts, acc_status_info, status_created_ts, lock_expiry_ts, login_success_ts)
select 1, null,  1,  'wrong credentials used', datetime('now'), null, null
where (select count(fk_account_id) from tbl_login_status where fk_account_id = 1) < 1;

insert into tbl_login_status (fk_account_id, acc_login_token, login_attempts, acc_status_info, status_created_ts, lock_expiry_ts, login_success_ts)
select 1, 'you_are_good_to_go',  2, 'account is active', datetime('now'), null, datetime('now')
where (select count(fk_account_id) from tbl_login_status where fk_account_id = 1) < 2;

--create backlog_list--
insert into tbl_backlog_list (list_id, list_title, list_owner, list_created_ts)
select 1, "outdoors", 1, datetime('now')
where not exists (select * from tbl_backlog_list where list_id = 1);

insert into tbl_backlog_list (list_id, list_title, list_owner, list_created_ts)
select 2, "grocery", 2, datetime('now')
where not exists (select * from tbl_backlog_list where list_id = 2);

insert into tbl_backlog_list (list_id, list_title, list_owner, list_created_ts)
select 3, "workout", 1, datetime('now')
where not exists (select * from tbl_backlog_list where list_id = 3);

insert into tbl_backlog_list (list_id, list_title, list_owner, list_created_ts)
select 4, "school", 1, datetime('now')
where not exists (select * from tbl_backlog_list where list_id = 4);

--create backlog_items--
insert into tbl_backlog_item (item_id, item_name, is_done, fk_list_id, item_created_ts)
select 1, 'grill', 'false', 1, datetime('now')
where not exists (select * from tbl_backlog_item where item_id = 1);

insert into tbl_backlog_item (item_id, item_name, is_done, fk_list_id, item_created_ts)
select 2, 'charcoal', 'true', 1, datetime('now')
where not exists (select * from tbl_backlog_item where item_id = 2);

insert into tbl_backlog_item (item_id, item_name, is_done, fk_list_id, item_created_ts)
select 3, 'milk', 'false', 2, datetime('now')
where not exists (select * from tbl_backlog_item where item_id = 3);

insert into tbl_backlog_item (item_id, item_name, is_done, fk_list_id, item_created_ts)
select 4, 'bread', 'false', 2, datetime('now')
where not exists (select * from tbl_backlog_item where item_id = 4);

insert into tbl_backlog_item (item_id, item_name, is_done, fk_list_id, item_created_ts)
select 5, 'bike', 'false', 3, datetime('now')
where not exists (select * from tbl_backlog_item where item_id = 5);

insert into tbl_backlog_item (item_id, item_name, is_done, fk_list_id, item_created_ts)
select 6, 'sneakers', 'true', 3, datetime('now')
where not exists (select * from tbl_backlog_item where item_id = 6);

--insert blog post--;
insert into tbl_blog_post (blog_id, blog_title, blog_summary, blog_author, blog_tags, is_published, blog_created_ts) 
select 1, 'Maths addition', 'Addition is the sum of numbers', 1, 'chemistry', 0, datetime('now')
where not exists (select blog_id from tbl_blog_post where blog_id = 1);
insert into tbl_blog_post (blog_id, blog_title, blog_summary, blog_author, blog_tags, is_published, blog_created_ts) 
select 2, 'Maths subtraction', 'Subtraction is the difference of numbers', 1, 'physics, math', 0, datetime('now')
where not exists (select blog_id from tbl_blog_post where blog_id = 2);
insert into tbl_blog_post (blog_id, blog_title, blog_summary, blog_author, blog_tags, is_published, blog_created_ts) 
select 3, 'Maths factorial', 'Factorial is the sum of multiples of numbers', 1, 'biology, math', 0, datetime('now')
where not exists (select blog_id from tbl_blog_post where blog_id = 3);
insert into tbl_blog_post (blog_id, blog_title, blog_summary, blog_author, blog_tags, is_published, blog_created_ts) 
select 4, 'Maths Multiplication', 'Multiplication is the sum of numbers repeated by a factor', 2, 'science, vectors', 0, datetime('now')
where not exists (select blog_id from tbl_blog_post where blog_id = 4);
insert into tbl_blog_post (blog_id, blog_title, blog_summary, blog_author, blog_tags, is_published, blog_created_ts) 
select 5, 'Maths Division', 'Divison is reducing numbers by a given factor', 2, 'sociology, numbers', 0, datetime('now')
where not exists (select blog_id from tbl_blog_post where blog_id = 5);

--insert blog content--;
insert into tbl_blog_content (fk_blog_id, blog_page, page_created_ts, blog_text) 
select 1, 1, datetime('now'), 'Addition is plentiful all sea fish!'
where not exists (select fk_blog_id from tbl_blog_content where fk_blog_id = 1);
insert into tbl_blog_content (fk_blog_id, blog_page, page_created_ts, blog_text) 
select 2, 1, datetime('now'), 'Subtraction rocks like mountain climbing!'
where not exists (select fk_blog_id from tbl_blog_content where fk_blog_id = 2);
insert into tbl_blog_content (fk_blog_id, blog_page, page_created_ts, blog_text) 
select 3, 1, datetime('now'), 'Factorial is colorful like a rainbow!'
where not exists (select fk_blog_id from tbl_blog_content where fk_blog_id = 3);
insert into tbl_blog_content (fk_blog_id, blog_page, page_created_ts, blog_text) 
select 4, 1, datetime('now'), 'Multiplication is blissful like a humans!'
where not exists (select fk_blog_id from tbl_blog_content where fk_blog_id = 4);
insert into tbl_blog_content (fk_blog_id, blog_page, page_created_ts, blog_text) 
select 5, 1, datetime('now'), 'Division is oppressive like dictators!'
where not exists (select fk_blog_id from tbl_blog_content where fk_blog_id = 5); 

--insert blog comment--;
insert into tbl_blog_comment (comment_id, parent_blog, parent_comment, comment_author, comment_text, is_published, comment_created_ts) 
select 1, 1, null, 2, 'Nice work', 0, datetime('now')
where not exists (select comment_id from tbl_blog_comment where comment_id = 1);
insert into tbl_blog_comment (comment_id, parent_blog, parent_comment, comment_author, comment_text, is_published, comment_created_ts) 
select 2, 1, 1, 2, 'Nice feedback', 0, datetime('now')
where not exists (select comment_id from tbl_blog_comment where comment_id = 2);
