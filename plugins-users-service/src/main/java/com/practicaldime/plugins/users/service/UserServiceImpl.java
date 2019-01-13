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

import com.practicaldime.common.util.AppResult;
import com.practicaldime.common.util.CatchExceptions;
import com.practicaldime.common.util.PasswordUtil;
import com.practicaldime.common.util.RandomString;
import com.practicaldime.common.util.ResStatus;
import com.practicaldime.common.util.UserTokenGen;
import com.practicaldime.common.util.Validatable;
import com.practicaldime.domain.users.AccStatus;
import com.practicaldime.domain.users.Account;
import com.practicaldime.domain.users.LoginStatus;
import com.practicaldime.domain.users.Profile;
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
    public AppResult<String> createAccount(Account account) {
        AppResult<Profile> profileResult = userDao.findByEmail(account.getProfile().getEmailAddress());
        if (profileResult.getError() == null) {
        	Profile profile = profileResult.getEntity();
            AppResult<Account> accountResult = userDao.findByUsername(account.getUsername());
            if (accountResult.getEntity() == null) {
                account.setProfile(profile);
                account.setPassword(PasswordUtil.hashPassword(account.getPassword()));
                account.setCreatedTs(new Date());
                AppResult<Account> newAccount = userDao.register(account);
                if (newAccount.getError() == null) {
                    String activation = UserTokenGen.getInstance().encrypt(account.getProfile().getEmailAddress());
                    System.out.printf("activation '%s' - %s%n", account.getUsername(), activation);
                    return new AppResult<>(activation);
                } else {
                    return new AppResult<>(400, "failed to create new account");
                }
            } else {
                return new AppResult<>(400, "username is already taken");
            }            
        } else {
            return new AppResult<>(404, "there is no profile for this email");
        }
    }

    @Override
    public AppResult<Account> getAccount(long accountId) {
    	AppResult<Account> getAccount = userDao.findAccount(accountId);
    	if(getAccount.getError() == null) {
    		Account account = getAccount.getEntity();
    		account.setPassword(null);
    		return new AppResult<>(account);
    	}
    	return getAccount;
    }

    @Override
    public AppResult<Account> getAccount(String username) {
    	AppResult<Account> getAccount = userDao.findByUsername(username);
        if(getAccount.getError() == null) {
    		Account account = getAccount.getEntity();
    		account.setPassword(null);
    		return new AppResult<>(account);
    	}
    	return getAccount;
    }

    @Override
    public AppResult<Account> getAccountByEmail(String emailAddress) {
    	AppResult<Account> getAccount = userDao.searchByEmail(emailAddress);
    	if(getAccount.getError() == null) {
    		Account account = getAccount.getEntity();
    		account.setPassword(null);
    		return new AppResult<>(account);
    	}
    	return getAccount;
    }

    @Override
    public AppResult<Integer> updatePassword(long accountId, char[] password) {
        return userDao.update(accountId, password);
    }
    
    @Override
    public AppResult<char[]> fetchPassword(long accountId){
    	AppResult<Account> getAccount = userDao.findAccount(accountId);
    	if(getAccount.getError() == null) {
    		return new AppResult<>(getAccount.getEntity().getPassword());
    	}
    	return new AppResult<>(getAccount.getStatus());
    }

    @Override
    public AppResult<char[]> resetPassword(long accountId) {
        String generatedPassword = RandomString.generate();
        char[] newPassword = PasswordUtil.hashPassword(generatedPassword.toCharArray());
        AppResult<Integer> resetResult = userDao.update(accountId, newPassword);
        if (resetResult.getEntity() > 0) {
            return new AppResult<>(newPassword);
        } else {
            return new AppResult<>(resetResult.getStatus());
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public AppResult<Account> updateAccount(Account account) {
        long accountId = account.getId();
        AppResult<Integer> updateStatus = userDao.update(accountId, account.getStatus());
        if (updateStatus.getError() == null) {
            AppResult<Integer> updateRole = userDao.update(accountId, account.getRole());
            if (updateRole.getError() == null) {
                AppResult<Account> doUpdate = userDao.findAccount(accountId);
                if (doUpdate.getError() == null) {
                	if(doUpdate.getError() == null) {
                		Account updated = doUpdate.getEntity();
                		updated.setPassword(null);
                		return new AppResult<>(updated);
                	}
                	return doUpdate;
                } else {
                    return new AppResult<>(doUpdate.getStatus());
                }
            } else {
                return new AppResult<>(updateRole.getStatus());
            }
        } else {
            return new AppResult<>(updateStatus.getStatus());
        }
    }

    @Override
    public AppResult<Profile> createProfile(Profile profile) {
    	AppResult<Profile> profileResult = userDao.findByEmail(profile.getEmailAddress());
    	if(profileResult.getEntity() == null) {
    		return userDao.register(profile);
    	} else {
            return new AppResult<>(400, "email is already in use");
        }
    }

    @Override
    public AppResult<Profile> getProfile(long profileId) {
        return userDao.findProfile(profileId);
    }

    @Override
    public AppResult<Profile> getProfile(String emailAddress) {
        return userDao.findByEmail(emailAddress);
    }

    @Override
    public AppResult<Profile> updateProfile(Profile profile) {
        return userDao.update(profile);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public AppResult<LoginStatus> accountLogin(String username, char[] password) {
        try {
            AppResult<Account> result = userDao.findByUsername(username);
            if (result.getError() == null) {
                Account account = result.getEntity();

                //check account status
                String accStatus = account.getStatus().toString();
                //check if account is deleted            
                if (AccStatus.deleted.toString().equals(accStatus)) {
                    String loginInfo = "This account is currently inactive. Request a recover link to reactivate the account";
                    return new AppResult<>(403, loginInfo);
                }

                if (AccStatus.disabled.toString().equals(accStatus)) {
                    String loginInfo = "This account is currently disabled. Request a restore link to reanable the account";
                    return new AppResult<>(403, loginInfo);
                }

                if (AccStatus.pending.toString().equals(accStatus)) {
                    String loginInfo = "This account is pending activation. Request an activation link if you don't have one already";
                    return new AppResult<>(403, loginInfo);
                }

                //fetch login status
                List<LoginStatus> currentStatus = userDao.fetchLoginStatus(account.getId()).getEntity();

                //check if locked
                if (AccStatus.locked.toString().equals(accStatus)) {
                    //check if lockout is not expired
                    LoginStatus lockStatus = currentStatus.stream().filter((LoginStatus t) -> t.getLockExpiry() != null).findFirst().get();
                    Calendar endLock = Calendar.getInstance();
                    endLock.add(Calendar.MINUTE, -serviceProperties.getLockoutDuration());
                    if (lockStatus.getLockExpiry().before(new Date())) {
                        String loginInfo = "This account is currently locked untill " + new SimpleDateFormat("hh:mm:ss A 'on' dd MMM yyyy").format(lockStatus.getLockExpiry());
                        return new AppResult<>(403, loginInfo);
                    } else {
                        AppResult<Integer> unlock = userDao.update(account.getId(), AccStatus.active);
                        if (unlock.getError() != null) {
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
                char[] dbpassword = fetchPassword(account.getId()).getEntity();
                if (PasswordUtil.verifyPassword(new String(password), dbpassword)) {
                    login.setStatusInfo("login ok");
                    login.setLoginSuccess(new Date());
                    login.setLoginToken(UserTokenGen.getInstance().encrypt(username));
                    login.setStatusCreated(new Date());
                    //clear login statuses
                    AppResult<Integer> cleared = userDao.clearLoginStatus(account.getId());
                    if (cleared.getError() != null) {
                        //TODO: Handle this scenario in a different way
                        System.err.println("Login status entries not successfully cleared");
                    }
                    //save login info
                    AppResult<Integer> added = userDao.addLoginStatus(login);
                    if (added.getError() != null) {
                        //TODO: Handle this scenario in a different way
                        System.err.println("Login status NOT saved properly - " + added.getStatus());
                    }
                    return new AppResult<>(login);
                } else {
                    //add login status and increase count for login attempt
                    String reason = "password did not match";
                    login.setStatusInfo(reason);
                    login.setLoginAttempts(loginAttempts + 1);
                    login.setStatusCreated(new Date());
                    AppResult<Integer> added = userDao.addLoginStatus(login);
                    if (added.getError() != null) {
                        //TODO: Handle this scenario in a different way
                        System.err.println("Login status NOT saved properly - " + added.getStatus());
                    }

                    //check login attempts
                    if (login.getLoginAttempts() > serviceProperties.getMaxAttempts()) {
                        //lock the account
                        AppResult<Integer> locked = userDao.update(account.getId(), AccStatus.locked);
                        if (locked.getError() != null) {
                            //TODO: Handle this scenario in a differnt way
                            System.err.println("Account status NOT updated properly - " + added.getStatus());
                        }
                    }
                    return new AppResult<>(403, reason);
                }
            } else {
                return new AppResult<>(result.getStatus());
            }
        } catch (NumberFormatException e) {
            return new AppResult<>(new ResStatus(1, e.getMessage()));
        }
    }

    @Override
    public AppResult<Integer> verifyAccount(String token) {
        String[] decrypted = UserTokenGen.getInstance().decrypt(token);
        long time = Long.valueOf(decrypted[1]);
        //find account to activate
        AppResult<Account> findAccount = userDao.searchByEmail(decrypted[0]);
        if (findAccount.getError() == null) {
            Account account = findAccount.getEntity();
            Date createdTs = account.getCreatedTs();
            long days = Duration.between(createdTs.toInstant(), new Date(time).toInstant()).toDays();
            if (days > 0) {
                return new AppResult<>(403, "The token does not appear to be valid");
            }
            AppResult<Integer> updated = userDao.update(account.getId(), AccStatus.active);
            return (updated.getEntity() == 1) ? updated : new AppResult<>(updated.getStatus());
        } else {
            return new AppResult<>(findAccount.getStatus());
        }
    }

    @Override
    public AppResult<Integer> toggleActivate(long accountId) {
        AppResult<Account> findAccount = userDao.findAccount(accountId);
        if (findAccount.getError() == null) {
            AccStatus status = findAccount.getEntity().getStatus();
            switch (status) {
                case active: {
                    return userDao.update(accountId, AccStatus.deleted);
                }
                case deleted: {
                    return userDao.update(accountId, AccStatus.active);
                }
                default: {
                    return new AppResult<>(403, "Cannot toggle status for this account");
                }
            }
        } else {
            return new AppResult<>(404, "Cannot locate an account with this id");
        }
    }

    @Override
    public AppResult<Integer> recoverAccount(String email, String username, char[] password) {
        if (email != null) {
            AppResult<Account> findAccount = userDao.searchByEmail(email);
            if (findAccount.getError() == null) {
                return userDao.update(findAccount.getEntity().getId(), PasswordUtil.hashPassword(password));
            } else {
                return new AppResult<>(404, "Cannot locate an account with email address");
            }
        } else if (username != null) {
            AppResult<Account> findAccount = userDao.findByUsername(username);
            if (findAccount.getError() == null) {
                return userDao.update(findAccount.getEntity().getId(), PasswordUtil.hashPassword(password));
            } else {
                return new AppResult<>(404, "Cannot locate an account with username");
            }
        }
        return new AppResult<>(400, "neither email nor username is provided");
    }

    @Override
    public AppResult<Integer> accountLogout(long accountId) {
        return userDao.clearLoginStatus(accountId);
    }
}
