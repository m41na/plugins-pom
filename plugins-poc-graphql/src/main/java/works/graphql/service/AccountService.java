package works.graphql.service;

import java.util.List;

import works.graphql.entity.Account;
import works.graphql.entity.Address;

public interface AccountService {

	Account findById(Long id);

	Account findByUsername(String username);

	List<Account> fetchAccounts();

	Integer createAccount(String username, String password, String emailAddr);
	
	Integer updateProfile(Long id, String firstName, String lastName, String aboutMe);
	
	Integer updateAddress(Long id, Address address);
}
