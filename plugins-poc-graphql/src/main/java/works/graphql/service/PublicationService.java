package works.graphql.service;

import java.util.List;

import works.graphql.entity.Blog;
import works.graphql.entity.Ebook;

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
