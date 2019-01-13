package com.practicaldime.plugins.blogs.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.practicaldime.common.util.PasswordCheck;
import com.practicaldime.common.util.PasswordStrength;
import com.practicaldime.plugins.blogs.dao.BlogsDao;
import com.practicaldime.plugins.blogs.service.BlogsService;
import com.practicaldime.plugins.blogs.service.BlogsServiceImpl;

@Configuration
@EnableTransactionManagement
@Import(BlogsDaoTestConfig.class)
@TestPropertySource(locations = "classpath:test-service-config.properties")
public class BlogsServiceTestConfig {

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertiesResolver() {
        return new PropertySourcesPlaceholderConfigurer();
    }
    
    @Bean
    public PasswordCheck passwordCheck() {
    	return new PasswordStrength();
    }

    @Bean
    public BlogsService getBlogService(@Autowired BlogsDao blogsDao) {
        BlogsServiceImpl service = new BlogsServiceImpl();
        service.setBlogsDao(blogsDao);
        return service;
    }
}
