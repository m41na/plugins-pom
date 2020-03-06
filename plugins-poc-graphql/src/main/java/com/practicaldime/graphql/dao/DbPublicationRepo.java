package com.practicaldime.graphql.dao;

import com.practicaldime.graphql.app.Result;
import com.practicaldime.graphql.entity.Blog;
import com.practicaldime.graphql.entity.Ebook;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.TypedQuery;
import java.util.List;

@Repository
public class DbPublicationRepo implements PublicationRepo {

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public Result<Blog> findBlogById(Long id) {
        try {
            Session session = this.sessionFactory.openSession();
            Blog blog = session.get(Blog.class, id);
            session.close();
            return Result.of(blog);
        } catch (Exception e) {
            return new Result<>(null, e.getMessage());
        }
    }

    @Override
    public Result<Ebook> findEbookById(Long id) {
        try {
            Session session = this.sessionFactory.openSession();
            Ebook ebook = session.get(Ebook.class, id);
            session.close();
            return Result.of(ebook);
        } catch (Exception e) {
            return new Result<>(null, e.getMessage());
        }
    }

    @Override
    public Result<Blog> findBlogByTitle(String title) {
        try {
            Session session = this.sessionFactory.openSession();
            TypedQuery<Blog> search = session.createQuery("SELECT b FROM Blog b JOIN a.author a WHERE b.title = :title",
                    Blog.class);
            search.setParameter("title", title);
            Blog result = search.getSingleResult();
            session.close();
            return Result.of(result);
        } catch (Exception e) {
            return new Result<>(null, e.getMessage());
        }
    }

    @Override
    public Result<Ebook> findEbookByTitle(String title) {
        try {
            Session session = this.sessionFactory.openSession();
            TypedQuery<Ebook> search = session
                    .createQuery("SELECT e FROM Ebook e WHERE e.title = :title", Ebook.class);
            search.setParameter("title", title);
            Ebook result = search.getSingleResult();
            session.close();
            return Result.of(result);
        } catch (Exception e) {
            return new Result<>(null, e.getMessage());
        }
    }

    @Override
    public Result<List<Blog>> fetchBlogs(int start, int size) {
        try {
            Session session = this.sessionFactory.openSession();
            Query<Blog> query = session.createQuery("from Blog", Blog.class);
            query.setMaxResults(size);
            query.setFirstResult(start);
            List<Blog> result = query.list();
            session.close();
            return Result.of(result);
        } catch (Exception e) {
            return new Result<>(null, e.getMessage());
        }
    }

    @Override
    public Result<List<Ebook>> fetchEbooks(int start, int size) {
        try {
            Session session = this.sessionFactory.openSession();
            Query<Ebook> query = session.createQuery("from Ebook", Ebook.class);
            query.setMaxResults(size);
            query.setFirstResult(start);
            List<Ebook> result = query.list();
            session.close();
            return Result.of(result);
        } catch (Exception e) {
            return new Result<>(null, e.getMessage());
        }
    }

    @Override
    public Result<Integer> createBlog(Blog blog) {
        try {
            Session session = this.sessionFactory.openSession();
            Transaction tx = session.beginTransaction();
            session.persist(blog);
            tx.commit();
            session.close();
            return Result.of(1);
        } catch (Exception e) {
            return new Result<>(null, e.getMessage());
        }
    }

    @Override
    public Result<Integer> createEbook(Ebook ebook) {
        try {
            Session session = this.sessionFactory.openSession();
            Transaction tx = session.beginTransaction();
            session.persist(ebook);
            tx.commit();
            session.close();
            return Result.of(1);
        } catch (Exception e) {
            return new Result<>(null, e.getMessage());
        }
    }

}
