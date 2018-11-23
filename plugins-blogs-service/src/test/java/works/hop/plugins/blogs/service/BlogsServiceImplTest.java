package works.hop.plugins.blogs.service;

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

import com.jarredweb.common.util.AppResult;
import com.jarredweb.domain.blogs.BlogPost;
import com.jarredweb.domain.blogs.Comment;
import com.jarredweb.domain.users.Profile;
import com.jarredweb.plugins.blogs.service.BlogsService;

import works.hop.plugins.blogs.config.BlogsServiceTestConfig;

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
        AppResult<BlogPost> created = blogsService.create(blog);
        assertTrue("Expected no error", created.getError() == null);
    }

    @Test
    public void testFind() {
        long blogId = 2;
        AppResult<BlogPost> blog = blogsService.find(blogId);
        assertTrue("Expected no error", blog.getError() == null);
        assertEquals("Expected " + blogId, blogId, blog.getEntity().getId());
    }

    @Test
    public void testFindByTitle() {
        String title = "Math";
        AppResult<List<BlogPost>> blog = blogsService.findByTitle(title);
        assertTrue("Expected no error", blog.getError() == null);
        assertEquals("Expected 3", 3, blog.getEntity().size());
    }

    @Test
    public void testFindByAuthor() {
        long author = 1;
        AppResult<List<BlogPost>> blog = blogsService.findByAuthor(author);
        assertTrue("Expected no error", blog.getError() == null);
        assertEquals("Expected 2", 2, blog.getEntity().size());
    }

    @Test
    public void testPublish() {
        long blogId = 2l;
        boolean publish  =true;
        AppResult<Integer> published = blogsService.publish(blogId, publish);
        assertTrue("Expected no error", published.getError() == null);
        assertEquals("Expected 1", 1, published.getEntity().intValue());
    }

    @Test
    public void testUpdateAddContent() {
        long blogId = 2l;
        int page = 1;
        String content = "interesting content from testUpdateAddContent";
        AppResult<Integer> updated = blogsService.update(blogId, page, content);
        assertTrue("Expected no error", updated.getError() == null);
        assertEquals("Expected 1", 1, updated.getEntity().intValue());
    }

    @Test
    public void testUpdateBlogContent() {
        long blogId = 2l;
        AppResult<BlogPost> existing = blogsService.find(blogId);
        assertTrue("Expected no error", existing.getError() == null);
        BlogPost blog = existing.getEntity();
        String title = "test-title-2";
        String summary = "interesting content from testUpdateBlogContent";
        //set new values
        blog.setTitle(title);
        blog.setSummary(summary);
        AppResult<Integer> updated = blogsService.update(blog);
        assertTrue("Expected no error", updated.getError() == null);
        assertEquals("Expected 1", 1, updated.getEntity().intValue());
        //retrieve updated
        AppResult<BlogPost> result = blogsService.find(blogId);
        assertTrue("Expected no error", result.getError() == null);
        BlogPost post = result.getEntity();
        assertEquals(title, post.getTitle());
        assertEquals(summary, post.getSummary());
    }
    
    @Test
    public void testUpdateBlogTags() {
        long blogId = 1l;
        AppResult<BlogPost> find = blogsService.find(blogId);
        assertTrue("Expected no error", find.getError() == null);
        BlogPost post = find.getEntity();
        List<String> tags = post.getTags();
        String tagVal = tags.stream().collect(Collectors.joining(","));
        System.out.printf("tags found %s%n", tagVal);
        tags.add("testing");
        AppResult<Integer> updated = blogsService.update(post.getId(), tags.toArray(new String[tags.size()]));
        assertEquals("Expected 1", 1, updated.getEntity().intValue());
        List<String> newList = blogsService.find(blogId).getEntity().getTags();
        assertEquals("Expecting same size", newList.size() - tags.size(), 0);
    }
    
    @Test
    public void testFetchAvailableags() {
    	AppResult<Set<String>> fetch = blogsService.fetchTags();
    	assertTrue("Expected no error", fetch.getError() == null);
    	Set<String> tags = fetch.getEntity();
    	assertEquals("Expecting 8", 8, tags.size());
    }

    @Test
    public void testDelete() {
        long blogId = 1l;
        AppResult<Integer> deleted = blogsService.delete(blogId);
        assertTrue("Expected no error", deleted.getError() == null);
        assertEquals("Expected 1", 1, deleted.getEntity().intValue());
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
        AppResult<Comment> created = blogsService.comment(blogId, comment);
        assertTrue("Expected no error", created.getError() == null);
        assertEquals("Expected " + content, content, created.getEntity().getContent());        
    }

    @Test
    public void testComments() {
        long blogId = 1l;
        AppResult<List<Comment>> comments = blogsService.comments(blogId);
        assertEquals("Execting 2", 2, comments.getEntity().size());
    }

    @Test
    public void testPublishComment() {
        AppResult<Integer> published = blogsService.publishComment(1l, 1l, true);
         assertEquals("Execting 1", 1, published.getEntity().intValue());
    }

    @Test
    public void testUpdateComment() {
        long blogId = 1l;
        long commentId = 2l;
        String content = "updated with 'testUpdateComment'";
        AppResult<Integer> published = blogsService.updateComment(blogId, commentId, content);
         assertEquals("Execting 1", 1, published.getEntity().intValue());
    }

    @Test
    public void testDeleteComment() {
        long blogId = 1;
        long commentId = 1;
        AppResult<Integer> published = blogsService.deleteComment(blogId, commentId);
         assertEquals("Execting 1", 1, published.getEntity().intValue());
    }
    
}
