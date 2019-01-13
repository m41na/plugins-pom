package com.practicaldime.plugins.users.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

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
import com.practicaldime.common.util.ResStatus;
import com.practicaldime.common.util.UserTokenGen;
import com.practicaldime.domain.users.AccRole;
import com.practicaldime.domain.users.AccStatus;
import com.practicaldime.domain.users.Account;
import com.practicaldime.domain.users.LoginStatus;
import com.practicaldime.domain.users.Profile;
import com.practicaldime.plugins.users.service.UserService;

import com.practicaldime.plugins.users.config.UsersDaoTestConfig;
import com.practicaldime.plugins.users.config.UsersServiceTestConfig;

@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {UsersServiceTestConfig.class, UsersDaoTestConfig.class}, loader = AnnotationConfigContextLoader.class)
@Sql({"/sql/create-tables.sql"})
@Sql(scripts = "/sql/insert-data.sql", config = @SqlConfig(commentPrefix = "--"))
public class UserServiceImplTest {

    @Autowired
    private UserService service;
    private final int maxLogin = 3;

    @Test
    public void testCreateAccount() {
        // create profile
        Profile profile = new Profile();
        profile.setEmailAddress("service1@sample.com");
        profile.setFirstName("service1");
        profile.setLastName("laster");
        AppResult<Profile> newProfile = service.createProfile(profile);
        assertNull("Expecting no error", newProfile.getError());

        // create account
        Account account = new Account();
        account.setUsername("service1acc1");
        account.setPassword("password1".toCharArray());
        account.setProfile(newProfile.getEntity());

        // register account
        AppResult<String> result = service.createAccount(account);
        assertTrue("Expecting activation token", result.getEntity() != null);
        System.out.println(result.getEntity());
        // find account by username
        AppResult<Account> acc = service.getAccount("service1acc1");
        assertEquals("expecting 'service1acc1'", "service1acc1", acc.getEntity().getUsername());
    }

    @Test
    public void testGetAccountById() {
        Account account = service.getAccount(2).getEntity();
        assertEquals("Expecting 2", 2, account.getId());
    }

    @Test
    public void testGetAccountByUsername() {
        Account account = service.getAccount("admin").getEntity();
        assertEquals("Expecting 'admin'", "admin", account.getUsername());
    }

    @Test
    public void testGetAccountByEmailAddress() {
        String email = "admin.user@host.com";
        Account account = service.getAccountByEmail(email).getEntity();
        assertEquals("Expecting " + email, email, account.getProfile().getEmailAddress());
    }

    @Test
    public void testUpdateAccount() {
        Account account = service.getAccount("admin").getEntity();
        account.setRole(AccRole.super_user);
        account = service.updateAccount(account).getEntity();
        assertEquals("Expecting 'super_user'", "super_user", account.getRole().toString());
        // get new account instance
        account = service.getAccount("admin").getEntity();
        assertEquals("Expecting 'super_user'", "super_user", account.getRole().toString());
    }

    @Test
    public void testResetPassword() {
    	Account account = service.getAccount("admin").getEntity();
        char[] dbPass = service.fetchPassword(account.getId()).getEntity();
        char[] newPass = service.resetPassword(account.getId()).getEntity();
        // get updated account instance
        assertFalse("Expecting different passwords", new String(newPass).equals(new String(dbPass)));
    }

    @Test
    public void testUpdatePassword() {
        Account account = service.getAccount("admin").getEntity();
        String newPassword = "gemini";
        int updated = service.updatePassword(account.getId(), newPassword.toCharArray()).getEntity();
        assertEquals("Expecting 1 row updated", 1, updated);
    }

    @Test
    public void testCreateProfile() {
        // create profile
        Profile profile = new Profile();
        profile.setEmailAddress("testCreateProfile@sample.com");
        profile.setFirstName("testCreateProfile");
        profile.setLastName("laster");
        AppResult<Profile> result = service.createProfile(profile);
        assertEquals("Expecting 'testCreateProfile'", "testCreateProfile", result.getEntity().getFirstName());
    }

    @Test
    public void testGetProfileById() {
        Profile profile = service.getProfile(2).getEntity();
        assertEquals("Expecting 2", 2, profile.getId());
    }

    @Test
    public void testGetProfileByEmailAddress() {
        Profile profile = service.getProfile("admin.user@host.com").getEntity();
        assertEquals("Expecting 'admin.user@host.com'", "admin.user@host.com", profile.getEmailAddress());
    }

    @Test
    public void testUpdateProfile() {
        Profile profile = service.getProfile(1).getEntity();
        profile.setFirstName("bambam");
        profile = service.updateProfile(profile).getEntity();
        // get new account instance
        assertEquals("Expecting 'bambam'", "bambam", profile.getFirstName());
    }

    @Test
    public void testAccountLogin() {
        testCreateAccount();
        String username = "service1acc1";
        String password = "password1";
        AppResult<LoginStatus> login = service.accountLogin(username, password.toCharArray());
        LoginStatus result = login.getEntity();
        assertNull("Expecting null login", result);
        ResStatus status = login.getStatus();
        assertTrue("Expecting 'This account is pending activation'", status.getError().contains("This account is pending activation"));
        //activate account
        String email = "service1@sample.com";
        String token = UserTokenGen.getInstance().encrypt(email);
        AppResult<Integer> activated = service.verifyAccount(token);
        assertEquals("Expecting 1 row updated", 1, activated.getEntity().intValue());
        //retrieve user by email
        AppResult<Account> newAccount = service.getAccount(username);
        //try to login again now
        login = service.accountLogin(username, password.toCharArray());
        result = login.getEntity();
        assertNotNull("Expecting non-null login", result);
        assertEquals("Expecting same user id", newAccount.getEntity().getId(), result.getAccountId().longValue());
        //try to login again wuthout signing out
        login = service.accountLogin(username, password.toCharArray());
        result = login.getEntity();
        assertNotNull("Expecting non-null login", result);
        assertEquals("Expecting same user id", newAccount.getEntity().getId(), result.getAccountId().longValue());
        //try three time with wrong credentials
        int attempts = 0;
        while (attempts <= maxLogin) {
            login = service.accountLogin(username, (password + "wrong").toCharArray());
            attempts++;
        }
        result = login.getEntity();
        assertNull("Expecting null login", result);
        //retrieve user by email
        AppResult<Account> findLocked = service.getAccount(username);
        Account lockedAccount = findLocked.getEntity();
        assertEquals("Expecting 'locked'", AccStatus.locked, lockedAccount.getStatus());
    }

    @Test
    public void testVerifyAccount() {
        testCreateAccount();
        String username = "service1acc1";
        String password = "password1";
        AppResult<LoginStatus> login = service.accountLogin(username, password.toCharArray());
        LoginStatus result = login.getEntity();
        assertNull("Expecting null login", result);
        ResStatus status = login.getStatus();
        assertTrue("Expecting 'This account is pending activation'", status.getError().contains("This account is pending activation"));
        //activate account
        String email = "service1@sample.com";
        String token = UserTokenGen.getInstance().encrypt(email);
        AppResult<Integer> activated = service.verifyAccount(token);
        assertEquals("Expecting 1 row updated", 1, activated.getEntity().intValue());
    }

    @Test
    public void testToggleActivate() {
        long accountId = 1l;
        AppResult<Integer> toggled = service.toggleActivate(accountId);
        assertEquals("Expecting 1", 1, toggled.getEntity().intValue());
        //fetch account by id
        AppResult<Account> findAccount = service.getAccount(accountId);
        assertEquals("Expceting 'deleted'", AccStatus.deleted.toString(), findAccount.getEntity().getStatus().toString());
        //toggle back
        toggled = service.toggleActivate(accountId);
        assertEquals("Expecting 1", 1, toggled.getEntity().intValue());
        //fetch account by id
        findAccount = service.getAccount(accountId);
        assertEquals("Expecting 'active'", AccStatus.active.toString(), findAccount.getEntity().getStatus().toString());
    }

    @Test
    public void testRecoverAccount() {
        String email = "admin.user@host.com";
        String username = "admin";
        String newPass1 = "newPassword1";
        String newPass2 = "newPassword2";
        
        AppResult<Integer> rec1 =service.recoverAccount(email, null, newPass1.toCharArray());
        assertEquals("Expecting 1 row recovered", 1, rec1.getEntity().intValue());
        //signin in with new password
        AppResult<LoginStatus> login1 = service.accountLogin(username, newPass1.toCharArray());
        assertEquals("Expecting id 1", 1l, login1.getEntity().getAccountId().longValue());
        
        AppResult<Integer> rec2 =service.recoverAccount(null, username, newPass2.toCharArray());
        assertEquals("Expecting 1 row recovered", 1, rec2.getEntity().intValue());
        //signin in with new password
        AppResult<LoginStatus> login2 = service.accountLogin(username, newPass2.toCharArray());
        assertEquals("Expecting id 1", 1l, login2.getEntity().getAccountId().longValue());
    }

    @Test
    public void testAccountLogout() {
        String username = "admin";
        String password = "p455w0rd";
        
        AppResult<Integer> recovered =service.recoverAccount(null, username, password.toCharArray());
        assertEquals("Expecting 1 row recovered", 1, recovered.getEntity().intValue());
        
        AppResult<LoginStatus> login = service.accountLogin(username, password.toCharArray());
         assertEquals("Expecting id 1", 1l, login.getEntity().getAccountId().longValue());
         
        AppResult<Integer> logout = service.accountLogout(1l);
        assertEquals("Expecting 1 row removed", 1, logout.getEntity().intValue());
    }
}
