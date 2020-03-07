package plugins.poc.web.config;

import com.practicaldime.plugins.api.PlugDefinition;
import com.practicaldime.plugins.config.PlugConfig;
import com.practicaldime.plugins.loader.PluginCentral;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import plugins.poc.web.PingHandler;
import plugins.poc.web.PluginServlet;
import plugins.poc.web.PluginServletAsync;
import plugins.poc.web.service.PluginService;
import plugins.poc.web.service.PluginServiceImpl;

import javax.servlet.http.HttpServlet;

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
    public PlugDefinition pluggable() {
        return PlugConfig.getInstance().loadConfig();
    }

    @Bean
    public PluginCentral pluginCentral(@Autowired PlugDefinition plugs) {
        return new PluginCentral(plugs.getDefinitions());
    }
}
