package com.practicaldime.plugins.backlog;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.practicaldime.plugins.backlog.config.BackLogDaoConfig;
import com.practicaldime.plugins.backlog.service.BackLogService;

import com.practicaldime.plugins.api.AbstractPlugin;
import com.practicaldime.plugins.api.PlugResult;

public class BackLogPlugin extends AbstractPlugin<BackLogService> {

	private AnnotationConfigApplicationContext context;
	
	public BackLogPlugin() {
		super(BackLogService.class);
	}
	
	@Override
	public void load(ClassLoader loader) {
		context = new AnnotationConfigApplicationContext();
		context.setClassLoader(loader);
		context.register(BackLogDaoConfig.class);
		context.refresh();
		System.out.println("plugin loaded");
		this.service = context.getBean(BackLogService.class);
	}
	
	@Override
	public Object getBean(String name) {
		return context.getBean(name);
	}

	@Override
	public PlugResult<?> execute(String feature, String payload) {
		System.out.println("plugin executing");
		switch (feature) {
		
		default:
			return new PlugResult<>("Feature specified not found");
		}
	}

	@Override
	public void unload() {
		context.close();
		System.out.println("plugin unloaded");
		context = null;
	}
}
