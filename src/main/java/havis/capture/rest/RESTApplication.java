package havis.capture.rest;

import havis.capture.provider.AdapterExceptionMapper;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.core.Application;
import javax.ws.rs.ext.Providers;

public class RESTApplication extends Application {

	private final static String PROVIDERS = Providers.class.getName();
	private final static Map<String, Object> properties = new HashMap<>();
	private Set<Object> singletons = new HashSet<Object>();

	static {
		properties.put(PROVIDERS, new Class<?>[] { AdapterExceptionMapper.class });
	}

	public RESTApplication(Object service) {
		singletons.add(service);
	}

	@Override
	public Set<Object> getSingletons() {
		return singletons;
	}

	@Override
	public Map<String, Object> getProperties() {
		return properties;
	}
}