package havis.capture.osgi;

import havis.capture.Adapter;
import havis.capture.AdapterHandler;
import havis.capture.AdapterName;
import havis.capture.AdapterServiceClass;
import havis.capture.AdapterManager;
import havis.capture.rest.AdapterService;
import havis.capture.rest.RESTApplication;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.core.Application;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.PrototypeServiceFactory;
import org.osgi.framework.ServiceRegistration;
import org.osgi.framework.wiring.BundleWiring;

public class Activator implements BundleActivator {

	private final static String NAME = "NAME";
	private final static Logger log = Logger.getLogger(Activator.class.getName());

	private List<ServiceRegistration<AdapterHandler>> handlers = new ArrayList<>();
	private List<ServiceRegistration<Application>> apps = new ArrayList<>();
	private List<AdapterService> services = new ArrayList<>();

	@Override
	public void start(BundleContext context) throws Exception {
		ClassLoader current = Thread.currentThread().getContextClassLoader();
		try {
			Thread.currentThread().setContextClassLoader(context.getBundle().adapt(BundleWiring.class).getClassLoader());

			ServiceLoader<Adapter> loader = ServiceLoader.load(Adapter.class);
			Iterator<Adapter> i = loader.iterator();
			while (i.hasNext()) {
				Adapter adapter = i.next();
				AdapterName name = adapter.getClass().getAnnotation(AdapterName.class);
				if (name != null) {
					final AdapterManager manager = new AdapterManager(adapter);

					Dictionary<String, String> properties = new Hashtable<>();
					properties.put(NAME, name.value());

					log.log(Level.FINE, "Register prototype service factory {0} (''{1}'': ''{2}'')",
							new Object[] { AdapterHandler.class.getName(), NAME, name.value() });
					handlers.add(context.registerService(AdapterHandler.class, new PrototypeServiceFactory<AdapterHandler>() {
						@Override
						public AdapterHandler getService(Bundle bundle, ServiceRegistration<AdapterHandler> registration) {
							return manager.createInstance();
						}

						@Override
						public void ungetService(Bundle bundle, ServiceRegistration<AdapterHandler> registration, AdapterHandler service) {
						}
					}, properties));

					AdapterServiceClass clazz = adapter.getClass().getAnnotation(AdapterServiceClass.class);
					if (clazz != null) {
						Constructor<? extends AdapterService> constructor = clazz.value().getConstructor(AdapterManager.class);
						if (constructor != null) {
							AdapterService service = constructor.newInstance(manager);
							services.add(service);
							apps.add(context.registerService(Application.class, new RESTApplication(service), null));
						} else {
							log.log(Level.FINE, "Missing parameterized constructor for class ''{0}''", clazz.getClass().getName());
						}
					}
				} else {
					log.log(Level.FINE, "Missing adapter name for instance ''{0}''", adapter.getClass().getName());
				}
			}
		} finally {
			Thread.currentThread().setContextClassLoader(current);
		}
	}

	@Override
	public void stop(BundleContext bundleContext) throws Exception {
		for (ServiceRegistration<AdapterHandler> handler : handlers)
			handler.unregister();
		handlers.clear();

		for (ServiceRegistration<Application> app : apps)
			app.unregister();
		apps.clear();

		for (AdapterService service : services)
			service.close();
		services.clear();
	}
}