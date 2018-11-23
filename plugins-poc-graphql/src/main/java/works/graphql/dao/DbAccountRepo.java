package works.graphql.dao;

import java.util.List;

import javax.persistence.TypedQuery;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import works.graphql.app.Result;
import works.graphql.entity.Account;

@Repository
public class DbAccountRepo implements AccountRepo {

	@Autowired
	private SessionFactory sessionFactory;

	@Override
	public Result<Account> findById(Long id) {
		try {
			Session session = this.sessionFactory.openSession();
			Account account = session.get(Account.class, id);
			session.close();
			return Result.of(account);
		} catch (Exception e) {
			return new Result<>(null, e.getMessage());
		}
	}

	@Override
	public Result<Account> findByUsername(String username) {
		try {
			Session session = this.sessionFactory.openSession();
			TypedQuery<Account> search = session.createQuery(
					"SELECT a FROM Account a JOIN a.profile p WHERE a.username = :username", Account.class);
			search.setParameter("username", username);
			Account result = search.getSingleResult();
			session.close();
			return Result.of(result);
		} catch (Exception e) {
			return new Result<>(null, e.getMessage());
		}
	}

	@Override
	public Result<List<Account>> fetchAccounts() {
		try {
			Session session = this.sessionFactory.openSession();
			List<Account> accounts = session.createQuery("from Account", Account.class).list();
			session.close();
			return Result.of(accounts);
		} catch (Exception e) {
			return new Result<>(null, e.getMessage());
		}
	}

	@Override
	public Result<Integer> createAccount(Account account) {
		try {
			Session session = this.sessionFactory.openSession();
			Transaction tx = session.beginTransaction();
			session.persist(account);
			tx.commit();
			session.close();
			return Result.of(1);
		} catch (Exception e) {
			return new Result<>(null, e.getMessage());
		}
	}

	@Override
	public Result<Integer> updateAccount(Account account) {
		try {
			Session session = this.sessionFactory.openSession();
			Transaction tx = session.beginTransaction();
			session.saveOrUpdate(account);
			tx.commit();
			session.close();
			return Result.of(1);
		} catch (Exception e) {
			return new Result<>(null, e.getMessage());
		}
	}
}
