package com.practicaldime.plugins.blogs.service;

import com.practicaldime.common.entity.blogs.BlogPost;
import com.practicaldime.common.entity.blogs.Comment;
import com.practicaldime.common.util.AResult;

import java.util.List;
import java.util.Set;

public interface BlogsService {

    AResult<BlogPost> create(BlogPost blog);

    AResult<BlogPost> find(long blogId);

    AResult<List<BlogPost>> findRecent(int start, int size);

    AResult<List<BlogPost>> findByTitle(String title);

    AResult<List<BlogPost>> findByTags(String[] tags);

    AResult<List<BlogPost>> findByAuthor(long authorId);

    AResult<Integer> publish(long blogId, boolean publish);

    AResult<Integer> update(BlogPost blog);

    AResult<Integer> update(long blogId, int page, String content);

    AResult<Integer> update(long blogId, String[] tags);

    AResult<Set<String>> fetchTags();

    AResult<Integer> delete(long blogId);

    AResult<Comment> comment(long blogId, Comment comment);

    AResult<List<Comment>> comments(long blogId);

    AResult<Integer> publishComment(long blogId, long commenId, boolean publish);

    AResult<Integer> updateComment(long blogId, long commentId, String content);

    AResult<Integer> deleteComment(long blogId, long commentId);
}
