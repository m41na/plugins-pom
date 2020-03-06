package com.practicaldime.plugins.users.service;

import com.practicaldime.common.entity.users.*;
import com.practicaldime.common.util.AResult;
import com.practicaldime.common.util.UserTokenGen;
import com.practicaldime.plugins.users.config.UsersDaoTestConfig;
import com.practicaldime.plugins.users.config.UsersServiceTestConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.*;

@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {UsersServiceTestConfig.class, UsersDaoTestConfig.class}, loader = AnnotationConfigContextLoader.class)
@Sql({"/sql/create-tables.sql"})
@Sql(scripts = "/sql/insert-data.sql", config = @SqlConfig(commentPrefix = "--"))
public class UserServiceImplTest {

    private final int maxLogin = 3;
    @Autowired
    private UserService service;

    @Test
    public void testCreateAccount() {
        // create profile
        Profile profile = new Profile();
        profile.setEmailAddress("service1@sample.com");
        profile.setFirstName("service1");
        profile.setLastName("laster");
        AResult<Profile> newProfile = service.createProfile(profile);
        assertTrue("Expecting no error", newProfile.errors.isEmpty());

        // create account
        Account account = new Account();
        account.setUsername("service1acc1");
        account.setPassword("password1".toCharArray());
        account.setProfile(newProfile.data);

        // register account
        AResult<String> result = service.createAccount(account);
        assertTrue("Expecting activation token", result.data != null);
        System.out.println(result.data);
        // find account by username
        AResult<Account> acc = service.getAccount("service1acc1");
        assertEquals("expecting 'service1acc1'", "service1acc1", acc.data.getUsername());
    }

    @Test
    public void testGetAccountById() {
        Account account = service.getAccount(2).data;
        assertEquals("Expecting 2", 2, account.getId().longValue());
    }

    @Test
    public void testGetAccountByUsername() {
        Account account = service.getAccount("admin").data;
        assertEquals("Expecting 'admin'", "admin", account.getUsername());
    }

    @Test
    public void testGetAccountByEmailAddress() {
        String email = "admin.user@host.com";
        Account account = service.getAccountByEmail(email).data;
        assertEquals("Expecting " + email, email, account.getProfile().getEmailAddress());
    }

    @Test
    public void testUpdateAccount() {
        Account account = service.getAccount("admin").data;
        account.setRole(AccRole.super_user);
        account = service.updateAccount(account).data;
        assertEquals("Expecting 'super_user'", "super_user", account.getRole().toString());
        // get new account instance
        account = service.getAccount("admin").data;
        assertEquals("Expecting 'super_user'", "super_user", account.getRole().toString());
    }

    @Test
    public void testResetPassword() {
        Account account = service.getAccount("admin").data;
        char[] dbPass = service.fetchPassword(account.getId()).data;
        char[] newPass = service.resetPassword(account.getId()).data;
        // get updated account instance
        assertFalse("Expecting different passwords", new String(newPass).equals(new String(dbPass)));
    }

    @Test
    public void testUpdatePassword() {
        Account account = service.getAccount("admin").data;
        String newPassword = "gemini";
        int updated = service.updatePassword(account.getId(), newPassword.toCharArray()).data;
        assertEquals("Expecting 1 row updated", 1, updated);
    }

    @Test
    public void testCreateProfile() {
        // create profile
        Profile profile = new Profile();
        profile.setEmailAddress("testCreateProfile@sample.com");
        profile.setFirstName("testCreateProfile");
        profile.setLastName("laster");
        AResult<Profile> result = service.createProfile(profile);
        assertEquals("Expecting 'testCreateProfile'", "testCreateProfile", result.data.getFirstName());
    }

    @Test
    public void testGetProfileById() {
        Profile profile = service.getProfile(2).data;
        assertEquals("Expecting 2", 2, profile.getId().longValue());
    }

    @Test
    public void testGetProfileByEmailAddress() {
        Profile profile = service.getProfile("admin.user@host.com").data;
        assertEquals("Expecting 'admin.user@host.com'", "admin.user@host.com", profile.getEmailAddress());
    }

    @Test
    public void testUpdateProfile() {
        Profile profile = service.getProfile(1).data;
        profile.setFirstName("bambam");
        profile = service.updateProfile(profile).data;
        // get new account instance
        assertEquals("Expecting 'bambam'", "bambam", profile.getFirstName());
    }

    @Test
    public void testAccountLogin() {
        testCreateAccount();
        String username = "service1acc1";
        String password = "password1";
        AResult<LoginStatus> login = service.accountLogin(username, password.toCharArray());
        LoginStatus result = login.data;
        assertNull("Expecting null login", result);
        assertTrue("Expecting 'This account is pending activation'", login.errorString().contains("This account is pending activation"));
        //activate account
        String email = "service1@sample.com";
        String token = UserTokenGen.getInstance().encrypt(email);
        AResult<Integer> activated = service.verifyAccount(token);
        assertEquals("Expecting 1 row updated", 1, activated.data.intValue());
        //retrieve user by email
        AResult<Account> newAccount = service.getAccount(username);
        //try to login again now
        login = service.accountLogin(username, password.toCharArray());
        result = login.data;
        assertNotNull("Expecting non-null login", result);
        assertEquals("Expecting same user id", newAccount.data.getId().longValue(), result.getAccountId().longValue());
        //try to login again wuthout signing out
        login = service.accountLogin(username, password.toCharArray());
        result = login.data;
        assertNotNull("Expecting non-null login", result);
        assertEquals("Expecting same user id", newAccount.data.getId().longValue(), result.getAccountId().longValue());
        //try three time with wrong credentials
        int attempts = 0;
        while (attempts <= maxLogin) {
            login = service.accountLogin(username, (password + "wrong").toCharArray());
            attempts++;
        }
        result = login.data;
        assertNull("Expecting null login", result);
        //retrieve user by email
        AResult<Account> findLocked = service.getAccount(username);
        Account lockedAccount = findLocked.data;
        assertEquals("Expecting 'locked'", AccStatus.locked, lockedAccount.getStatus());
    }

    @Test
    public void testVerifyAccount() {
        testCreateAccount();
        String username = "service1acc1";
        String password = "password1";
        AResult<LoginStatus> login = service.accountLogin(username, password.toCharArray());
        LoginStatus result = login.data;
        assertNull("Expecting null login", result);
        assertTrue("Expecting 'This account is pending activation'", login.errorString().contains("This account is pending activation"));
        //activate account
        String email = "service1@sample.com";
        String token = UserTokenGen.getInstance().encrypt(email);
        AResult<Integer> activated = service.verifyAccount(token);
        assertEquals("Expecting 1 row updated", 1, activated.data.intValue());
    }

    @Test
    public void testToggleActivate() {
        long accountId = 1l;
        AResult<Integer> toggled = service.toggleActivate(accountId);
        assertEquals("Expecting 1", 1, toggled.data.intValue());
        //fetch account by id
        AResult<Account> findAccount = service.getAccount(accountId);
        assertEquals("Expecting 'deleted'", AccStatus.deleted.toString(), findAccount.data.getStatus().toString());
        //toggle back
        toggled = service.toggleActivate(accountId);
        assertEquals("Expecting 1", 1, toggled.data.intValue());
        //fetch account by id
        findAccount = service.getAccount(accountId);
        assertEquals("Expecting 'active'", AccStatus.active.toString(), findAccount.data.getStatus().toString());
    }

    @Test
    public void testRecoverAccount() {
        String email = "admin.user@host.com";
        String username = "admin";
        String newPass1 = "newPassword1";
        String newPass2 = "newPassword2";

        AResult<Integer> rec1 = service.recoverAccount(email, null, newPass1.toCharArray());
        assertEquals("Expecting 1 row recovered", 1, rec1.data.intValue());
        //signin in with new password
        AResult<LoginStatus> login1 = service.accountLogin(username, newPass1.toCharArray());
        assertEquals("Expecting id 1", 1l, login1.data.getAccountId().longValue());

        AResult<Integer> rec2 = service.recoverAccount(null, username, newPass2.toCharArray());
        assertEquals("Expecting 1 row recovered", 1, rec2.data.intValue());
        //signin in with new password
        AResult<LoginStatus> login2 = service.accountLogin(username, newPass2.toCharArray());
        assertEquals("Expecting id 1", 1l, login2.data.getAccountId().longValue());
    }

    @Test
    public void testAccountLogout() {
        String username = "admin";
        String password = "p455w0rd";

        AResult<Integer> recovered = service.recoverAccount(null, username, password.toCharArray());
        assertEquals("Expecting 1 row recovered", 1, recovered.data.intValue());

        AResult<LoginStatus> login = service.accountLogin(username, password.toCharArray());
        assertEquals("Expecting id 1", 1l, login.data.getAccountId().longValue());

        AResult<Integer> logout = service.accountLogout(1l);
        assertEquals("Expecting 1 row removed", 1, logout.data.intValue());
    }
}
