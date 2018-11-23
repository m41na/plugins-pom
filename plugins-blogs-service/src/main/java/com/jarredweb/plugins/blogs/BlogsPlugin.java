package com.jarredweb.plugins.blogs;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.jarredweb.plugins.blogs.config.BlogsDaoConfig;
import com.jarredweb.plugins.blogs.service.BlogsService;

import works.hop.plugins.api.AbstractPlugin;
import works.hop.plugins.api.PlugResult;

public class BlogsPlugin extends AbstractPlugin<BlogsService> {

	private AnnotationConfigApplicationContext context;
	
	public BlogsPlugin() {
		super(BlogsService.class);
	}
	
	@Override
	public void load(ClassLoader loader) {
		context = new AnnotationConfigApplicationContext();
		context.setClassLoader(loader);
		context.register(BlogsDaoConfig.class);
		context.refresh();
		System.out.println("plugin loaded");
		this.service = context.getBean(BlogsService.class);
	}

	@Override
	public Object invoke(String feature, Class<?>[] params, Object[] args) throws ReflectiveOperationException {
		System.out.println("plugin executing");
		return type.getClass().getMethod(feature, params).invoke(service, args);
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
