package com.jarredweb.plugins.users.service;

import com.jarredweb.common.util.AppResult;
import com.jarredweb.domain.users.Account;
import com.jarredweb.domain.users.LoginStatus;
import com.jarredweb.domain.users.Profile;

public interface UserService {

    //Account functionality
    AppResult<String> createAccount(Account account);

    AppResult<Account> getAccount(long accountId);

    AppResult<Account> getAccount(String username);

    AppResult<Account> getAccountByEmail(String email);
    
    AppResult<char[]> fetchPassword(long accountId);

    AppResult<char[]> resetPassword(long accountId);
    
    AppResult<Integer> updatePassword(long accountId, char[] password);
    
    AppResult<Account> updateAccount(Account account);

    //profile functionality
    AppResult<Profile> createProfile(Profile profile);

    AppResult<Profile> getProfile(long profileId);

    AppResult<Profile> getProfile(String emailAddress);

    AppResult<Profile> updateProfile(Profile profile);
    
    //login functionality
    AppResult<LoginStatus> accountLogin(String username, char[] password);

    AppResult<Integer> verifyAccount(String token);
    
    AppResult<Integer> toggleActivate(long accountId);
    
    AppResult<Integer> recoverAccount(String email, String username, char[] password);
    
    AppResult<Integer> accountLogout(long accountId);
}
