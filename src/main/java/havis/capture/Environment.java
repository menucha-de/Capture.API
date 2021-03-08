package havis.capture;

import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Environment {

	private final static Logger log = Logger.getLogger(Environment.class.getName());
	private final static Properties properties = new Properties();

	static {
		try (InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream("havis.capture.adapter.properties")) {
			if (stream != null) {
				properties.load(stream);
			}
		} catch (Exception e) {
			log.log(Level.SEVERE, "Failed to load environment properties", e);
		}
	}
}