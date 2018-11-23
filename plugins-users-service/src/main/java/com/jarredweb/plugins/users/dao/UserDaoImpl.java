package com.jarredweb.plugins.users.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.jarredweb.common.util.AppResult;
import com.jarredweb.common.util.SqliteDate;
import com.jarredweb.domain.users.AccRole;
import com.jarredweb.domain.users.AccStatus;
import com.jarredweb.domain.users.Account;
import com.jarredweb.domain.users.LoginStatus;
import com.jarredweb.domain.users.Profile;

@Repository
public class UserDaoImpl implements UserDao {

    private final NamedParameterJdbcTemplate template;

    @Autowired
    public UserDaoImpl(DataSource ds) {
        template = new NamedParameterJdbcTemplate(ds);
    }
    
    @Override
    public AppResult<List<Account>> retrieveAccounts(int start, int size) {
        Map<String, Object> params = new HashMap<>();
        params.put("start", start);
        params.put("size", size);
        String sql = "SELECT * FROM tbl_account a inner join tbl_profile p on a.account_profile = p.profile_id limit :size offset :start order by a.username";
        List<Account> list = template.query(sql, accountMapper());
        return list != null? new AppResult<>(list) : new AppResult<>(404, "could not find accounts");
    }

    @Override
    public AppResult<Account> findAccount(long id) {
        Map<String, Object> params = new HashMap<>();
        params.put("id", id);

        String sql = "SELECT * FROM tbl_account a inner join tbl_profile p on a.account_profile = p.profile_id WHERE a.account_id=:id";

        Account account = template.queryForObject(sql, params, accountMapper());
        return account != null ? new AppResult<>(account) : new AppResult<>(404, "could not find account");
    }

    @Override
    public AppResult<Account> findByUsername(String username) {
        Map<String, Object> params = new HashMap<>();
        params.put("username", username);

        String sql = "SELECT * FROM tbl_account a inner join tbl_profile p on a.account_profile = p.profile_id WHERE a.username=:username";

        List<Account> list = template.query(sql, params, accountMapper());

        Account result = null;
        if (list != null && !list.isEmpty()) {
            result = list.get(0);
        }

        return result != null ? new AppResult<>(result) : new AppResult<>(404, "could not find account");
    }

    @Override
    public AppResult<Account> searchByEmail(String emailAddress) {
        Map<String, Object> params = new HashMap<>();
        params.put("email_addr", emailAddress);

        String sql = "SELECT * FROM tbl_account a  inner join tbl_profile p on a.account_profile = p.profile_id WHERE p.email_addr=:email_addr";

        List<Account> list = template.query(sql, params, accountMapper());

        Account result = null;
        if (list != null && !list.isEmpty()) {
            result = list.get(0);
        }

        return result != null ? new AppResult<>(result) : new AppResult<>(404, "could not find account");    }

    @Override
    public AppResult<Account> register(Account acc) {
        Map<String, Object> params = new HashMap<>();
        params.put("username", acc.getUsername());
        params.put("password", new String(acc.getPassword()));
        params.put("user", acc.getProfile().getId());

        String sql = "insert into tbl_account (username, password, account_profile, account_created_ts) values (:username, :password, :user, datetime('now'))";

        KeyHolder holder = new GeneratedKeyHolder();
        int res = template.update(sql, new MapSqlParameterSource(params), holder);
        acc.setId(holder.getKey().longValue());

        return (res > 0) ? new AppResult<>(acc) : new AppResult<>(400, "failed registration");
    }

    @Override
    public AppResult<Integer> update(long accountId, char[] password) {
        Map<String, Object> params = new HashMap<>();
        params.put("id", accountId);
        params.put("password", new String(password));

        String sql = "update tbl_account set password=:password where account_id=:id";

        int res = template.update(sql, params);
        return (res > 0) ? new AppResult<>(res) : new AppResult<>(400, "failed reseting account password");
    }

    @Override
    public AppResult<Integer> update(long accountId, AccStatus status) {
        Map<String, Object> params = new HashMap<>();
        params.put("id", accountId);
        params.put("acc_status", status.toString());

        String sql = "update tbl_account set acc_status=:acc_status where account_id=:id";

        int res = template.update(sql, params);
        return (res > 0) ? new AppResult<>(res) : new AppResult<>(400, "failed updating account status");
    }

    @Override
    public AppResult<Integer> update(long accountId, AccRole role) {
        Map<String, Object> params = new HashMap<>();
        params.put("id", accountId);
        params.put("acc_role", role.toString());

        String sql = "update tbl_account set acc_role=:acc_role where account_id=:id";

        int res = template.update(sql, params);
        return (res > 0) ? new AppResult<>(res) : new AppResult<>(400, "failed updating account role");
    }

    @Override
    public AppResult<Integer> deleteAccount(Long id) {
        Map<String, Object> params = new HashMap<>();
        params.put("id", id);

        String sql = "delete from tbl_account where account_id = :id";

        int res = template.update(sql, params);
        return (res > 0) ? new AppResult<>(res) : new AppResult<>(400, "failed to delete account");
    }

    @Override
    public AppResult<Profile> findProfile(long id) {
        Map<String, Object> params = new HashMap<>();
        params.put("id", id);

        String sql = "SELECT * FROM tbl_profile WHERE profile_id=:id";

        Profile profile = template.queryForObject(sql, params, profileMapper());
        return profile != null ? new AppResult<>(profile) : new AppResult<>(404, "could not find profile");
    }

    @Override
    public AppResult<Profile> findByEmail(String email) {
        Map<String, Object> params = new HashMap<>();
        params.put("email", email);

        String sql = "SELECT * FROM tbl_profile WHERE email_addr=:email";

        List<Profile> list = template.query(sql, params, profileMapper());

        Profile result = null;
        if (list != null && !list.isEmpty()) {
            result = list.get(0);
        }

        return result != null ? new AppResult<>(result) : new AppResult<>(404, "could not find profile");
    }

    @Override
    public AppResult<Profile> register(Profile profile) {
        Map<String, Object> params = new HashMap<>();
        params.put("firstName", profile.getFirstName());
        params.put("lastName", profile.getLastName());
        params.put("emailAddr", profile.getEmailAddress());
        params.put("phoneNum", profile.getPhoneNumber());

        String sql = "insert into tbl_profile (first_name, last_name, email_addr, phone_num, profile_created_ts) values (:firstName, :lastName, :emailAddr, :phoneNum, datetime('now'))";
        
        KeyHolder holder = new GeneratedKeyHolder();
        int res = template.update(sql, new MapSqlParameterSource(params), holder);
        profile.setId(holder.getKey().longValue());

        return (res > 0) ? new AppResult<>(profile) : new AppResult<>(400, "failed new category");
    }

    @Override
    public AppResult<Profile> update(Profile profile) {
        Map<String, Object> params = new HashMap<>();
        params.put("id", profile.getId());
        params.put("firstName", profile.getFirstName());
        params.put("lastName", profile.getLastName());
        params.put("phoneNum", profile.getPhoneNumber());

        String sql = "update tbl_profile set first_name=:firstName, last_name=:lastName, phone_num=:phoneNum where profile_id=:id";

        int res = template.update(sql, params);
        return (res > 0) ? new AppResult<>(profile) : new AppResult<>(400, "failed to update profile");
    }

    @Override
    public AppResult<Integer> deleteProfile(Long id) {
        Map<String, Object> params = new HashMap<>();
        params.put("id", id);

        String sql = "delete from tbl_profile where profile_id = :id";

        int res = template.update(sql, params);
        return (res > 0) ? new AppResult<>(res) : new AppResult<>(400, "failed to delete profile");
    }

    @Override
    public AppResult<List<LoginStatus>> fetchLoginStatus(long accountId) {
        Map<String, Object> params = new HashMap<>();
        params.put("fk_account_id", accountId);

        String sql = "SELECT * FROM tbl_login_status WHERE fk_account_id=:fk_account_id order by status_created_ts";

        List<LoginStatus> list = template.query(sql, params, loginStatusMapper());
        return new AppResult<>(list);
    }

    @Override
    public AppResult<Integer> addLoginStatus(LoginStatus status) {
        Map<String, Object> params = new HashMap<>();
        params.put("fk_account_id", status.getAccountId());
        params.put("acc_login_token", status.getLoginToken());
        params.put("lock_expiry_ts", SqliteDate.toString(status.getLockExpiry()));
        params.put("acc_status_info", status.getStatusInfo());
        params.put("login_attempts", status.getLoginAttempts());
        params.put("login_success_ts", SqliteDate.toString(status.getLoginSuccess()));
        params.put("status_created_ts", SqliteDate.toString(status.getStatusCreated()));

        String sql = "insert into tbl_login_status (fk_account_id, acc_login_token, login_attempts, acc_status_info, status_created_ts, lock_expiry_ts, login_success_ts) values " +
                "(:fk_account_id, :acc_login_token, :login_attempts, :acc_status_info, :status_created_ts, :lock_expiry_ts, :login_success_ts)";

        int res = template.update(sql, params);
        return (res > 0) ? new AppResult<>(res) : new AppResult<>(400, "failed inserting login status entry");
    }

    @Override
    public AppResult<Integer> clearLoginStatus(long accountId) {
        Map<String, Object> params = new HashMap<>();
        params.put("fk_account_id", accountId);

        String sql = "delete from tbl_login_status where fk_account_id = :fk_account_id";

        int res = template.update(sql, params);
        return new AppResult<>(res);
    }

    private RowMapper<Account> accountMapper() {
        return (rs, rowNum) -> {
            Account acc = new Account();
            
            acc.setId(rs.getLong("account_id"));
            acc.setUsername(rs.getString("username"));
            acc.setPassword(rs.getString("password").toCharArray());
            acc.setStatus(AccStatus.valueOf(rs.getString("acc_status")));
            acc.setRole(AccRole.valueOf(rs.getString("acc_role")));
            acc.setCreatedTs(SqliteDate.fromString(rs.getString("account_created_ts")));
            Profile user = profileMapper().mapRow(rs, rowNum);
            acc.setProfile(user);
            
            return acc;
        };
    }
    
    private RowMapper<LoginStatus> loginStatusMapper() {
        return (rs, rowNum) -> {
            LoginStatus login = new LoginStatus();
            login.setAccountId(rs.getLong("fk_account_id"));
            login.setStatusInfo(rs.getString("acc_status_info"));
            login.setLoginToken(rs.getString("acc_login_token"));
            login.setLockExpiry(SqliteDate.fromString(rs.getString("lock_expiry_ts")));
            login.setLoginAttempts(rs.getInt("login_attempts"));
            login.setLoginSuccess(SqliteDate.fromString(rs.getString("login_success_ts")));
            login.setStatusCreated(SqliteDate.fromString(rs.getString("status_created_ts")));
            return login;
        };
    }

    private RowMapper<Profile> profileMapper() {
        return (rs, rowNum) -> {
            Profile prof = new Profile();

            prof.setId(rs.getLong("profile_id"));
            prof.setFirstName(rs.getString("first_name"));
            prof.setLastName(rs.getString("last_name"));
            prof.setEmailAddress(rs.getString("email_addr"));
            prof.setPhoneNumber(rs.getString("phone_num"));
            prof.setCreatedTs(SqliteDate.fromString(rs.getString("profile_created_ts")));

            return prof;
        };
    }
}
