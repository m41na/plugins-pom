package works.graphql.config;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import works.graphql.dao.LinkRepository;

@Configuration
public class AppContext {

	private AnnotationConfigApplicationContext ctx;
	
	public void init() {
		ctx = new AnnotationConfigApplicationContext("works.graphql");
	}
	
	@Bean
	public LinkRepository getLinkRepository() {
		return new LinkRepository();
	}
	
	@SuppressWarnings("unchecked")
	public <T>T getBean(String name, Class<T> type){
		if(type == null) {
			return (T)ctx.getBean(name);
		}
		else if(name == null) {
			return ctx.getBean(type);
		}
		else {
			return ctx.getBean(name, type);
		}
	}
}
