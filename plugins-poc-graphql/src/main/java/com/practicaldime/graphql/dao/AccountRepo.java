package com.practicaldime.graphql.dao;

import com.practicaldime.graphql.app.Result;
import com.practicaldime.graphql.entity.Account;

import java.util.List;

public interface AccountRepo {

    Result<Account> findById(Long id);

    Result<Account> findByUsername(String username);

    Result<List<Account>> fetchAccounts();

    Result<Integer> createAccount(Account account);

    Result<Integer> updateAccount(Account account);
}
