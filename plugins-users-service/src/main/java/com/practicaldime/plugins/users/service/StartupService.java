package com.practicaldime.plugins.users.service;

import com.practicaldime.domain.users.Account;

public interface StartupService {
    
    void initialize();

    Account getUserAccount(String username);

    void onInitialized();
}
