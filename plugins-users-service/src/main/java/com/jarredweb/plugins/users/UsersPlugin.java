package com.jarredweb.plugins.users;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.google.gson.Gson;
import com.jarredweb.common.util.AppResult;
import com.jarredweb.domain.users.Account;
import com.jarredweb.plugins.users.config.UsersDaoConfig;
import com.jarredweb.plugins.users.service.UserService;

import works.hop.plugins.api.AbstractPlugin;
import works.hop.plugins.api.PlugException;
import works.hop.plugins.api.PlugResult;

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
					AppResult<String> created = service.createAccount(account);
					if (created.getEntity() != null) {
						return new PlugResult<>(created.getEntity());
					} else {
						return new PlugResult<>(false, created.getError());
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
				AppResult<Account> found = null;
				if (payload.matches("^\\d+$")) {
					Long accountId = Long.valueOf("accountId");
					found = service.getAccount(accountId);
				} else {
					found = service.getAccount(payload);
				}
				if (found.getEntity() != null) {
					return new PlugResult<>(found.getEntity());
				} else {
					return new PlugResult<>(false, found.getError());
				}
			} catch (PlugException e) {
				return new PlugResult<>(e.getMessage());
			}
		}
		case "getAccountByEmail": {
			try {
				AppResult<Account> found = service.getAccountByEmail(payload);
				if (found.getEntity() != null) {
					return new PlugResult<>(found.getEntity());
				} else {
					return new PlugResult<>(false, found.getError());
				}
			} catch (PlugException e) {
				return new PlugResult<>(e.getMessage());
			}
		}
		case "fetchPassword": {
			try {
				if (payload.matches("^\\d+$")) {
					Long accountId = Long.valueOf("accountId");
					AppResult<char[]> found = service.fetchPassword(accountId);
					if (found.getEntity() != null) {
						return new PlugResult<>(found.getEntity());
					} else {
						return new PlugResult<>(false, found.getError());
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
					AppResult<char[]> found = service.resetPassword(accountId);
					if (found.getEntity() != null) {
						return new PlugResult<>(found.getEntity());
					} else {
						return new PlugResult<>(false, found.getError());
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
					AppResult<char[]> found = service.resetPassword(accountId);
					if (found.getEntity() != null) {
						return new PlugResult<>(found.getEntity());
					} else {
						return new PlugResult<>(false, found.getError());
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
					AppResult<Account> updated = service.updateAccount(account);
					if (updated.getEntity() != null) {
						return new PlugResult<>(updated.getEntity());
					} else {
						return new PlugResult<>(false, updated.getError());
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
