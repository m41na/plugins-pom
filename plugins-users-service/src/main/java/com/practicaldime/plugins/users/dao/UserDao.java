package com.practicaldime.plugins.users.dao;

import com.practicaldime.common.entity.users.*;
import com.practicaldime.common.util.AResult;
import com.practicaldime.common.util.DaoStatus;

import java.util.List;

public interface UserDao extends DaoStatus {

    AResult<List<Account>> retrieveAccounts(int start, int size);

    AResult<Account> findAccount(long id);

    AResult<Account> findByUsername(String username);

    AResult<Account> searchByEmail(String emailAddress);

    AResult<Account> register(Account account);

    AResult<Integer> update(long accountId, char[] password);

    AResult<Integer> update(long accountId, AccStatus status);

    AResult<Integer> update(long accountId, AccRole role);

    AResult<Integer> deleteAccount(Long id);

    AResult<Profile> findProfile(long id);

    AResult<Profile> findByEmail(String email);

    AResult<Profile> register(Profile profile);

    AResult<Profile> update(Profile profile);

    AResult<Integer> deleteProfile(Long id);

    AResult<List<LoginStatus>> fetchLoginStatus(long accountId);

    AResult<Integer> addLoginStatus(LoginStatus status);

    AResult<Integer> clearLoginStatus(long accountId);
}
