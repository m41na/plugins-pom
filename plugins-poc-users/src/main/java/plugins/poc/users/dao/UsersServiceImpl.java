package plugins.poc.users.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import works.hop.plugins.api.PlugResult;

@Repository
public class UsersServiceImpl implements UsersService {

	private final NamedParameterJdbcTemplate template;

	@Autowired
	public UsersServiceImpl(DataSource ds) {
		template = new NamedParameterJdbcTemplate(ds);
	}

	@Override
	public PlugResult<User> create(User user) {
		Map<String, Object> params = new HashMap<>();
		params.put("first_name", user.getFirstName());
		params.put("last_name", user.getLastName());
		params.put("email_addr", user.getEmailAddress());
		params.put("phone_num", user.getPhoneNumber());

		String sql = "insert into tbl_users (first_name, last_name, email_addr, phone_num, user_created_ts) values (:first_name, :last_name, :email_addr, :phone_num, datetime('now'))";

		KeyHolder holder = new GeneratedKeyHolder();
		int res = template.update(sql, new MapSqlParameterSource(params), holder);
		user.setId(holder.getKey().longValue());

		return (res > 0) ? new PlugResult<>(user) : new PlugResult<>(false, "failed to create new user");
	}

	@Override
	public PlugResult<User> find(Long id) {
		Map<String, Object> params = new HashMap<>();
		params.put("user_id", id);

		String sql = "SELECT * FROM tbl_users where user_id=:user_id;";

		try {
			User user = template.queryForObject(sql, params, userMapper());
			return user != null ? new PlugResult<>(user) : new PlugResult<>(false, "could not find user by id");
		} catch (DataAccessException e) {
			return new PlugResult<>(false, e.getMessage());
		}
	}

	@Override
	public PlugResult<List<User>> findUsers(int start, int size) {
		Map<String, Object> params = new HashMap<>();
		params.put("limit", size);
		params.put("offset", start);

		String sql = "SELECT * FROM tbl_users limit :limit offset :offset;";

		try {
			List<User> users = template.query(sql, params, userMapper());
			return users != null ? new PlugResult<>(users) : new PlugResult<>(false, "could not find users");
		} catch (DataAccessException e) {
			return new PlugResult<>(false, e.getMessage());
		}
	}

	private RowMapper<User> userMapper() {
		return (rs, rowNum) -> {
			User user = new User();
			user.setId(rs.getLong("user_id"));
			user.setFirstName(rs.getString("first_name"));
			user.setLastName(rs.getString("last_name"));
			user.setEmailAddress(rs.getString("email_addr"));
			user.setPhoneNumber(rs.getString("phone_num"));
			user.setCreatedTs(SqliteDate.fromString(rs.getString("user_created_ts")));
			return user;
		};
	}
}
