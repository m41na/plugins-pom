package com.practicaldime.plugins.blogs.service;

import java.util.List;
import java.util.Set;

import com.practicaldime.common.util.AppResult;
import com.practicaldime.domain.blogs.BlogPost;
import com.practicaldime.domain.blogs.Comment;

public interface BlogsService {

    AppResult<BlogPost> create(BlogPost blog);

    AppResult<BlogPost> find(long blogId);
    
    AppResult<List<BlogPost>> findRecent(int start, int size);

    AppResult<List<BlogPost>> findByTitle(String title);

    AppResult<List<BlogPost>> findByTags(String[] tags);

    AppResult<List<BlogPost>> findByAuthor(long authorId);
    
    AppResult<Integer> publish( long blogId, boolean publish);

    AppResult<Integer> update(BlogPost blog);

    AppResult<Integer> update(long blogId, int page, String content);
    
    AppResult<Integer> update(long blogId, String[] tags);
    
    AppResult<Set<String>> fetchTags();

    AppResult<Integer> delete(long blogId);

    AppResult<Comment> comment(long blogId, Comment comment);

    AppResult<List<Comment>> comments(long blogId);
    
    AppResult<Integer> publishComment( long blogId, long commenId, boolean publish);
    
    AppResult<Integer> updateComment(long blogId, long commentId, String content);
    
    AppResult<Integer> deleteComment(long blogId, long commentId);
}
