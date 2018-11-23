package com.jarredweb.plugins.users.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ServiceProperties {

    public static final String MAX_LOGIN_ATTEMPTS = "${app.login.max.attempts}";
    public static final String LOCKOUT_DURATION = "${app.login.lock.duration}";
    
    private Integer maxAttempts;
    private Integer lockoutDuration;

    public Integer getMaxAttempts() {
        return maxAttempts;
    }

    public void setMaxAttempts(@Value(MAX_LOGIN_ATTEMPTS) Integer maxAttempts) {
        this.maxAttempts = maxAttempts;
    }

    public Integer getLockoutDuration() {
        return lockoutDuration;
    }

    public void setLockoutDuration(@Value(LOCKOUT_DURATION) Integer lockoutDuration) {
        this.lockoutDuration = lockoutDuration;
    }
}
