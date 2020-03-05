package com.practicaldime.plugins.blogs.service;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.practicaldime.common.util.AResult;
import com.practicaldime.common.util.CatchExceptions;
import com.practicaldime.common.util.Validatable;
import com.practicaldime.common.entity.blogs.BlogPost;
import com.practicaldime.common.entity.blogs.Comment;
import com.practicaldime.plugins.blogs.dao.BlogsDao;

@Service
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
@Validatable
@CatchExceptions
public class BlogsServiceImpl implements BlogsService {

    @Autowired
    private BlogsDao blogsDao;

    public BlogsDao getBlogsDao() {
        return blogsDao;
    }

    public void setBlogsDao(BlogsDao blogsDao) {
        this.blogsDao = blogsDao;
    }

    @Override
    public AResult<BlogPost> create(BlogPost blog) {
        return blogsDao.create(blog);
    }

    @Override
    public AResult<BlogPost> find(long blogId) {
        return blogsDao.find(blogId);
    }

    @Override
	public AResult<List<BlogPost>> findRecent(int start, int size) {
    	return blogsDao.findRecent(start, size);
	}

	@Override
    public AResult<List<BlogPost>> findByTitle(String title) {
        return blogsDao.findByTitle(title);
    }

    @Override
    public AResult<List<BlogPost>> findByTags(String[] tags) {
        return blogsDao.findByTags(tags);
    }

    @Override
    public AResult<List<BlogPost>> findByAuthor(long authorId) {
        return blogsDao.findByAuthor(authorId);
    }

    @Override
    public AResult<Integer> publish(long blogId, boolean publish) {
        return blogsDao.publish(blogId, publish);
    }

    @Override
    public AResult<Integer> update(BlogPost blog) {
        return blogsDao.update(blog);
    }

    @Override
    public AResult<Integer> update(long blogId, int page, String content) {
        return blogsDao.update(blogId, page, content);
    }
    
    @Override
    public AResult<Integer> update(long blogId, String[] tags) {
        return blogsDao.updateTags(blogId, tags);
    }

    @Override
	public AResult<Set<String>> fetchTags() {
		return blogsDao.tagsList();
	}

	@Override
    public AResult<Integer> delete(long blogId) {
        return blogsDao.delete(blogId);
    }

    @Override
    public AResult<Comment> comment(long blogId, Comment comment) {
        return blogsDao.comment(blogId, comment);
    }

    @Override
    public AResult<List<Comment>> comments(long blogId) {
        return blogsDao.comments(blogId);
    }

    @Override
    public AResult<Integer> publishComment(long blogId, long commenId, boolean publish) {
        return blogsDao.publishComment(commenId, publish);
    }

    @Override
    public AResult<Integer> updateComment(long blogId, long commentId, String content) {
        return blogsDao.updateComment(commentId, content);
    }

    @Override
    public AResult<Integer> deleteComment(long blogId, long commentId) {
        return blogsDao.deleteComment(blogId, commentId);
    }
}
