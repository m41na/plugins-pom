package works.graphql.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import works.graphql.app.Result;
import works.graphql.dao.AccountRepo;
import works.graphql.entity.Account;
import works.graphql.entity.Address;
import works.graphql.entity.Profile;

@Service
@Transactional
public class AccountServiceImpl implements AccountService{

	@Autowired
	private AccountRepo dao;
	
	@Override
	public Account findById(Long id) {
		return dao.findById(id).data;
	}

	@Override
	public Account findByUsername(String username) {
		return dao.findByUsername(username).data;
	}

	@Override
	public List<Account> fetchAccounts() {
		return dao.fetchAccounts().data;
	}

	@Override
	public Integer createAccount(String username, String password, String emailAddr) {
		Account acc = new Account();
		acc.username = username;
		acc.password = password;
		acc.emailAddr = emailAddr;
		return dao.createAccount(acc).data;
	}

	@Override
	@Transactional(value=TxType.REQUIRED)
	public Integer updateProfile(Long id, String firstName, String lastName, String aboutMe, String birthDay) {
		Result<Account> find = dao.findById(id);
		if(find.data != null) {
			Account account = find.data;
			Profile profile = account.profile;
			if(profile == null) {
				profile = new Profile();
				account.profile = profile;
			}
			profile.firstName = firstName;
			profile.lastName = lastName;
			profile.aboutMe = aboutMe;
			try {
				profile.birthDay = new SimpleDateFormat("yyyy-mm-dd").parse(birthDay);
			} catch (ParseException e) {
				//"The birthDay Date is not parsable, so just ignore -> "
				e.printStackTrace(System.err);
			}
			dao.updateAccount(account);
			return 1;
		}
		return 0;
	}
	
	@Override
	@Transactional(value=TxType.REQUIRED)
	public Integer updateAddress(Long id, Address address) {
		Result<Account> find = dao.findById(id);
		if(find.error != null) {
			Profile profile = find.data.profile;
			profile.address = address;
			dao.updateAccount(find.data);
			return 1;
		}
		return 0;
	}	
}
