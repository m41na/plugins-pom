package com.practicaldime.plugins.blogs.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.transaction.annotation.Transactional;

import com.practicaldime.common.util.AResult;
import com.practicaldime.common.entity.blogs.BlogPost;
import com.practicaldime.common.entity.blogs.Comment;
import com.practicaldime.common.entity.users.Profile;
import com.practicaldime.plugins.blogs.service.BlogsService;

import com.practicaldime.plugins.blogs.config.BlogsServiceTestConfig;

@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = BlogsServiceTestConfig.class, loader = AnnotationConfigContextLoader.class)
@Sql({"/sql/create-tables.sql"})
@Sql(scripts = "/sql/insert-data.sql", config = @SqlConfig(commentPrefix = "--"))
public class BlogsServiceImplTest {

    @Autowired
    private BlogsService blogsService;

    @Test
    public void testCreate() {
        BlogPost blog = new BlogPost();
        Profile author = new Profile();
        author.setId(1l);
        blog.setAuthor(author);
        blog.setSummary("summary from 'BlogsServiceImplTest::testCreate'");
        blog.setTitle("BlogsServiceImplTest testing");
        AResult<BlogPost> created = blogsService.create(blog);
        assertTrue("Expected no error", created.errors.isEmpty());
    }

    @Test
    public void testFind() {
        long blogId = 2;
        AResult<BlogPost> blog = blogsService.find(blogId);
        assertTrue("Expected no error", blog.errors.isEmpty());
        assertEquals("Expected " + blogId, blogId, blog.data.getId().longValue());
    }

    @Test
    public void testFindByTitle() {
        String title = "Math";
        AResult<List<BlogPost>> blog = blogsService.findByTitle(title);
        assertTrue("Expected no error", blog.errors.isEmpty());
        assertEquals("Expected 3", 3, blog.data.size());
    }

    @Test
    public void testFindByAuthor() {
        long author = 1;
        AResult<List<BlogPost>> blog = blogsService.findByAuthor(author);
        assertTrue("Expected no error", blog.errors.isEmpty());
        assertEquals("Expected 2", 2, blog.data.size());
    }

    @Test
    public void testPublish() {
        long blogId = 2l;
        boolean publish = true;
        AResult<Integer> published = blogsService.publish(blogId, publish);
        assertTrue("Expected no error", published.errors.isEmpty());
        assertEquals("Expected 1", 1, published.data.intValue());
    }

    @Test
    public void testUpdateAddContent() {
        long blogId = 2l;
        int page = 1;
        String content = "interesting content from testUpdateAddContent";
        AResult<Integer> updated = blogsService.update(blogId, page, content);
        assertTrue("Expected no error", updated.errors.isEmpty());
        assertEquals("Expected 1", 1, updated.data.intValue());
    }

    @Test
    public void testUpdateBlogContent() {
        long blogId = 2l;
        AResult<BlogPost> existing = blogsService.find(blogId);
        assertTrue("Expected no error", existing.errors.isEmpty());
        BlogPost blog = existing.data;
        String title = "test-title-2";
        String summary = "interesting content from testUpdateBlogContent";
        //set new values
        blog.setTitle(title);
        blog.setSummary(summary);
        AResult<Integer> updated = blogsService.update(blog);
        assertTrue("Expected no error", updated.errors.isEmpty());
        assertEquals("Expected 1", 1, updated.data.intValue());
        //retrieve updated
        AResult<BlogPost> result = blogsService.find(blogId);
        assertTrue("Expected no error", result.errors.isEmpty());
        BlogPost post = result.data;
        assertEquals(title, post.getTitle());
        assertEquals(summary, post.getSummary());
    }

    @Test
    public void testUpdateBlogTags() {
        long blogId = 1l;
        AResult<BlogPost> find = blogsService.find(blogId);
        assertTrue("Expected no error", find.errors.isEmpty());
        BlogPost post = find.data;
        List<String> tags = post.getTags();
        String tagVal = tags.stream().collect(Collectors.joining(","));
        System.out.printf("tags found %s%n", tagVal);
        tags.add("testing");
        AResult<Integer> updated = blogsService.update(post.getId(), tags.toArray(new String[tags.size()]));
        assertEquals("Expected 1", 1, updated.data.intValue());
        List<String> newList = blogsService.find(blogId).data.getTags();
        assertEquals("Expecting same size", newList.size() - tags.size(), 0);
    }

    @Test
    public void testFetchAvailableags() {
        AResult<Set<String>> fetch = blogsService.fetchTags();
        assertTrue("Expected no error", fetch.errors.isEmpty());
        Set<String> tags = fetch.data;
        assertEquals("Expecting 8", 8, tags.size());
    }

    @Test
    public void testDelete() {
        long blogId = 1l;
        AResult<Integer> deleted = blogsService.delete(blogId);
        assertTrue("Expected no error", deleted.errors.isEmpty());
        assertEquals("Expected 1", 1, deleted.data.intValue());
    }

    @Test
    public void testCreateComment() {
        long blogId = 1l;
        Comment comment = new Comment();
        Profile author = new Profile();
        author.setId(2l);
        comment.setAuthor(author);
        String content = "BlogsServiceImplTest::testCreateComment";
        comment.setContent(content);
        comment.setParentBlog(blogId);
        AResult<Comment> created = blogsService.comment(blogId, comment);
        assertTrue("Expected no error", created.errors.isEmpty());
        assertEquals("Expected " + content, content, created.data.getContent());
    }

    @Test
    public void testComments() {
        long blogId = 1l;
        AResult<List<Comment>> comments = blogsService.comments(blogId);
        assertEquals("Execting 2", 2, comments.data.size());
    }

    @Test
    public void testPublishComment() {
        AResult<Integer> published = blogsService.publishComment(1l, 1l, true);
        assertEquals("Execting 1", 1, published.data.intValue());
    }

    @Test
    public void testUpdateComment() {
        long blogId = 1l;
        long commentId = 2l;
        String content = "updated with 'testUpdateComment'";
        AResult<Integer> published = blogsService.updateComment(blogId, commentId, content);
        assertEquals("Execting 1", 1, published.data.intValue());
    }

    @Test
    public void testDeleteComment() {
        long blogId = 1;
        long commentId = 1;
        AResult<Integer> published = blogsService.deleteComment(blogId, commentId);
        assertEquals("Execting 1", 1, published.data.intValue());
    }

}
