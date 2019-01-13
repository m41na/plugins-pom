package com.practicaldime.graphql.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.transaction.annotation.Transactional;

import com.practicaldime.graphql.app.Result;
import com.practicaldime.graphql.config.HibernateTestConfig;
import com.practicaldime.graphql.entity.Blog;
import com.practicaldime.graphql.entity.Ebook;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = HibernateTestConfig.class, loader = AnnotationConfigContextLoader.class)
@Transactional
@Ignore("fix me later")
public class DbPublicationRepoTest {

	@Autowired
	private DbPublicationRepo dao;
	
	@Test
	public void testFindBlogById() {
		Result<Blog> byId = dao.findBlogById(1l);
		assertEquals("Expecting 1", 1, byId.data.id.longValue());
	}

	@Test
	public void testFindEbookById() {
		Result<Ebook> byId = dao.findEbookById(1l);
		assertEquals("Expecting 1", 1, byId.data.id.longValue());
	}

	@Test
	public void testFindBlogByTitle() {
		Result<Blog> byId = dao.findBlogByTitle("simple blog");
		assertEquals("Expecting 1", 1, byId.data.id.longValue());
	}

	@Test
	public void testFindEbookByTitle() {
		Result<Ebook> byId = dao.findEbookByTitle("simple ebook");
		assertEquals("Expecting 1", 1, byId.data.id.longValue());
	}

	@Test
	public void testFetchBlogs() {
		Result<List<Blog>> list = dao.fetchBlogs(0, 2);
		assertEquals("Expecting 0", 0, list.data.size());
	}

	@Test
	public void testFetchEbooks() {
		Result<List<Ebook>> list = dao.fetchEbooks(0, 2);
		assertEquals("Expecting 0", 0, list.data.size());
	}

	@Test
	public void testCreateBlog() {
		assertTrue("Not yet implemented", true);
	}

	@Test
	public void testCreateEbook() {
		assertTrue("Not yet implemented", true);
	}
}
