package com.practicaldime.plugins.blogs.service;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.practicaldime.common.util.AppResult;
import com.practicaldime.common.util.CatchExceptions;
import com.practicaldime.common.util.Validatable;
import com.practicaldime.domain.blogs.BlogPost;
import com.practicaldime.domain.blogs.Comment;
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
    public AppResult<BlogPost> create(BlogPost blog) {
        return blogsDao.create(blog);
    }

    @Override
    public AppResult<BlogPost> find(long blogId) {
        return blogsDao.find(blogId);
    }

    @Override
	public AppResult<List<BlogPost>> findRecent(int start, int size) {
    	return blogsDao.findRecent(start, size);
	}

	@Override
    public AppResult<List<BlogPost>> findByTitle(String title) {
        return blogsDao.findByTitle(title);
    }

    @Override
    public AppResult<List<BlogPost>> findByTags(String[] tags) {
        return blogsDao.findByTags(tags);
    }

    @Override
    public AppResult<List<BlogPost>> findByAuthor(long authorId) {
        return blogsDao.findByAuthor(authorId);
    }

    @Override
    public AppResult<Integer> publish(long blogId, boolean publish) {
        return blogsDao.publish(blogId, publish);
    }

    @Override
    public AppResult<Integer> update(BlogPost blog) {
        return blogsDao.update(blog);
    }

    @Override
    public AppResult<Integer> update(long blogId, int page, String content) {
        return blogsDao.update(blogId, page, content);
    }
    
    @Override
    public AppResult<Integer> update(long blogId, String[] tags) {
        return blogsDao.updateTags(blogId, tags);
    }

    @Override
	public AppResult<Set<String>> fetchTags() {
		return blogsDao.tagsList();
	}

	@Override
    public AppResult<Integer> delete(long blogId) {
        return blogsDao.delete(blogId);
    }

    @Override
    public AppResult<Comment> comment(long blogId, Comment comment) {
        return blogsDao.comment(blogId, comment);
    }

    @Override
    public AppResult<List<Comment>> comments(long blogId) {
        return blogsDao.comments(blogId);
    }

    @Override
    public AppResult<Integer> publishComment(long blogId, long commenId, boolean publish) {
        return blogsDao.publishComment(commenId, publish);
    }

    @Override
    public AppResult<Integer> updateComment(long blogId, long commentId, String content) {
        return blogsDao.updateComment(commentId, content);
    }

    @Override
    public AppResult<Integer> deleteComment(long blogId, long commentId) {
        return blogsDao.deleteComment(blogId, commentId);
    }
}
