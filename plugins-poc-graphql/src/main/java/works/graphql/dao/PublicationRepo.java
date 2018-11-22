package works.graphql.dao;

import java.util.List;

import works.graphql.app.Result;
import works.graphql.entity.Blog;
import works.graphql.entity.Ebook;

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
