package works.graphql.service;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import works.graphql.dao.PublicationRepo;
import works.graphql.entity.Blog;
import works.graphql.entity.Ebook;

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
