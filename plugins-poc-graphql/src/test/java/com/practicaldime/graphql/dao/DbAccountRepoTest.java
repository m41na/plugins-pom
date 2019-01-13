package com.practicaldime.graphql.dao;

import static org.junit.Assert.assertEquals;

import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.transaction.annotation.Transactional;

import com.practicaldime.graphql.app.Result;
import com.practicaldime.graphql.config.HibernateTestConfig;
import com.practicaldime.graphql.entity.Account;
import com.practicaldime.graphql.entity.Profile;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = HibernateTestConfig.class, loader = AnnotationConfigContextLoader.class)
@Transactional
public class DbAccountRepoTest {

	@Autowired
	private DbAccountRepo dao;

	@Test
	public void testFindById() {
		Result<Account> byId = dao.findById(1l);
		assertEquals("Expacting 1", 1, byId.data.id.longValue());
	}

	@Test
	public void testFetchAccounts() {
		Result<List<Account>> list = dao.fetchAccounts();
		assertEquals("Expecting 1", 1, list.data.size());
	}

	@Test
	public void testCreateAccount() {
		Account account = new Account();
		account.emailAddr = "steve@mike.com";
		account.username = "steve";
		account.password = "mike";
		Profile profile = new Profile();
		profile.firstName = "steve";
		profile.lastName = "mike";
		profile.aboutMe = "get up and ride";
		profile.birthDay = new Date();
		account.profile = profile;

		Result<Integer> result = dao.createAccount(account);
		if (result.data != null) {
			assertEquals("Expecting 1", 1, result.data.intValue());
		} else {
			System.err.println(result.error);
		}
		
		// fine by username
		Result<Account> created = dao.findByUsername(account.username);
		assertEquals("Expecting '" + account.emailAddr + "'", account.emailAddr, created.data.emailAddr);
	}
}
