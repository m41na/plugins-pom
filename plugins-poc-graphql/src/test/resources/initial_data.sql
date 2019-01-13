merge into tbl_author (author_id, first_name, last_name, author_bio, birth_day, addr_city, addr_state, street_name, unit_num, zip_code)
key (author_id)
VALUES 
(1, 'james', 'bond', 'gunslinger', TO_DATE(substr('2015-12-12 08:00 A.M.', 1, 10), 'yyyy-MM-DD'), '', '', '', '', ''),
(2, 'john', 'bean', 'mountain climber', TO_DATE(substr('2015-12-12 08:00 A.M.', 1, 10), 'yyyy-MM-DD'), '', '', '', '', '');

merge into tbl_account (account_id, account_user, account_pass, email_addr, account_profile) 
key (account_id) 
VALUES 
(1, 'admin', 'secret', 'steve@play.io', 1),
(2, 'guest', 'wisdom', 'guestdom@play.io', 2);

merge into tbl_blog (pub_id, pub_title, publish_date, pub_preface, blog_content, fk_author_id, fk_parent_blog) 
key (pub_id) 
VALUES 
(1, 'simple blog', TO_DATE(substr('2015-10-10 08:00 A.M.', 1, 10), 'yyyy-MM-DD'), 'react rocks', 'let"s begin somewhere', 1, null),
(2, 'simple graphs', TO_DATE(substr('2015-10-11 08:00 A.M.', 1, 10), 'yyyy-MM-DD'), 'html5 rocks', 'let"s draw a box', 1, 1),
(3, 'writting skills', TO_DATE(substr('2015-10-11 08:00 A.M.', 1, 10), 'yyyy-MM-DD'), 'cursive form', 'it"s not scribbles', 2, null),
(4, 'reading skills', TO_DATE(substr('2015-10-11 08:00 A.M.', 1, 10), 'yyyy-MM-DD'), 'ambient light', 'stay motivated', 2, 3),
(5, 'recall skills', TO_DATE(substr('2015-10-11 08:00 A.M.', 1, 10), 'yyyy-MM-DD'), 'alert mind', 'paying attention to detail', 2, 3);

merge into tbl_comment (comment_id, comment_text, comment_date, fk_author_id, fk_parent_blog, fk_parent_comment) 
key (comment_id) 
VALUES 
(1, 'simple blog is awesome', TO_DATE(substr('2015-10-10 08:00 A.M.', 1, 10), 'yyyy-MM-DD'), 2, 1, null),
(2, 'much appreciated', TO_DATE(substr('2015-10-11 08:00 A.M.', 1, 10), 'yyyy-MM-DD'), 1, 1, 1);

merge into tbl_ebook (pub_id, pub_title, publish_date, pub_preface) 
key (pub_id) 
VALUES 
(1, 'simple ebook', TO_DATE(substr('2015-10-10 08:00 A.M.', 1, 10), 'yyyy-MM-DD'), 'books are worth a million stories');

merge into tbl_ebook_author (author_id, pub_id)
key(author_id, pub_id)
VALUES
(1, 1);

merge into tbl_chapter (chapter_id, fk_ebook_id, book_content)
key(chapter_id, fk_ebook_id)
VALUES
(1, 1, 'A gentle start to ebooks');
