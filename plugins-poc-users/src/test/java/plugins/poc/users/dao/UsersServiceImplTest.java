package plugins.poc.users.dao;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.transaction.annotation.Transactional;

import plugins.poc.users.config.DaoTestConfig;
import plugins.poc.users.dao.User;
import plugins.poc.users.dao.UsersService;
import works.hop.plugins.api.PlugResult;

@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = DaoTestConfig.class, loader = AnnotationConfigContextLoader.class)
@Sql({ "/sql/create-tables.sql" })
@Sql(scripts = "/sql/insert-data.sql", config = @SqlConfig(commentPrefix = "--"))
public class UsersServiceImplTest {

	@Autowired
	private UsersService dao;

	@Test
	public void testCreate() {
		User user = new User();
		user.setFirstName("Steve");
		user.setLastName("Mike");
		user.setEmailAddress("steve.mike@email.com");
		user.setPhoneNumber("202-333-9090");
		PlugResult<User> created = dao.create(user);
		long expecting = 3;
		assertEquals(String.format("Expecting is %d", expecting), expecting, created.getEntity().getId().longValue());
	}

	@Test
	public void testFindUserById() {
		PlugResult<User> user = dao.find(1l);
		String expecting = "Admin";
		assertEquals(String.format("Expecting '%s' for first name", expecting), expecting, user.getEntity().getFirstName());
	}

	@Test
	public void testFindUsersList() {
		PlugResult<List<User>> users = dao.findUsers(1, 3);
		int expecting = 1;
		assertEquals(String.format("Expecting '%d' users", expecting), expecting, users.getEntity().size());
	}

}
