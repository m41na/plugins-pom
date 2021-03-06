package com.practicaldime.graphql.service;

import com.practicaldime.graphql.dao.PublicationRepo;
import com.practicaldime.graphql.entity.Blog;
import com.practicaldime.graphql.entity.Ebook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class PublicationServiceImpl implements PublicationService {

    @Autowired
    private PublicationRepo dao;

    @Override
    public Blog findBlogById(Long id) {
        return dao.findBlogById(id).data;
    }

    @Override
    public Ebook findEbookById(Long id) {
        return dao.findEbookById(id).data;
    }

    @Override
    public Blog findBlogByTitle(String title) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Ebook findEbookByTitle(String title) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Blog> fetchBlogs(int start, int size) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Ebook> fetchEbooks(int start, int size) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Integer createBlog(Blog blog) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Integer createEbook(Ebook ebook) {
        // TODO Auto-generated method stub
        return null;
    }
}
