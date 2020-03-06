package com.practicaldime.graphql.service;

import com.practicaldime.graphql.entity.Account;
import com.practicaldime.graphql.entity.Address;

import java.util.List;

public interface AccountService {

    Account findById(Long id);

    Account findByUsername(String username);

    List<Account> fetchAccounts();

    Integer createAccount(String username, String password, String emailAddr);

    Integer updateProfile(Long id, String firstName, String lastName, String aboutMe, String birthDay);

    Integer updateAddress(Long id, Address address);
}
