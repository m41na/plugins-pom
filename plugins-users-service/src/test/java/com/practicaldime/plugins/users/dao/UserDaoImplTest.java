package com.practicaldime.plugins.users.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Date;
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

import com.practicaldime.common.util.AppResult;
import com.practicaldime.domain.users.AccRole;
import com.practicaldime.domain.users.AccStatus;
import com.practicaldime.domain.users.Account;
import com.practicaldime.domain.users.LoginStatus;
import com.practicaldime.domain.users.Profile;
import com.practicaldime.plugins.users.dao.UserDao;

import com.practicaldime.plugins.users.config.UsersDaoTestConfig;

@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = UsersDaoTestConfig.class, loader = AnnotationConfigContextLoader.class)
@Sql({"/sql/create-tables.sql"})
@Sql(scripts = "/sql/insert-data.sql", config = @SqlConfig(commentPrefix = "--"))
public class UserDaoImplTest {

    @Autowired
    private UserDao dao;

    @Test
    public void testFindAccountById() {
        AppResult<Account> account = dao.findAccount(2);
        String expecting = "Guest";
        assertEquals(String.format("Expecting '%s' for first name", expecting), expecting,
                account.getEntity().getProfile().getFirstName());
    }

    @Test
    public void testFindAccountByUsername() {
        AppResult<Account> account = dao.findByUsername("admin");
        String expecting = "Admin";
        assertEquals(String.format("Expecting '%s' for first name", expecting), expecting, account.getEntity().getProfile().getFirstName());
    }

    @Test
    public void testFindAccountByEmailAddress() {
        String emailAddress = "guest.user@host.com";
        AppResult<Account> account = dao.searchByEmail(emailAddress);
        String expecting = "Guest";
        assertEquals(String.format("Expecting '%s' for first name", expecting), expecting, account.getEntity().getProfile().getFirstName());
    }

    @Test
    public void testRegisterAccount() {
        // register user profile
        Profile profile = new Profile();
        String email = DaoUtils.randomAlphaNumeric(10) + "@friendmail.com";
        profile.setEmailAddress(email);
        profile.setFirstName("tester1");
        profile.setLastName("laster");
        AppResult<Profile> result = dao.register(profile);
        // register account for the user
        Account account = new Account();
        account.setUsername("user1");
        account.setPassword("password1".toCharArray());
        account.setProfile(result.getEntity());
        AppResult<Account> result2 = dao.register(account);
        assertTrue(result2.getEntity().getId() > 0);
        // find account by username
        AppResult<Account> acc = dao.findByUsername("user1");
        assertNotNull(acc);
        assertEquals("expecting 'user1'", "user1", acc.getEntity().getUsername());
    }

    @Test
    public void testUpdatePassword() {
        Account acc = dao.findByUsername("admin").getEntity();
        assertNotNull(acc);
        assertEquals("expecting 'admin'", "admin", acc.getUsername());
        String newPassword = "buzzbuzz";
        AppResult<Integer> result = dao.update(acc.getId(), newPassword.toCharArray());
        if (result.getError() != null) {
            fail("Failed to updated account password");
        }
        Account updated = dao.findByUsername("admin").getEntity();
        assertEquals("Expecting " + newPassword, newPassword, new String(updated.getPassword()));
    }

    @Test
    public void testUpdateAccountStatus() {
        Account acc = dao.findByUsername("guest").getEntity();
        assertNotNull(acc);
        assertEquals("expecting 'guest'", "guest", acc.getUsername());
        //update status
        AccStatus newStatus = AccStatus.locked;
        AppResult<Integer> updated = dao.update(acc.getId(), newStatus);
        assertEquals("Expecting 1", 1, updated.getEntity().intValue());
    }

    @Test
    public void testUpdateAccountRole() {
        Account acc = dao.findByUsername("guest").getEntity();
        assertNotNull(acc);
        assertEquals("expecting 'guest'", "guest", acc.getUsername());
        //update role
        AccRole newRole = AccRole.super_user;
        AppResult<Integer> updated = dao.update(acc.getId(), newRole);
        assertEquals("Expecting 1", 1, updated.getEntity().intValue());
    }

    @Test
    public void testDeleteAccount() {
        testRegisterAccount();
        // find account by username
        AppResult<Account> acc = dao.findByUsername("user1");
        assertNotNull(acc);
        AppResult<Integer> result = dao.deleteAccount(acc.getEntity().getId());
        assertEquals("Expecting 1", 1, result.getEntity().intValue());
        // find removed account
        acc = dao.findByUsername("user1");
        assertEquals(null, acc.getEntity());
    }

    @Test
    public void testFindProfileById() {
        AppResult<Profile> profile = dao.findProfile(2);
        String expecting = "Guest";
        assertEquals(String.format("Extecting '%s' as first name", expecting), expecting,
                profile.getEntity().getFirstName());
    }

    @Test
    public void testFindProfileByEmail() {
        AppResult<Profile> profile = dao.findByEmail("guest.user@host.com");
        String expecting = "User";
        assertEquals(String.format("Extecting '%s' as last name", expecting), expecting,
                profile.getEntity().getLastName());
    }

    @Test
    public void testRegisterProfile() {
        // register user profile
        Profile profile = new Profile();
        profile.setEmailAddress("tester1@sample.com");
        profile.setFirstName("tester1");
        profile.setLastName("laster");
        AppResult<Profile> result = dao.register(profile);
        assertNotNull(result.getEntity());

        // retrieve by email
        AppResult<Profile> fromDb = dao.findByEmail(profile.getEmailAddress());
        assertEquals(result.getEntity().getId(), fromDb.getEntity().getId());
    }

    @Test
    public void testUpdateProfile() {
        Profile generated = DaoUtils.generateAndRegisterProfile(dao);
        // find profile by email addr
        Profile profile = dao.findByEmail(generated.getEmailAddress()).getEntity();
        String phone = "7776669999";
        profile.setPhoneNumber(phone);
        AppResult<Profile> result = dao.update(profile);
        assertEquals("Expecting same id", profile.getId(), result.getEntity().getId());
        assertEquals("Expecting different numbers", profile.getPhoneNumber(), result.getEntity().getPhoneNumber());
    }

    @Test
    public void testDeleteProfile() {
        Profile generated = DaoUtils.generateAndRegisterProfile(dao);
        // find profile by email addr
        AppResult<Profile> profile = dao.findByEmail(generated.getEmailAddress());
        assertNotNull(profile);
        AppResult<Integer> result = dao.deleteProfile(profile.getEntity().getId());
        assertEquals("Expecting 1", 1, result.getEntity().intValue());
        // find removed account
        profile = dao.findByEmail(generated.getEmailAddress());
        assertEquals(null, profile.getEntity());
    }
    
    @Test
    public void testFetchLoginStatus() {
        AppResult<List<LoginStatus>> logins = dao.fetchLoginStatus(1l);
        int expecting = 2;
        assertEquals(String.format("Extecting '%d' as list size", expecting), expecting, logins.getEntity().size());
    }
    
    @Test
    public void testAddLoginStatus() {
        LoginStatus login = new LoginStatus();
        login.setAccountId(1l);
        login.setLockExpiry(new Date());
        login.setLoginAttempts(2);
        login.setLoginToken("yes we can");
        login.setStatusCreated(new Date());
        login.setStatusInfo("moving forward");
        AppResult<Integer> created = dao.addLoginStatus(login);
        int expecting = 1;
        assertEquals(String.format("Extecting '%d' as number of rows added", expecting), expecting,  created.getEntity().intValue());
    }
    
    @Test
    public void testClearLoginStatus() {
        AppResult<Integer> rows = dao.clearLoginStatus(1l);
        int expecting = 2;
        assertEquals(String.format("Extecting '%d' as number of rows cleared", expecting), expecting, rows.getEntity().intValue());
    }
}
