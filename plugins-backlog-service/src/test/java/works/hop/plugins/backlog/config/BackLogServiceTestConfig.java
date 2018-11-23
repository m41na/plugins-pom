package works.hop.plugins.backlog.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.jarredweb.common.util.PasswordCheck;
import com.jarredweb.common.util.PasswordStrength;
import com.jarredweb.plugins.backlog.dao.BackLogDao;
import com.jarredweb.plugins.backlog.service.BackLogService;
import com.jarredweb.plugins.backlog.service.BackLogServiceImpl;

@Configuration
@EnableTransactionManagement
@Import(BackLogDaoTestConfig.class)
@TestPropertySource(locations = "classpath:test-service-config.properties")
public class BackLogServiceTestConfig {

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertiesResolver() {
        return new PropertySourcesPlaceholderConfigurer();
    }
    
    @Bean
    public PasswordCheck passwordCheck() {
    	return new PasswordStrength();
    }

    @Bean
    public BackLogService getTodoService(@Autowired BackLogDao backLogDao) {
    	BackLogServiceImpl service = new BackLogServiceImpl();
        service.setTodoDao(backLogDao);
        return service;
    }
}
