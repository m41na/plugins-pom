package com.jarredweb.plugins.blogs.dao;

import java.util.List;
import java.util.Set;

import com.jarredweb.common.util.AppResult;
import com.jarredweb.common.util.DaoStatus;
import com.jarredweb.domain.blogs.BlogPost;
import com.jarredweb.domain.blogs.Comment;

public interface BlogsDao extends DaoStatus{

    AppResult<BlogPost> create(BlogPost blog);

    AppResult<BlogPost> find(long blogId);

	AppResult<List<BlogPost>> findRecent(int start, int size);

    AppResult<List<BlogPost>> findByTitle(String title);

    AppResult<List<BlogPost>> findByAuthor(long authorId);
    
    AppResult<List<BlogPost>> findByTags(String[] tags);
    
    AppResult<Integer> updateTags(long blogId, String[] tag);
    
    AppResult<Set<String>> tagsList();

    AppResult<Integer> publish(long blogId, boolean publish);

    AppResult<Integer> update(BlogPost blog);

    AppResult<Integer> update(long blogId, int page, String content);

    AppResult<Integer> delete(long blogId);

    AppResult<Comment> comment(long blogId, Comment comment);

    AppResult<List<Comment>> comments(long blogId);

    AppResult<Integer> publishComment(long commenId, boolean publish);

    AppResult<Integer> updateComment(long commentId, String content);

    AppResult<Integer> deleteComment(long blogId, long commentId);
}
