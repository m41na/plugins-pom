package works.graphql.dao;

import java.util.List;

import works.graphql.app.Result;
import works.graphql.entity.Account;

public interface AccountRepo {

	Result<Account> findById(Long id);
	
	Result<Account> findByUsername(String username);
	
	Result<List<Account>> fetchAccounts();
	
	Result<Integer> createAccount(Account account);
	
	Result<Integer> updateAccount(Account account);
}
