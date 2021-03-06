package com.practicaldime.plugins.users.service;

import com.practicaldime.common.entity.users.AccRole;
import com.practicaldime.common.entity.users.AccStatus;
import com.practicaldime.common.entity.users.Account;
import com.practicaldime.common.entity.users.Profile;
import com.practicaldime.common.util.AResult;
import com.practicaldime.common.util.AppException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

@Service("StartupService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class StartupServiceImpl implements StartupService {

    private static final Logger LOG = LoggerFactory.getLogger(StartupServiceImpl.class);

    @Autowired
    private UserService userService;
    @Autowired
    private DataSource dataSource;

    private Properties props = null;

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void initialize() {
        // 1. populate props with initial values
        props = new Properties();

        // 2. read values from config files
        String defaultConfigFile = "/config/app-default.properties";
        try(InputStream stream = this.getClass().getResourceAsStream(defaultConfigFile)) {
            if(stream != null) {
                props.load(stream);
                // load overriding properties
                props.load(new FileReader(new File("app-config.properties")));
            }
            else{
                LOG.warn("Could not locate the file specified at [" + defaultConfigFile + "]");
            }
        } catch (IOException | NullPointerException e) {
            LOG.error(e.getMessage());
            throw new AppException(1, "could not locate properties file to initialize application", e);
        }

        // 3. check initialized property
        if ("true".equals(props.getProperty("app.jdbc.configure")) && !"true".equals(props.getProperty("app.initialized"))) {

            // 4. initialize database schema
            ResourceDatabasePopulator schema = new ResourceDatabasePopulator();
            schema.addScript(new ClassPathResource("sql/update-schema.sql"));
            schema.execute(dataSource);

            // 5. check if default admin user exists
            Account account = getUserAccount(props.getProperty("app.admin.username"));

            if (account == null) {
                // 6. register default admin user
                Profile user = new Profile();
                user.setEmailAddress(props.getProperty("app.admin.email"));
                user.setFirstName(props.getProperty("app.admin.firstname"));
                user.setLastName(props.getProperty("app.admin.lastname"));

                AResult<Profile> createResult = userService.createProfile(user);

                user = createResult.data;

                account = new Account();
                account.setProfile(user);
                account.setUsername(props.getProperty("app.admin.username"));

                System.out.print("Enter your admin password: ");

                Console console = System.console();
                if (console != null) {
                    char[] password = console.readPassword();
                    account.setPassword(password);
                } else {
                    try (Scanner in = new Scanner(System.in);) {
                        String password = in.nextLine();
                        account.setPassword(password.toCharArray());
                    }
                }
                AResult<String> created = userService.createAccount(account);

                if (created.data != null) {
                    // 7. update user info
                    account.setRole(AccRole.admin);
                    account.setStatus(AccStatus.active);
                    AResult<Account> updated = userService.updateAccount(account);
                    if (updated.data == null) {
                        throw new Error("Could not create default admin");
                    }

                    LOG.info("new '{}' account registered", account.getUsername());
                }

                // 8. populate with initial data
                ResourceDatabasePopulator dbdata = new ResourceDatabasePopulator();
                dbdata.addScript(new ClassPathResource("sql/insert-data.sql"));
                dbdata.execute(dataSource);
            }

            //9. all clear - update initialized flag
            onInitialized();
        }
    }

    @Override
    public Account getUserAccount(String username) {
        return userService.getAccount(username).data;
    }

    @Override
    public void onInitialized() {
        try {
            Path configFilePath = Paths.get("app-config.properties");
            List<String> fileContent = new ArrayList<>(Files.readAllLines(configFilePath, StandardCharsets.UTF_8));

            for (int i = 0; i < fileContent.size(); i++) {
                if (fileContent.get(i).startsWith("app.initialized")) {
                    fileContent.set(i, "app.initialized=true");
                    break;
                }
            }

            Files.write(configFilePath, fileContent, StandardCharsets.UTF_8);
            LOG.info("app-config.properties: 'initialized' property updated");
        } catch (IOException e) {
            throw new Error(
                    "Application was initialized but could not update the initialized property. Consider doing this manually and then restart");
        }
    }
}
