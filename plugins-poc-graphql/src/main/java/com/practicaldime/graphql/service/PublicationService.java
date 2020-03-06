package com.practicaldime.graphql.service;

import com.practicaldime.graphql.entity.Blog;
import com.practicaldime.graphql.entity.Ebook;

import java.util.List;

public interface PublicationService {

    Blog findBlogById(Long id);

    Ebook findEbookById(Long id);

    Blog findBlogByTitle(String title);

    Ebook findEbookByTitle(String title);

    List<Blog> fetchBlogs(int start, int size);

    List<Ebook> fetchEbooks(int start, int size);

    Integer createBlog(Blog blog);

    Integer createEbook(Ebook ebook);
}
