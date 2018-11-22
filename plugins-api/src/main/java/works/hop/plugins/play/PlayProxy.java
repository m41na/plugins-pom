package works.hop.plugins.play;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

public class PlayProxy implements InvocationHandler{

	private final Map<String, String> map = new HashMap<>();
	
	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		String name = method.getName();
		if(name.equals("getName")) {
			return map.get("name");
		}
		else {
			return map.put("name", (String) args[0]);
		}
	}

	public static Object instance(Class<?> loaded) {
		return Proxy.newProxyInstance(PlayProxy.class.getClassLoader(), new Class<?>[] {loaded}, new PlayProxy());
	}
}
