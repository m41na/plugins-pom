package com.practicaldime.graphql.dao;

import com.practicaldime.graphql.app.Result;
import com.practicaldime.graphql.entity.Blog;
import com.practicaldime.graphql.entity.Ebook;

import java.util.List;

public interface PublicationRepo {

    Result<Blog> findBlogById(Long id);

    Result<Ebook> findEbookById(Long id);

    Result<Blog> findBlogByTitle(String title);

    Result<Ebook> findEbookByTitle(String title);

    Result<List<Blog>> fetchBlogs(int start, int size);

    Result<List<Ebook>> fetchEbooks(int start, int size);

    Result<Integer> createBlog(Blog blog);

    Result<Integer> createEbook(Ebook ebook);
}
