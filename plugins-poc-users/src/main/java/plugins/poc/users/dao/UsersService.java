package plugins.poc.users.dao;

import com.practicaldime.plugins.api.PlugResult;

import java.util.List;

public interface UsersService {

    PlugResult<User> create(User user);

    PlugResult<User> find(Long id);

    PlugResult<List<User>> findUsers(int start, int size);
}