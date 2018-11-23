package com.jarredweb.plugins.users.service;

import com.jarredweb.domain.users.Account;

public interface StartupService {
    
    void initialize();

    Account getUserAccount(String username);

    void onInitialized();
}
