package works.hop.plugins.blogs.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import com.jarredweb.common.util.AppResult;
import com.jarredweb.domain.blogs.BlogPost;
import com.jarredweb.domain.blogs.Comment;
import com.jarredweb.domain.users.Profile;
import com.jarredweb.plugins.blogs.dao.BlogsDao;

import works.hop.plugins.blogs.config.BlogsDaoTestConfig;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = BlogsDaoTestConfig.class, loader = AnnotationConfigContextLoader.class)
@Sql({"/sql/create-tables.sql"})
@Sql(scripts = "/sql/insert-data.sql", config = @SqlConfig(commentPrefix = "--"))
public class BlogsDaoImplTest {

    private static final Logger LOG = LoggerFactory.getLogger(BlogsDaoImplTest.class);

    @Autowired
    private BlogsDao blogsDao;

    @Test
    public void testFind() {
        AppResult<BlogPost> list = blogsDao.find(1l);
        assertEquals("Expecting 1", 1, list.getEntity().getId());
        assertEquals("Expecting 1", 1, list.getEntity().getAuthor().getId());
    }

    @Test
    public void testCreate() {
        BlogPost blog = new BlogPost();
        Profile author = new Profile();
        author.setId(2);
        blog.setAuthor(author);
        blog.setTitle("sharks");
        blog.setSummary("testing 'testCreate()'");
        BlogPost created = blogsDao.create(blog).getEntity();
        LOG.info("newly created rest list id is {}", created.getId());
        assertEquals("Expecting 'sharks'", "sharks", created.getTitle());
        assertEquals("Expecting 'Guest'", "Guest", created.getAuthor().getFirstName());
    }
    
    @Test
    public void testFindRecent() {
        AppResult<List<BlogPost>> search = blogsDao.findRecent(1, 2);
        assertTrue("Expected more than one", search.getEntity().size() > 1);
    }

    @Test
    public void testFindByTitle() {
        String title = "Math";
        AppResult<List<BlogPost>> search = blogsDao.findByTitle(title);
        assertTrue("Expected more than one", search.getEntity().size() > 1);
    }

    @Test
    public void testFndByAuthor() {
        long authorId = 2l;
        AppResult<List<BlogPost>> search = blogsDao.findByAuthor(authorId);
        assertTrue("Expected more than one", search.getEntity().size() > 1);
    }
    
    @Test
    public void testFindByTags() {
        AppResult<List<BlogPost>> search = blogsDao.findByTags(new String[] {"math","physics"});
        assertEquals("Expected 2 records", 2, search.getEntity().size());
    }
    
    @Test
    public void testUpdateTags() {
    	long blogId = 1l;
    	BlogPost blog = blogsDao.find(blogId).getEntity();
    	List<String> tags = blog.getTags();
    	tags.add("brown");
    	AppResult<Integer> doUpdate = blogsDao.updateTags(blogId, tags.toArray(new String[tags.size()]));
    	assertEquals("Expecting 1 row updated", 1, doUpdate.getEntity().intValue());
    	BlogPost updated = blogsDao.find(blogId).getEntity();
    	assertTrue("Expecting list contains new tag", updated.getTags().contains("brown"));
    }
    
    @Test
    public void testGetTagsList() {
    	Set<String> tags = blogsDao.tagsList().getEntity();
    	assertEquals("Expecting 8 different tags", 8, tags.size());
    }

    @Test
    public void testPublish() {
        long blogId = 2l;
        boolean publish = true;
        AppResult<Integer> result = blogsDao.publish(blogId, publish);
        assertEquals("Expecting 1", 1, result.getEntity().intValue());
        //fetch published blog
        AppResult<BlogPost> published = blogsDao.find(blogId);
        assertEquals("Expecting 'true'", true, published.getEntity().isPublished());
    }

    @Test
    public void testUpdate() {
        long blogId = 3l;
        BlogPost blog = blogsDao.find(blogId).getEntity();
        String newTitle = "what in the world";
        blog.setTitle(newTitle);
        AppResult<Integer> updated = blogsDao.update(blog);
        assertEquals("Expecting 1'", 1, updated.getEntity().intValue());
    }

    @Test
    public void testUpdateAddContent() {
        long blogId = 3l;
        String content = "this is content from 'testUpdateAddContent'";
        AppResult<Integer> result = blogsDao.update(blogId, 1, content);
        assertEquals("Expecting 1", 1, result.getEntity().intValue());
        AppResult<BlogPost> updated = blogsDao.find(blogId);
        assertEquals("Expecting similar content", content, updated.getEntity().getContent());
    }

    @Test
    public void testDelete() {
        AppResult<Integer> result = blogsDao.delete(2l);
        assertEquals("Expecting 1", 1, result.getEntity().intValue());
    }

    @Test
    public void testAddComment() {
        long blogId = 3l;
        Comment comment = new Comment();
        Profile author = new Profile();
        author.setId(2l);
        comment.setAuthor(author);
        comment.setContent("test comment in 'testAddComment'");
        comment.setParentBlog(1l);
        Comment created = blogsDao.comment(blogId, comment).getEntity();
        assertEquals("Expecting 3", 3, created.getId());
    }

    @Test
    public void testRetrieveComments() {
        long blogId = 1l;
        List<Comment> comments = blogsDao.comments(blogId).getEntity();
        assertEquals("Expecting 2", 2, comments.size());
        assertEquals("Expecting 'Guest'", "Guest", comments.get(0).getAuthor().getFirstName());
    }

    @Test
    public void testPublishComment() {
        long commenId = 2l;
        boolean publish = true;
        AppResult<Integer> result = blogsDao.publishComment(commenId, publish);
        assertEquals("Expecting 1", 1, result.getEntity().intValue());
    }

    @Test
    public void testUpdateComment() {
        long commentId = 1l;
        String content = "comment updated from 'testUpdateComment'";
        AppResult<Integer> updated = blogsDao.updateComment(commentId, content);
        assertEquals("Expecting 1", 1, updated.getEntity().intValue());
    }

    @Test
    public void testDeleteComment() {
        long blogId = 1l;
        long commentId = 1l;
        AppResult<Integer> deleted = blogsDao.deleteComment(blogId, commentId);
        assertEquals("Expecting 1", 1, deleted.getEntity().intValue());
    }
}
