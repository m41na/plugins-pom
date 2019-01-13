package plugins.poc.web.config;

import javax.servlet.http.HttpServlet;

import org.eclipse.jetty.server.handler.AbstractHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import plugins.poc.web.PingHandler;
import plugins.poc.web.PluginServlet;
import plugins.poc.web.PluginServletAsync;
import plugins.poc.web.service.PluginService;
import plugins.poc.web.service.PluginServiceImpl;
import com.practicaldime.plugins.api.Pluggable;
import com.practicaldime.plugins.config.PlugConfig;
import com.practicaldime.plugins.loader.PluginCentral;

@Configuration
public class AppContext {

	@Bean("pingHandler")
	public AbstractHandler pingHandler(@Autowired PluginService service) {
		return new PingHandler(service);
	}
	
	@Bean
	public HttpServlet pluginServlet(@Autowired PluginService service) {
		return new PluginServlet(service);
	}
	
	@Bean
	public HttpServlet pluginServletAsync(@Autowired PluginService service) {
		return new PluginServletAsync(service);
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
		return new PluginCentral(plugs.getSources());
	}
}
