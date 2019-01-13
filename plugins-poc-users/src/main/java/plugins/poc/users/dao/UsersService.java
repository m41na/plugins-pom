package plugins.poc.users.dao;

import java.util.List;

import com.practicaldime.plugins.api.PlugResult;

public interface UsersService {

	PlugResult<User> create(User user);

	PlugResult<User> find(Long id);

	PlugResult<List<User>> findUsers(int start, int size);
}