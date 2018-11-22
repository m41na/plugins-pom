package plugins.poc.web.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;

import works.hop.plugins.api.PlugException;
import works.hop.plugins.api.PlugResult;
import works.hop.plugins.api.Pluggable;
import works.hop.plugins.loader.PluginCentral;

@Service
public class PluginServiceImpl implements PluginService {

	@Autowired
	private PluginCentral controls;
	@Autowired
	private Pluggable plugins;

	@Override
	public boolean isAvailable(String name) {
		return plugins.getSources().stream().anyMatch(t -> t.getPlugin().equals(name));
	}

	@Override
	public List<String> plugsAvailable() {
		Gson gson = new Gson();
		return plugins.getSources().stream().map(e -> gson.toJson(e)).collect(Collectors.toList());
	}

	@Override
	public boolean loadPlugin(String name) {
		try {
			controls.loadPlugin(name);
			return true;
		} catch (Exception e) {
			e.printStackTrace(System.err);
			return false;
		}
	}

	@Override
	public boolean reloadPlugin(String name) {
		try {
			controls.reloadPlugin(name);
			return true;
		} catch (Exception e) {
			e.printStackTrace(System.err);
			return false;
		}
	}

	@Override
	public boolean unloadPlugin(String name) {
		try {
			controls.unloadPlugin(name);
			return true;
		} catch (Exception e) {
			e.printStackTrace(System.err);
			return false;
		}
	}

	//@Override
	public String executePlugin(String name, String feature, String payload) {
		Gson gson = new Gson();
		try {
			PlugResult<?> result = controls.runPlugin(name, feature, payload);
			return gson.toJson(result);
		} catch (Exception e) {
			e.printStackTrace(System.err);
			return gson.toJson(new PlugResult<>(e.getMessage()));
		}
	}

	@Override
	public Object invokePlugin(String name, String feature, Object[] args) {
		try {
			Object proxy = controls.loadPluginProxy(name);
			return proxy.getClass().getMethod(name).invoke(proxy, args);
		} catch (ReflectiveOperationException e) {
			throw new PlugException(e);
		}
	}

	@Override
	public byte[] loadPluginBytes(String name) {
		return controls.loadPluginBytes(name);
	}

	@Override
	public Object createPluginProxy(String name) {
		return controls.loadPluginProxy(name);
	}
}
