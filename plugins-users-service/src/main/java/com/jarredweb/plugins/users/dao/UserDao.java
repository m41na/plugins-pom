package com.jarredweb.plugins.users.dao;

import java.util.List;

import com.jarredweb.common.util.AppResult;
import com.jarredweb.common.util.DaoStatus;
import com.jarredweb.domain.users.AccRole;
import com.jarredweb.domain.users.AccStatus;
import com.jarredweb.domain.users.Account;
import com.jarredweb.domain.users.LoginStatus;
import com.jarredweb.domain.users.Profile;

public interface UserDao extends DaoStatus{

    AppResult<List<Account>> retrieveAccounts(int start, int size);

    AppResult<Account> findAccount(long id);

    AppResult<Account> findByUsername(String username);

    AppResult<Account> searchByEmail(String emailAddress);

    AppResult<Account> register(Account account);

    AppResult<Integer> update(long accountId, char[] password);
    
    AppResult<Integer> update(long accountId, AccStatus status);
    
    AppResult<Integer> update(long accountId, AccRole role);

    AppResult<Integer> deleteAccount(Long id);

    AppResult<Profile> findProfile(long id);

    AppResult<Profile> findByEmail(String email);

    AppResult<Profile> register(Profile profile);

    AppResult<Profile> update(Profile profile);

    AppResult<Integer> deleteProfile(Long id);

    AppResult<List<LoginStatus>> fetchLoginStatus(long accountId);

    AppResult<Integer> addLoginStatus(LoginStatus status);
    
    AppResult<Integer> clearLoginStatus(long accountId);
}
