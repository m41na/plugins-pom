package com.practicaldime.plugins.blogs.dao;

import com.practicaldime.common.entity.blogs.BlogPost;
import com.practicaldime.common.entity.blogs.Comment;
import com.practicaldime.common.util.AResult;
import com.practicaldime.common.util.DaoStatus;

import java.util.List;
import java.util.Set;

public interface BlogsDao extends DaoStatus {

    AResult<BlogPost> create(BlogPost blog);

    AResult<BlogPost> find(long blogId);

    AResult<List<BlogPost>> findRecent(int start, int size);

    AResult<List<BlogPost>> findByTitle(String title);

    AResult<List<BlogPost>> findByAuthor(long authorId);

    AResult<List<BlogPost>> findByTags(String[] tags);

    AResult<Integer> updateTags(long blogId, String[] tag);

    AResult<Set<String>> tagsList();

    AResult<Integer> publish(long blogId, boolean publish);

    AResult<Integer> update(BlogPost blog);

    AResult<Integer> update(long blogId, int page, String content);

    AResult<Integer> delete(long blogId);

    AResult<Comment> comment(long blogId, Comment comment);

    AResult<List<Comment>> comments(long blogId);

    AResult<Integer> publishComment(long commenId, boolean publish);

    AResult<Integer> updateComment(long commentId, String content);

    AResult<Integer> deleteComment(long blogId, long commentId);
}
