package works.hop.plugins.users.dao;

import java.util.List;

import works.hop.plugins.api.PlugResult;

public interface UsersService {

	PlugResult<User> create(User user);

	PlugResult<User> find(Long id);

	PlugResult<List<User>> findUsers(int start, int size);
}