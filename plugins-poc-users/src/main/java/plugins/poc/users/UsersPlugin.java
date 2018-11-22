package plugins.poc.users;

import java.lang.reflect.Type;
import java.util.Map;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import plugins.poc.users.config.DaoConfig;
import plugins.poc.users.dao.User;
import plugins.poc.users.dao.UsersService;
import works.hop.plugins.api.AbstractPlugin;
import works.hop.plugins.api.PlugException;
import works.hop.plugins.api.PlugResult;

public class UsersPlugin extends AbstractPlugin<UsersService> {

	private AnnotationConfigApplicationContext context;
	
	public UsersPlugin() {
		super(UsersService.class);
	}
	
	@Override
	public void load(ClassLoader loader) {
		context = new AnnotationConfigApplicationContext();
		context.setClassLoader(loader);
		context.register(DaoConfig.class);
		context.refresh();
		System.out.println("plugin loaded");
		this.service = context.getBean(UsersService.class);
	}

	@Override
	public PlugResult<?> execute(String feature, String payload) {
		System.out.println("plugin executing");
		Gson gson = new Gson();
		switch (feature) {
		case "createUser":
			try {
				User newUser = gson.fromJson(payload, User.class);
				if (newUser != null) {
					try {
						return service.create(newUser);
					} catch (Exception e) {
						return new PlugResult<>(e.getMessage());
					}
				} else {
					throw new PlugException("Could not read user data from json payload");
				}
			} catch (PlugException e) {
				return new PlugResult<>(e.getMessage());
			}
		case "findUser":
			try {
				Type mapType = new TypeToken<Map<String, Long>>() {
				}.getType();
				Map<String, Long> map = gson.fromJson(payload, mapType);
				return service.find(map.get("id"));
			} catch (PlugException e) {
				return new PlugResult<>(e.getMessage());
			}
		case "retrieveUsers":
			try {
				Type mapType = new TypeToken<Map<String, Integer>>() {
				}.getType();
				Map<String, Integer> map = gson.fromJson(payload, mapType);
				return service.findUsers(map.get("start"), map.get("size"));
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
