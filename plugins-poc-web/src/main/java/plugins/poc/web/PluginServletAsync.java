package plugins.poc.web;

import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import plugins.poc.web.service.PluginService;

import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import static plugins.poc.web.service.PluginService.*;

@Component
public class PluginServletAsync extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private PluginService service;

    public PluginServletAsync(@Autowired PluginService service) {
        super();
        this.service = service;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        final AsyncContext async = req.startAsync();

        ServletOutputStream out = resp.getOutputStream();
        out.setWriteListener(new WriteListener() {

            @Override
            public void onWritePossible() throws IOException {
                CompletableFuture.runAsync(() -> {
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

                    ByteBuffer content = ByteBuffer.wrap(json.getBytes(StandardCharsets.UTF_8));
                    while (out.isReady()) {
                        if (!content.hasRemaining()) {
                            resp.setStatus(200);
                            resp.setContentType("application/json");
                            async.complete();
                            return;
                        }
                        // write to response
                        try {
                            out.write(content.get());
                        } catch (IOException e) {
                            throw new CompletionException(e);
                        }
                    }
                });
            }

            @Override
            public void onError(Throwable t) {
                getServletContext().log("Async error", t);
                async.complete();
            }
        });
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        final AsyncContext async = req.startAsync();

        final ServletInputStream input = req.getInputStream();
        input.setReadListener(new ReadListener() {

            byte[] buffer = new byte[4 * 1024];
            StringBuilder payload = new StringBuilder();

            @Override
            public void onDataAvailable() throws IOException {
                int len = -1;
                while (input.isReady() && (len = input.read(buffer)) != -1) {
                    payload.append(new String(buffer, 0, len));
                }
            }

            @Override
            public void onAllDataRead() throws IOException {
                CompletableFuture.runAsync(() -> {
                    try {
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

                        ByteBuffer content = ByteBuffer.wrap(json.toString().getBytes(StandardCharsets.UTF_8));

                        ServletOutputStream out = resp.getOutputStream();
                        out.setWriteListener(new WriteListener() {

                            @Override
                            public void onWritePossible() throws IOException {
                                while (out.isReady()) {
                                    if (!content.hasRemaining()) {
                                        resp.setStatus(200);
                                        resp.setContentType("application/json");
                                        async.complete();
                                        return;
                                    }
                                    // write to response
                                    out.write(content.get());

                                }
                            }

                            @Override
                            public void onError(Throwable t) {
                                getServletContext().log("Async error", t);
                                async.complete();
                            }
                        });
                    } catch (IOException e) {
                        throw new CompletionException(e);
                    }
                });
            }

            @Override
            public void onError(Throwable t) {
                t.printStackTrace(System.err);
                async.complete();
            }
        });
    }
}
