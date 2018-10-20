package works.hop.plugins.web;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import works.hop.plugins.service.PluginService;

@Component
public class PingHandler extends AbstractHandler {
	
	private PluginService service;
	
	public PingHandler(@Autowired PluginService service) {
		super();
		this.service = service;
	}

	public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		response.setContentType("text/html;charset=utf-8");
		response.setStatus(HttpServletResponse.SC_OK);
		baseRequest.setHandled(true);
		boolean available = service.isAvailable("printTasks");
		response.getWriter().println(available);
	}
}
