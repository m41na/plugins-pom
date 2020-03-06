package com.practicaldime.plugins.users.config;

import com.practicaldime.common.util.PasswordCheck;
import com.practicaldime.common.util.PasswordStrength;
import com.practicaldime.plugins.users.dao.UserDao;
import com.practicaldime.plugins.users.service.StartupService;
import com.practicaldime.plugins.users.service.StartupServiceImpl;
import com.practicaldime.plugins.users.service.UserService;
import com.practicaldime.plugins.users.service.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement
@Import(UsersDaoTestConfig.class)
@TestPropertySource(locations = "classpath:test-service-config.properties")
public class UsersServiceTestConfig {

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertiesResolver() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean
    public PasswordCheck passwordCheck() {
        return new PasswordStrength();
    }

    @Bean
    public ServiceProperties serviceProperties() {
        ServiceProperties props = new ServiceProperties();
        props.setMaxAttempts(3);
        props.setLockoutDuration(120);
        return props;
    }

    @Bean
    public UserService getUserService(@Autowired UserDao dao) {
        UserServiceImpl service = new UserServiceImpl();
        service.setUserDao(dao);
        service.setServiceProperties(serviceProperties());
        return service;
    }

    @Bean
    public StartupService getStartupService(@Autowired UserService userService, @Autowired DataSource dataSource) {
        StartupServiceImpl service = new StartupServiceImpl();
        service.setUserService(userService);
        service.setDataSource(dataSource);
        return service;
    }
}
