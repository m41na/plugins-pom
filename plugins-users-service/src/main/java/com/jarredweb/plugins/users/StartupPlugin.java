package com.jarredweb.plugins.users;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.jarredweb.domain.users.Account;
import com.jarredweb.plugins.users.config.UsersServiceConfig;
import com.jarredweb.plugins.users.service.StartupService;

import works.hop.plugins.api.AbstractPlugin;
import works.hop.plugins.api.PlugException;
import works.hop.plugins.api.PlugResult;

public class StartupPlugin extends AbstractPlugin<StartupService> {

	private AnnotationConfigApplicationContext context;
	
	public StartupPlugin() {
		super(StartupService.class);
	}
	
	@Override
	public void load(ClassLoader loader) {
		context = new AnnotationConfigApplicationContext();
		context.setClassLoader(loader);
		context.register(UsersServiceConfig.class);
		context.refresh();
		System.out.println("plugin loaded");
		this.service = context.getBean(StartupService.class);
	}

	@Override
	public PlugResult<?> execute(String feature, String payload) {
		System.out.println("plugin executing");
		switch (feature) {
		case "initialize":
			try {
				service.initialize();
				return new PlugResult<>(0);
			} catch (PlugException e) {
				return new PlugResult<>(e.getMessage());
			}
		case "getUserAccount":
			try {
				Account account = service.getUserAccount(payload);
				return new PlugResult<>(account);
			} catch (PlugException e) {
				return new PlugResult<>(e.getMessage());
			}
		case "onInitialized":
			try {
				service.onInitialized();
				return new PlugResult<>(0);
			} catch (PlugException e) {
				return new PlugResult<>(e.getMessage());
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
