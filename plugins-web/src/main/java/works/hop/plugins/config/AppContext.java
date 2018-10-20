package works.hop.plugins.config;

import javax.servlet.http.HttpServlet;

import org.eclipse.jetty.server.handler.AbstractHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import works.hop.plugins.api.Pluggable;
import works.hop.plugins.loader.PluginCentral;
import works.hop.plugins.service.PluginService;
import works.hop.plugins.service.PluginServiceImpl;
import works.hop.plugins.web.PingHandler;
import works.hop.plugins.web.PluginServlet;

@Configuration
public class AppContext {

	@Bean("pingHandler")
	public AbstractHandler pingHandler(@Autowired PluginService service) {
		return new PingHandler(service);
	}
	
	@Bean("pluginServlet")
	public HttpServlet pluginServlet(@Autowired PluginService service) {
		return new PluginServlet(service);
	}
	
	@Bean
	public PluginService pluginService() {
		return new PluginServiceImpl();
	}
	
	@Bean
	public Pluggable pluggable() {
		return PlugConfig.getInstance().loadConfig();
	}
	
	@Bean
	public PluginCentral pluginCentral(@Autowired Pluggable plugs) {
		ClassLoader parentCl = PluginCentral.class.getClassLoader();
		return new PluginCentral(parentCl, plugs.getSources());
	}
}
