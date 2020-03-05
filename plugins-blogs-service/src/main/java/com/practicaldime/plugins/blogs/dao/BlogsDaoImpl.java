package com.practicaldime.plugins.blogs.dao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.practicaldime.common.util.AResult;
import com.practicaldime.common.util.SqliteDate;
import com.practicaldime.common.entity.blogs.BlogPost;
import com.practicaldime.common.entity.blogs.Comment;
import com.practicaldime.common.entity.users.Profile;

@Repository
public class BlogsDaoImpl implements BlogsDao {

    private final NamedParameterJdbcTemplate template;

    @Autowired
    public BlogsDaoImpl(DataSource ds) {
        template = new NamedParameterJdbcTemplate(ds);
    }

    @Override
    public AResult<BlogPost> create(BlogPost blog) {
        Map<String, Object> params = new HashMap<>();
        params.put("blog_title", blog.getTitle());
        params.put("blog_summary", blog.getSummary());
        params.put("blog_author", blog.getAuthor().getId());
        params.put("blog_tags", blog.getTags().stream().collect(Collectors.joining(",")));

        String sql = "insert into tbl_blog_post (blog_title, blog_summary, blog_author, blog_tags, is_published, blog_created_ts) values (:blog_title, :blog_summary, :blog_author, :blog_tags, 0, datetime('now'))";

        KeyHolder holder = new GeneratedKeyHolder();
        int res = template.update(sql, new MapSqlParameterSource(params), holder);
        blog.setId(holder.getKey().longValue());

        return (res > 0) ? find(blog.getId()) : new AResult<>("failed to create new blog post", 400);
    }

    @Override
    public AResult<BlogPost> find(long id) {
        Map<String, Object> params = new HashMap<>();
        params.put("blog_id", id);

        String sql = "SELECT * FROM tbl_blog_post "
                + "inner join tbl_profile on profile_id=blog_author "
                + "left join tbl_blog_content on fk_blog_id=blog_id "
                + "WHERE blog_id=:blog_id "
                + "order by page_created_ts;";

        try {
            BlogPost blog = template.query(sql, params, blogPostExtractor());
            return blog != null ? new AResult<>(blog) : new AResult<>("could not find blog_post by id", 404);
        } catch (DataAccessException e) {
            return new AResult<>(e.getMessage(), 500);
        }
    }

    @Override
    public AResult<List<BlogPost>> findRecent(int start, int size) {
        Map<String, Object> params = new HashMap<>();
        params.put("offset", start);
        params.put("limit", size);

        String sql = "SELECT * FROM tbl_blog_post "
                + "inner join tbl_profile on profile_id=blog_author "
                + "left join tbl_blog_content on fk_blog_id=blog_id "
                + "order by page_created_ts, blog_created_ts desc "
                + "limit :limit offset :offset";

        try {
            List<BlogPost> blogs = template.query(sql, params, blogsListExtractor());
            return blogs != null ? new AResult<>(blogs) : new AResult<>("could not find blog posts", 404);
        } catch (DataAccessException e) {
            return new AResult<>(e.getMessage(), 500);
        }
    }

    @Override
    public AResult<List<BlogPost>> findByTitle(String title) {
        Map<String, Object> params = new HashMap<>();
        params.put("blog_title", "%" + title + "%");

        String sql = "SELECT * FROM tbl_blog_post "
                + "inner join tbl_profile on profile_id=blog_author "
                + "left join tbl_blog_content on fk_blog_id=blog_id "
                + "WHERE blog_title like :blog_title "
                + "order by page_created_ts;";

        try {
            List<BlogPost> blogs = template.query(sql, params, blogsListExtractor());
            return blogs != null ? new AResult<>(blogs) : new AResult<>("could not find blog post with matching title", 404);
        } catch (DataAccessException e) {
            return new AResult<>(e.getMessage(), 500);
        }
    }

    @Override
    public AResult<List<BlogPost>> findByAuthor(long authorId) {
        Map<String, Object> params = new HashMap<>();
        params.put("blog_author", authorId);

        String sql = "SELECT * FROM tbl_blog_post "
                + "inner join tbl_profile on profile_id=blog_author "
                + "left join tbl_blog_content on fk_blog_id=blog_id "
                + "WHERE blog_author = :blog_author "
                + "order by page_created_ts;";

        try {
            List<BlogPost> blogs = template.query(sql, params, blogsListExtractor());
            return blogs != null ? new AResult<>(blogs) : new AResult<>("could not find blog posts by author id " + authorId, 404);
        } catch (DataAccessException e) {
            return new AResult<>(e.getMessage(), 500);
        }
    }

    @Override
    public AResult<List<BlogPost>> findByTags(String[] tags) {
        Map<String, Object> params = new HashMap<>();
        StringBuilder where = new StringBuilder("WHERE ");
        for (int i = 0; i < tags.length; i++) {
            String tagIndex = "tag_" + i + "";
            params.put(tagIndex, "%" + tags[i] + "%");
            where.append("blog_tags like :").append(tagIndex).append(" ");
            if (i + 1 < tags.length) {
                where.append("or ");
            }
        }

        String sql = "SELECT distinct * FROM tbl_blog_post "
                + "inner join tbl_profile on profile_id=blog_author "
                + "left join tbl_blog_content on fk_blog_id=blog_id "
                + where.toString()
                + "order by blog_id, page_created_ts;";

        try {
            List<BlogPost> blogs = template.query(sql, params, blogsListExtractor());
            String tagsRegex = Arrays.stream(tags).map(e -> "(" + e + ")").collect(Collectors.joining("|"));
            return blogs != null ? new AResult<>(blogs) : new AResult<>("could not find blog posts by the tags '" + tagsRegex + "'", 404);
        } catch (DataAccessException e) {
            return new AResult<>(e.getMessage(), 500);
        }
    }

    @Override
    public AResult<Integer> updateTags(long blogId, String[] tags) {
        Map<String, Object> params = new HashMap<>();
        String newTags = Arrays.stream(tags).collect(Collectors.joining(","));
        params.put("blog_id", blogId);
        params.put("blog_tags", newTags);

        String sql = "update tbl_blog_post set blog_tags=:blog_tags where blog_id=:blog_id";

        int res = template.update(sql, params);
        return (res > 0) ? new AResult<>(res) : new AResult<>("failed to update blog post tags", 400);
    }

    @Override
    public AResult<Set<String>> tagsList() {
        String sql = "select distinct blog_tags from tbl_blog_post";

        List<String> blogs = template.query(sql, (rs, num) -> rs.getString("blog_tags"));
        //reduce(identity, accumulator, combiner)
        Set<String> tags = blogs.stream().filter(e -> e != null).map(e -> Arrays.asList(e.split(","))).reduce(new HashSet<>(), new BiFunction<Set<String>, List<String>, Set<String>>() {

            @Override
            public Set<String> apply(Set<String> t, List<String> u) {
                return Stream.concat(t.stream(), u.stream().filter(e -> e.trim().length() > 0))
                        .map(e -> e.trim().substring(0, 1).toUpperCase() + e.trim().substring(1)).collect(Collectors.toSet());
            }
        }, new BinaryOperator<Set<String>>() {

            @Override
            public Set<String> apply(Set<String> t, Set<String> u) {
                t.addAll(u);
                return t;
            }
        });
        return new AResult<>(tags);
    }

    @Override
    public AResult<Integer> publish(long blogId, boolean publish) {
        Map<String, Object> params = new HashMap<>();
        params.put("blog_id", blogId);
        params.put("is_published", publish);

        String sql = "update tbl_blog_post set is_published=:is_published where blog_id=:blog_id";

        int res = template.update(sql, params);
        return (res > 0) ? new AResult<>(res) : new AResult<>("failed to publish blog post", 400);
    }

    @Override
    public AResult<Integer> update(BlogPost blog) {
        Map<String, Object> params = new HashMap<>();
        params.put("blog_id", blog.getId());
        params.put("blog_title", blog.getTitle());
        params.put("blog_summary", blog.getSummary());

        String sql = "update tbl_blog_post set blog_title=:blog_title, blog_summary=:blog_summary where blog_id=:blog_id";

        int res = template.update(sql, params);
        return (res > 0) ? new AResult<>(res) : new AResult<>("failed to update blog post", 400);
    }

    @Override
    public AResult<Integer> update(long blogId, int page, String content) {
        Map<String, Object> params = new HashMap<>();
        params.put("fk_blog_id", blogId);
        params.put("page_created_ts", page);
        params.put("blog_text", content);

        String sql = "insert or replace into tbl_blog_content (fk_blog_id, page_created_ts, blog_text) values (:fk_blog_id, :page_created_ts, :blog_text) ";

        int res = template.update(sql, params);
        return (res > 0) ? new AResult<>(res) : new AResult<>("failed to update/insert blog content", 400);
    }

    @Override
    public AResult<Integer> delete(long blogId) {
        Map<String, Object> params = new HashMap<>();
        params.put("fk_blog_id", blogId);

        String sql = "delete from tbl_blog_content where fk_blog_id=:fk_blog_id";

        int res = template.update(sql, params);
        return (res > 0) ? new AResult<>(res) : new AResult<>("failed to delete blog post", 400);
    }

    @Override
    public AResult<Comment> comment(long blogId, Comment comment) {
        Map<String, Object> params = new HashMap<>();
        params.put("parent_blog", comment.getParentBlog());
        params.put("parent_comment", comment.getParentComment() > 0 ? comment.getParentComment() : null);
        params.put("comment_author", comment.getAuthor().getId());
        params.put("comment_text", comment.getContent());

        String sql = "INSERT into tbl_blog_comment (parent_blog, parent_comment, comment_author, comment_text,  is_published, comment_created_ts) values (:parent_blog, :parent_comment, :comment_author, :comment_text, 0, datetime('now'))";

        KeyHolder holder = new GeneratedKeyHolder();
        int res = template.update(sql, new MapSqlParameterSource(params), holder);
        comment.setId(holder.getKey().longValue());

        return (res > 0) ? new AResult<>(comment) : new AResult<>("failed to create new blog comment", 400);
    }

    @Override
    public AResult<List<Comment>> comments(long blogId) {
        Map<String, Object> params = new HashMap<>();
        params.put("parent_blog", blogId);

        String sql = "SELECT * from tbl_blog_comment inner join tbl_profile on profile_id=comment_author where parent_blog=:parent_blog order by parent_comment, comment_created_ts ";

        try {
            List<Comment> comments = template.query(sql, params, commentsExtractor());
            return comments != null ? new AResult<>(comments) : new AResult<>("could not find any comments for this blog", 404);
        } catch (DataAccessException e) {
            return new AResult<>(e.getMessage(), 500);
        }
    }

    @Override
    public AResult<Integer> publishComment(long commenId, boolean publish) {
        Map<String, Object> params = new HashMap<>();
        params.put("comment_id", commenId);
        params.put("is_published", publish);

        String sql = "update tbl_blog_comment set is_published=:is_published where comment_id=:comment_id";

        int res = template.update(sql, params);
        return (res > 0) ? new AResult<>(res) : new AResult<>("failed to publish blog comment", 400);
    }

    @Override
    public AResult<Integer> updateComment(long commentId, String content) {
        Map<String, Object> params = new HashMap<>();
        params.put("comment_id", commentId);
        params.put("comment_text", content);

        String sql = "update tbl_blog_comment set comment_text=:comment_text where comment_id=:comment_id";

        int res = template.update(sql, params);
        return (res > 0) ? new AResult<>(res) : new AResult<>("failed to update blog comment", 400);
    }

    @Override
    public AResult<Integer> deleteComment(long blogId, long commentId) {
        Map<String, Object> params = new HashMap<>();
        params.put("comment_id", commentId);
        params.put("parent_blog", blogId);

        String sql = "delete from tbl_blog_comment where comment_id=:comment_id and parent_blog=:parent_blog";

        int res = template.update(sql, params);
        return (res > 0) ? new AResult<>(res) : new AResult<>("failed to update blog comment", 400);
    }

    private RowMapper<BlogPost> blogPostMapper() {
        return (rs, rowNum) -> {
            BlogPost blog = new BlogPost();
            blog.setId(rs.getLong("blog_id"));
            blog.setTitle(rs.getString("blog_title"));
            blog.setSummary(rs.getString("blog_summary"));
            blog.setPublished(rs.getBoolean("is_published"));
            if (rs.getString("blog_tags") != null) {
                List<String> tags = Arrays.stream(rs.getString("blog_tags").split(",")).collect(Collectors.toList());
                blog.getTags().addAll(tags);
            }
            blog.setCreatedTs(SqliteDate.fromString(rs.getString("blog_created_ts")));
            //map author
            Profile author = new Profile();
            author.setId(rs.getLong("blog_author"));
            author.setFirstName(rs.getString("first_name"));
            author.setLastName(rs.getString("last_name"));
            author.setEmailAddress(rs.getString("email_addr"));
            blog.setAuthor(author);
            return blog;
        };
    }

    private ResultSetExtractor<BlogPost> blogPostExtractor() {
        return (rs) -> {
            int row = 0;
            BlogPost post = blogPostMapper().mapRow(rs, row);
            //map content
            int page;
            do {
                page = rs.getInt("blog_page");
                String content = rs.getString("blog_text");
                post.setPageNum(page);
                post.setContent(content);
            } while (rs.next() && rs.getInt("blog_page") != page);
            return post;
        };
    }

    private ResultSetExtractor<List<BlogPost>> blogsListExtractor() {
        return (rs) -> {
            List<BlogPost> list = new ArrayList<>();
            long blogId;
            do {
                blogId = rs.getLong("blog_id");
                BlogPost post = blogPostExtractor().extractData(rs);
                list.add(post);
            } while (rs.next() && rs.getLong("blog_id") != blogId);
            return list;
        };
    }

    private RowMapper<Comment> commentMapper() {
        return (rs, rowNum) -> {
            Comment comment = new Comment();
            comment.setId(rs.getLong("comment_id"));
            comment.setContent(rs.getString("comment_text"));
            comment.setParentBlog(rs.getLong("parent_blog"));
            comment.setParentComment(rs.getLong("parent_comment"));
            comment.setPublished(rs.getBoolean("is_published"));
            Profile author = new Profile();
            author.setId(rs.getLong("comment_author"));
            author.setFirstName(rs.getString("first_name"));
            author.setLastName(rs.getString("last_name"));
            author.setEmailAddress(rs.getString("email_addr"));
            comment.setAuthor(author);
            comment.setCreatedTs(SqliteDate.fromString(rs.getString("comment_created_ts")));
            return comment;
        };
    }

    private ResultSetExtractor<List<Comment>> commentsExtractor() {
        return (rs) -> {
            List<Comment> list = new ArrayList<>();
            int rowNum = 0;
            while (rs.next()) {
                Comment entry = commentMapper().mapRow(rs, ++rowNum);
                list.add(entry);
            }
            return list;
        };
    }
}
