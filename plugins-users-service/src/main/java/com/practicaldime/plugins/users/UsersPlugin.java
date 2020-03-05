package com.practicaldime.plugins.users;

import com.practicaldime.common.entity.users.Account;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.google.gson.Gson;
import com.practicaldime.common.util.AResult;;
import com.practicaldime.plugins.users.config.UsersDaoConfig;
import com.practicaldime.plugins.users.service.UserService;

import com.practicaldime.plugins.api.AbstractPlugin;
import com.practicaldime.plugins.api.PlugException;
import com.practicaldime.plugins.api.PlugResult;

public class UsersPlugin extends AbstractPlugin<UserService> {

	private AnnotationConfigApplicationContext context;

	public UsersPlugin() {
		super(UserService.class);
	}

	@Override
	public void load(ClassLoader loader) {
		context = new AnnotationConfigApplicationContext();
		context.setClassLoader(loader);
		context.register(UsersDaoConfig.class);
		context.refresh();
		System.out.println("plugin loaded");
		this.service = context.getBean(UserService.class);
	}

	@Override
	public PlugResult<?> execute(String feature, String payload) {
		System.out.println("plugin executing");
		Gson gson = new Gson();
		switch (feature) {

		case "createAccount": {
			Account account = gson.fromJson(payload, Account.class);
			if (account != null) {
				try {
					AResult<String> created = service.createAccount(account);
					if (created.data != null) {
						return new PlugResult<>(created.data);
					} else {
						return new PlugResult<>(false, created.errorString());
					}
				} catch (Exception e) {
					return new PlugResult<>(e.getMessage());
				}
			} else {
				throw new PlugException("Could not read user data from json payload");
			}
		}
		case "getAccount": {
			try {
				AResult<Account> found = null;
				if (payload.matches("^\\d+$")) {
					Long accountId = Long.valueOf("accountId");
					found = service.getAccount(accountId);
				} else {
					found = service.getAccount(payload);
				}
				if (found.data != null) {
					return new PlugResult<>(found.data);
				} else {
					return new PlugResult<>(false, found.errorString());
				}
			} catch (PlugException e) {
				return new PlugResult<>(e.getMessage());
			}
		}
		case "getAccountByEmail": {
			try {
				AResult<Account> found = service.getAccountByEmail(payload);
				if (found.data != null) {
					return new PlugResult<>(found.data);
				} else {
					return new PlugResult<>(false, found.errorString());
				}
			} catch (PlugException e) {
				return new PlugResult<>(e.getMessage());
			}
		}
		case "fetchPassword": {
			try {
				if (payload.matches("^\\d+$")) {
					Long accountId = Long.valueOf("accountId");
					AResult<char[]> found = service.fetchPassword(accountId);
					if (found.data != null) {
						return new PlugResult<>(found.data);
					} else {
						return new PlugResult<>(false, found.errorString());
					}
				} else {
					return new PlugResult<>(false, "expecting a numeric id value");
				}
			} catch (PlugException e) {
				return new PlugResult<>(e.getMessage());
			}
		}
		case "resetPassword": {
			try {
				if (payload.matches("^\\d+$")) {
					Long accountId = Long.valueOf("accountId");
					AResult<char[]> found = service.resetPassword(accountId);
					if (found.data != null) {
						return new PlugResult<>(found.data);
					} else {
						return new PlugResult<>(false, found.errorString());
					}
				} else {
					return new PlugResult<>(false, "expecting a numeric id value");
				}
			} catch (PlugException e) {
				return new PlugResult<>(e.getMessage());
			}
		}
		case "updatePassword": {
			try {
				if (payload.matches("^\\d+$")) {
					Long accountId = Long.valueOf("accountId");
					AResult<char[]> found = service.resetPassword(accountId);
					if (found.data != null) {
						return new PlugResult<>(found.data);
					} else {
						return new PlugResult<>(false, found.errorString());
					}
				} else {
					return new PlugResult<>(false, "expecting a numeric id value");
				}
			} catch (PlugException e) {
				return new PlugResult<>(e.getMessage());
			}
		}
		case "updateAccount": {
			try {
				Account account = gson.fromJson(payload, Account.class);
				if (account != null) {
					AResult<Account> updated = service.updateAccount(account);
					if (updated.data != null) {
						return new PlugResult<>(updated.data);
					} else {
						return new PlugResult<>(false, updated.errorString());
					}
				} else {
					return new PlugResult<>(false, "Could not parse account from payload");
				}
			} catch (PlugException e) {
				return new PlugResult<>(e.getMessage());
			}
		}
		default:
			return new PlugResult<>("Feature specified not found");
		}
	}

	@Override
	public Object getBean(String name) {
		return context.getBean(name);
	}

	@Override
	public void unload() {
		context.close();
		System.out.println("plugin unloaded");
		context = null;
	}
}
