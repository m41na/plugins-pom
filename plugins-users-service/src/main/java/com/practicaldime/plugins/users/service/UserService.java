package com.practicaldime.plugins.users.service;

import com.practicaldime.common.entity.users.Account;
import com.practicaldime.common.entity.users.LoginStatus;
import com.practicaldime.common.entity.users.Profile;
import com.practicaldime.common.util.AResult;

public interface UserService {

    //Account functionality
    AResult<String> createAccount(Account account);

    AResult<Account> getAccount(long accountId);

    AResult<Account> getAccount(String username);

    AResult<Account> getAccountByEmail(String email);
    
    AResult<char[]> fetchPassword(long accountId);

    AResult<char[]> resetPassword(long accountId);
    
    AResult<Integer> updatePassword(long accountId, char[] password);
    
    AResult<Account> updateAccount(Account account);

    //profile functionality
    AResult<Profile> createProfile(Profile profile);

    AResult<Profile> getProfile(long profileId);

    AResult<Profile> getProfile(String emailAddress);

    AResult<Profile> updateProfile(Profile profile);
    
    //login functionality
    AResult<LoginStatus> accountLogin(String username, char[] password);

    AResult<Integer> verifyAccount(String token);
    
    AResult<Integer> toggleActivate(long accountId);
    
    AResult<Integer> recoverAccount(String email, String username, char[] password);
    
    AResult<Integer> accountLogout(long accountId);
}
