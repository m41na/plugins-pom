package com.practicaldime.plugins.users.service;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.practicaldime.common.util.AResult;
import com.practicaldime.common.util.CatchExceptions;
import com.practicaldime.common.util.PasswordUtil;
import com.practicaldime.common.util.RandomString;
import com.practicaldime.common.util.UserTokenGen;
import com.practicaldime.common.util.Validatable;
import com.practicaldime.common.entity.users.AccStatus;
import com.practicaldime.common.entity.users.Account;
import com.practicaldime.common.entity.users.LoginStatus;
import com.practicaldime.common.entity.users.Profile;
import com.practicaldime.plugins.users.config.ServiceProperties;
import com.practicaldime.plugins.users.dao.UserDao;

@Service("UserService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
@Validatable
@CatchExceptions
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;
    @Autowired
    private ServiceProperties serviceProperties;

    public UserDao getUserDao() {
        return userDao;
    }

    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    public void setServiceProperties(ServiceProperties serviceProperties) {
        this.serviceProperties = serviceProperties;
    }

    public ServiceProperties getServiceProperties() {
        return serviceProperties;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, readOnly = false, rollbackFor = Exception.class)
    public AResult<String> createAccount(Account account) {
        AResult<Profile> profileResult = userDao.findByEmail(account.getProfile().getEmailAddress());
        if (profileResult.errors.isEmpty()) {
        	Profile profile = profileResult.data;
            AResult<Account> accountResult = userDao.findByUsername(account.getUsername());
            if (accountResult.data == null) {
                account.setProfile(profile);
                account.setPassword(PasswordUtil.hashPassword(account.getPassword()));
                account.setCreatedTs(new Date());
                AResult<Account> newAccount = userDao.register(account);
                if (newAccount.errors.isEmpty()) {
                    String activation = UserTokenGen.getInstance().encrypt(account.getProfile().getEmailAddress());
                    System.out.printf("activation '%s' - %s%n", account.getUsername(), activation);
                    return new AResult<>(activation);
                } else {
                    return new AResult<>(400, "failed to create new account");
                }
            } else {
                return new AResult<>(400, "username is already taken");
            }            
        } else {
            return new AResult<>(404, "there is no profile for this email");
        }
    }

    @Override
    public AResult<Account> getAccount(long accountId) {
    	AResult<Account> getAccount = userDao.findAccount(accountId);
    	if(getAccount.errors.isEmpty()) {
    		Account account = getAccount.data;
    		account.setPassword(null);
    		return new AResult<>(account);
    	}
    	return getAccount;
    }

    @Override
    public AResult<Account> getAccount(String username) {
    	AResult<Account> getAccount = userDao.findByUsername(username);
        if(getAccount.errors.isEmpty()) {
    		Account account = getAccount.data;
    		account.setPassword(null);
    		return new AResult<>(account);
    	}
    	return getAccount;
    }

    @Override
    public AResult<Account> getAccountByEmail(String emailAddress) {
    	AResult<Account> getAccount = userDao.searchByEmail(emailAddress);
    	if(getAccount.errors.isEmpty()) {
    		Account account = getAccount.data;
    		account.setPassword(null);
    		return new AResult<>(account);
    	}
    	return getAccount;
    }

    @Override
    public AResult<Integer> updatePassword(long accountId, char[] password) {
        return userDao.update(accountId, password);
    }
    
    @Override
    public AResult<char[]> fetchPassword(long accountId){
    	AResult<Account> getAccount = userDao.findAccount(accountId);
    	if(getAccount.errors.isEmpty()) {
    		return new AResult<>(getAccount.data.getPassword());
    	}
    	return new AResult<>(getAccount.data.getPassword());
    }

    @Override
    public AResult<char[]> resetPassword(long accountId) {
        String generatedPassword = RandomString.generate();
        char[] newPassword = PasswordUtil.hashPassword(generatedPassword.toCharArray());
        AResult<Integer> resetResult = userDao.update(accountId, newPassword);
        if (resetResult.data > 0) {
            return new AResult<>(newPassword);
        } else {
            return new AResult<char[]>(resetResult.errorString(), 500);
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public AResult<Account> updateAccount(Account account) {
        long accountId = account.getId();
        AResult<Integer> updateStatus = userDao.update(accountId, account.getStatus());
        if (updateStatus.errors.isEmpty()) {
            AResult<Integer> updateRole = userDao.update(accountId, account.getRole());
            if (updateRole.errors.isEmpty()) {
                AResult<Account> doUpdate = userDao.findAccount(accountId);
                if (doUpdate.errors.isEmpty()) {
                	if(doUpdate.errors.isEmpty()) {
                		Account updated = doUpdate.data;
                		updated.setPassword(null);
                		return new AResult<>(updated);
                	}
                	return doUpdate;
                } else {
                    return new AResult<>(doUpdate.data);
                }
            } else {
                return new AResult<>(200, null);
            }
        } else {
            return new AResult<>(200, null);
        }
    }

    @Override
    public AResult<Profile> createProfile(Profile profile) {
    	AResult<Profile> profileResult = userDao.findByEmail(profile.getEmailAddress());
    	if(profileResult.data == null) {
    		return userDao.register(profile);
    	} else {
            return new AResult<>("email is already in use", 400);
        }
    }

    @Override
    public AResult<Profile> getProfile(long profileId) {
        return userDao.findProfile(profileId);
    }

    @Override
    public AResult<Profile> getProfile(String emailAddress) {
        return userDao.findByEmail(emailAddress);
    }

    @Override
    public AResult<Profile> updateProfile(Profile profile) {
        return userDao.update(profile);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public AResult<LoginStatus> accountLogin(String username, char[] password) {
        try {
            AResult<Account> result = userDao.findByUsername(username);
            if (result.errors.isEmpty()) {
                Account account = result.data;

                //check account status
                String accStatus = account.getStatus().toString();
                //check if account is deleted            
                if (AccStatus.deleted.toString().equals(accStatus)) {
                    String loginInfo = "This account is currently inactive. Request a recover link to reactivate the account";
                    return new AResult<>(loginInfo, 403);
                }

                if (AccStatus.disabled.toString().equals(accStatus)) {
                    String loginInfo = "This account is currently disabled. Request a restore link to reanable the account";
                    return new AResult<>(loginInfo, 403);
                }

                if (AccStatus.unverified.toString().equals(accStatus)) {
                    String loginInfo = "This account is pending activation. Request an activation link if you don't have one already";
                    return new AResult<>(loginInfo, 403);
                }

                //fetch login status
                List<LoginStatus> currentStatus = userDao.fetchLoginStatus(account.getId()).data;

                //check if locked
                if (AccStatus.locked.toString().equals(accStatus)) {
                    //check if lockout is not expired
                    LoginStatus lockStatus = currentStatus.stream().filter((LoginStatus t) -> t.getLockExpiry() != null).findFirst().get();
                    Calendar endLock = Calendar.getInstance();
                    endLock.add(Calendar.MINUTE, -serviceProperties.getLockoutDuration());
                    if (lockStatus.getLockExpiry().before(new Date())) {
                        String loginInfo = "This account is currently locked untill " + new SimpleDateFormat("hh:mm:ss A 'on' dd MMM yyyy").format(lockStatus.getLockExpiry());
                        return new AResult<>(loginInfo, 403);
                    } else {
                        AResult<Integer> unlock = userDao.update(account.getId(), AccStatus.active);
                        if (!unlock.errors.isEmpty()) {
                            //TODO: Handle this scenario in a different way
                            System.err.println("Login status not successfully unlocked");
                        }
                    }
                }

                //create login status
                LoginStatus login = new LoginStatus();
                login.setAccountId(account.getId());

                //get login attempts
                int loginAttempts = currentStatus.isEmpty() ? 0
                        : currentStatus.stream().reduce((LoginStatus t, LoginStatus u) -> t.getLoginAttempts() > u.getLoginAttempts() ? t : u).get().getLoginAttempts();

                //attempt to login
                char[] dbpassword = fetchPassword(account.getId()).data;
                if (PasswordUtil.verifyPassword(new String(password), dbpassword)) {
                    login.setStatusInfo("login ok");
                    login.setLoginSuccess(new Date());
                    login.setLoginToken(UserTokenGen.getInstance().encrypt(username));
                    login.setStatusCreated(new Date());
                    //clear login statuses
                    AResult<Integer> cleared = userDao.clearLoginStatus(account.getId());
                    if (!cleared.errors.isEmpty()) {
                        //TODO: Handle this scenario in a different way
                        System.err.println("Login status entries not successfully cleared");
                    }
                    //save login info
                    AResult<Integer> added = userDao.addLoginStatus(login);
                    if (!added.errors.isEmpty()) {
                        //TODO: Handle this scenario in a different way
                        System.err.println("Login status NOT saved properly - " + added.data);
                    }
                    return new AResult<>(login);
                } else {
                    //add login status and increase count for login attempt
                    String reason = "password did not match";
                    login.setStatusInfo(reason);
                    login.setLoginAttempts(loginAttempts + 1);
                    login.setStatusCreated(new Date());
                    AResult<Integer> added = userDao.addLoginStatus(login);
                    if (!added.errors.isEmpty()) {
                        //TODO: Handle this scenario in a different way
                        System.err.println("Login status NOT saved properly - " + added.data);
                    }

                    //check login attempts
                    if (login.getLoginAttempts() > serviceProperties.getMaxAttempts()) {
                        //lock the account
                        AResult<Integer> locked = userDao.update(account.getId(), AccStatus.locked);
                        if (!locked.errors.isEmpty()) {
                            //TODO: Handle this scenario in a differnt way
                            System.err.println("Account status NOT updated properly - " + added.data);
                        }
                    }
                    return new AResult<>(reason, 403);
                }
            } else {
                return new AResult<>(200,null);
            }
        } catch (NumberFormatException e) {
            return new AResult<>(e.getMessage(), 403);
        }
    }

    @Override
    public AResult<Integer> verifyAccount(String token) {
        String[] decrypted = UserTokenGen.getInstance().decrypt(token);
        long time = Long.valueOf(decrypted[1]);
        //find account to activate
        AResult<Account> findAccount = userDao.searchByEmail(decrypted[0]);
        if (findAccount.errors.isEmpty()) {
            Account account = findAccount.data;
            Date createdTs = account.getCreatedTs();
            long days = Duration.between(createdTs.toInstant(), new Date(time).toInstant()).toDays();
            if (days > 0) {
                return new AResult<>("The token does not appear to be valid", 403);
            }
            AResult<Integer> updated = userDao.update(account.getId(), AccStatus.active);
            return (updated.data == 1) ? updated : new AResult<>(updated.data);
        } else {
            return new AResult<>(findAccount.code);
        }
    }

    @Override
    public AResult<Integer> toggleActivate(long accountId) {
        AResult<Account> findAccount = userDao.findAccount(accountId);
        if (findAccount.errors.isEmpty()) {
            AccStatus status = findAccount.data.getStatus();
            switch (status) {
                case active: {
                    return userDao.update(accountId, AccStatus.deleted);
                }
                case deleted: {
                    return userDao.update(accountId, AccStatus.active);
                }
                default: {
                    return new AResult<>("Cannot toggle status for this account", 403);
                }
            }
        } else {
            return new AResult<>("Cannot locate an account with this id", 404);
        }
    }

    @Override
    public AResult<Integer> recoverAccount(String email, String username, char[] password) {
        if (email != null) {
            AResult<Account> findAccount = userDao.searchByEmail(email);
            if (findAccount.errors.isEmpty()) {
                return userDao.update(findAccount.data.getId(), PasswordUtil.hashPassword(password));
            } else {
                return new AResult<>("Cannot locate an account with email address", 404);
            }
        } else if (username != null) {
            AResult<Account> findAccount = userDao.findByUsername(username);
            if (findAccount.errors.isEmpty()) {
                return userDao.update(findAccount.data.getId(), PasswordUtil.hashPassword(password));
            } else {
                return new AResult<>("Cannot locate an account with username", 404);
            }
        }
        return new AResult<>("neither email nor username is provided", 400);
    }

    @Override
    public AResult<Integer> accountLogout(long accountId) {
        return userDao.clearLoginStatus(accountId);
    }
}
