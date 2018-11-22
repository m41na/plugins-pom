package plugins.poc.web;

import static plugins.poc.web.service.PluginService.EXECUTE_PLUGIN;
import static plugins.poc.web.service.PluginService.IS_AVAILABLE;
import static plugins.poc.web.service.PluginService.LOAD_PLUGIN;
import static plugins.poc.web.service.PluginService.PLUGIN_ACTION_HEADER;
import static plugins.poc.web.service.PluginService.PLUGIN_FEATURE_HEADER;
import static plugins.poc.web.service.PluginService.PLUGIN_NAME_HEADER;
import static plugins.poc.web.service.PluginService.PLUGS_AVAILABLE;
import static plugins.poc.web.service.PluginService.RELOAD_PLUGIN;
import static plugins.poc.web.service.PluginService.UNLOAD_PLUGIN;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;

import plugins.poc.web.service.PluginService;

@Component
public class PluginServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private PluginService service;

	public PluginServlet(@Autowired PluginService service) {
		super();
		this.service = service;
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String json = null;
		String pluginAction = req.getHeader(PLUGIN_ACTION_HEADER);
		String pluginName = req.getHeader(PLUGIN_NAME_HEADER);
		switch (pluginAction) {
		case IS_AVAILABLE:
			boolean available = service.isAvailable(pluginName);
			json = "{\"available\":" + available + "}";
			break;
		case PLUGS_AVAILABLE:
			List<String> result = service.plugsAvailable();
			json = new Gson().toJson(result);
			break;
		default:
			break;
		}

		// write content
		try {
			resp.setContentLength(json.length());
			resp.setContentType("application/json");
			PrintWriter out = resp.getWriter();
			out.write(json);
		} catch (Exception e) {
			try {
				resp.sendError(500, e.getMessage());
			} catch (IOException e1) {
				e1.printStackTrace(System.err);
			}
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		StringBuilder payload = new StringBuilder();
		BufferedReader br = req.getReader();
		String str;
		while ((str = br.readLine()) != null) {
			payload.append(str);
		}

		// process request
		String json = null;
		String pluginAction = req.getHeader(PLUGIN_ACTION_HEADER);
		String pluginName = req.getHeader(PLUGIN_NAME_HEADER);
		String pluginFeature = req.getHeader(PLUGIN_FEATURE_HEADER);
		if (pluginAction != null) {
			switch (pluginAction) {
			case LOAD_PLUGIN:
				boolean loaded = service.loadPlugin(pluginName);
				json = "{\"loaded\":" + loaded + "}";
				break;
			case RELOAD_PLUGIN:
				boolean reloaded = service.reloadPlugin(pluginName);
				json = "{\"reloaded\":" + reloaded + "}";
				break;
			case UNLOAD_PLUGIN:
				boolean unloaded = service.unloadPlugin(pluginName);
				json = "{\"unloaded\":" + unloaded + "}";
				break;
			case EXECUTE_PLUGIN:
				json = service.executePlugin(pluginName, pluginFeature, payload.toString());
				break;
			default:
				break;
			}
			// set ok status
			resp.setStatus(200);
		} else {
			json = "{\"success\": \"false\", \"status\":\"The 'xh-plugin-action' header value is missing\"}";
			resp.setStatus(403);
		}

		// write content
		try {
			resp.setContentLength(json.length());
			resp.setContentType("application/json");
			PrintWriter out = resp.getWriter();
			out.write(json);
		} catch (Exception e) {
			try {
				resp.sendError(500, e.getMessage());
			} catch (IOException e1) {
				e1.printStackTrace(System.err);
			}
		}
	}
}
