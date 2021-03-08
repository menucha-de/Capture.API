package havis.capture;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.fasterxml.jackson.databind.ObjectMapper;

public class AdapterConfiguration {

	public static class Container {
		public ConcurrentHashMap<String, String> properties;
		public ConcurrentHashMap<String, Device> devices;
	}

	private ObjectMapper mapper = new ObjectMapper();

	private String filename;
	private Container config;

	/**
	 * Required for serialization
	 */
	public AdapterConfiguration() {

	}

	public AdapterConfiguration(String filename) {
		this.filename = filename;
		this.config = new Container();
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public Map<String, String> getProperties() throws AdapterException {
		if (config.properties == null) {
			config.properties = new ConcurrentHashMap<String, String>();
		}
		return config.properties;
	}

	public void setProperties(Map<String, String> properties) throws AdapterException {
		if (properties != null) {
			this.config.properties = new ConcurrentHashMap<String, String>(properties);
		} else {
			this.config.properties = null;
		}
		serialize();
	}

	public void setProperty(String name, String value) throws AdapterException {
		getProperties().put(name, value);
		serialize();
	}

	public Map<String, Device> getDevices() throws AdapterException {
		if (config.devices == null) {
			config.devices = new ConcurrentHashMap<String, Device>();
		}
		return config.devices;
	}

	public void setDevices(Map<String, Device> devices) {
		if (devices != null) {
			this.config.devices = new ConcurrentHashMap<String, Device>(devices);
		} else {
			this.config.devices = null;
		}
	}

	/**
	 * 
	 * @param device
	 *            The device ID
	 * @return The device label
	 * @throws AdapterException
	 *             If the device does not exist
	 */
	public String getLabel(String device) throws AdapterException {
		Device d = null;
		if (device != null) {
			d = getDevices().get(device);
		}
		if (d == null) {
			throw new AdapterException("Device with id '" + device + "' does not exist.");
		} else {
			return d.getLabel();
		}
	}

	/**
	 * Sets the device label. If label is successfully set the configuration
	 * will be serialized.
	 * 
	 * @param device
	 *            The device ID
	 * @param label
	 *            The new device label
	 * @throws AdapterException
	 *             If the device does not exist or the serialization failed.
	 */
	public void setLabel(String device, String label) throws AdapterException {
		Device d = null;
		if (device != null) {
			d = getDevices().get(device);
		}
		if (d == null) {
			throw new AdapterException("Device with id '" + device + "' does not exist.");
		} else {
			d.setLabel(label);
			d.setCustomized(true);
		}
		serialize();
	}

	/**
	 * 
	 * @param device
	 *            The device ID
	 * @param field
	 *            The field ID
	 * @return The field label
	 * @throws AdapterException
	 *             If the device or field does not exist
	 */
	public String getLabel(String device, String field) throws AdapterException {
		Device d = null;
		if (device != null) {
			d = getDevices().get(device);
		}
		if (d == null) {
			throw new AdapterException("Device with id '" + device + "' does not exist.");
		} else {
			if (field != null && d.getFields() != null && d.getFields().containsKey(field) && d.getFields().get(field) != null) {
				return d.getFields().get(field).getLabel();
			} else {
				throw new AdapterException("Field with id '" + field + "' does not exist.");
			}
		}
	}

	/**
	 * Sets the field label. If label is successfully set the configuration will
	 * be serialized.
	 * 
	 * @param device
	 *            The device ID
	 * @param field
	 *            The field ID
	 * @param label
	 *            The field label
	 * @throws AdapterException
	 *             If the device or field does not exist or the serialization
	 *             failed.
	 */
	public void setLabel(String device, String field, String label) throws AdapterException {
		Device d = null;
		if (device != null) {
			d = getDevices().get(device);
		}
		if (d == null) {
			throw new AdapterException("Device with id '" + device + "' does not exist.");
		} else {
			if (field != null && d.getFields() != null && d.getFields().containsKey(field) && d.getFields().get(field) != null) {
				d.getFields().get(field).setLabel(label);
				d.setCustomized(true);
			} else {
				throw new AdapterException("Field with id '" + field + "' does not exist.");
			}
		}
		serialize();
	}

	/**
	 * Sets the device property. If property is successfully set the
	 * configuration will be serialized.
	 * 
	 * @param device
	 *            The device ID
	 * @param name
	 *            The property name
	 * @param value
	 *            The property value
	 * @throws AdapterException
	 *             If the device does not exist or the serialization failed.
	 */
	public void setProperty(String device, String name, String value) throws AdapterException {
		Device d = null;
		if (device != null) {
			d = getDevices().get(device);
		}
		if (d == null) {
			throw new AdapterException("Device with id '" + device + "' does not exist.");
		} else {
			if (name == null) {
				throw new AdapterException("NULL is not a valid key.");
			}
			if (d.getProperties() == null) {
				d.setProperties(new HashMap<String, String>());
			}
			getDevices().get(device).getProperties().put(name, value);
			d.setCustomized(true);
		}
		serialize();
	}

	/**
	 * Sets the field property. If property is successfully set the
	 * configuration will be serialized.
	 * 
	 * @param device
	 *            The device ID
	 * @param field
	 *            The field ID
	 * @param name
	 *            The property name
	 * @param value
	 *            The property value
	 * @throws AdapterException
	 *             If the device or field does not exist or the serialization
	 *             failed.
	 */
	public void setProperty(String device, String field, String name, String value) throws AdapterException {
		Device d = null;
		if (device != null) {
			d = getDevices().get(device);
		}
		if (d == null) {
			throw new AdapterException("Device with id '" + device + "' does not exist.");
		} else {
			if (field != null && d.getFields() != null && d.getFields().containsKey(field) && d.getFields().get(field) != null) {
				if (d.getFields().get(field).getProperties() == null) {
					d.getFields().get(field).setProperties(new HashMap<String, String>());
				}
				if (name == null) {
					throw new AdapterException("NULL is not a valid key.");
				}
				d.getFields().get(field).getProperties().put(name, value);
				d.setCustomized(true);
			} else {
				throw new AdapterException("Field with id '" + field + "' does not exist.");
			}
		}
		serialize();
	}

	/**
	 * Note: AdapterConfiguration will not automatically be serialized.
	 * 
	 * @param device
	 *            The device object
	 * @throws AdapterException
	 *             If device or device ID is null
	 */
	public void add(Device device) throws AdapterException {
		if (device == null) {
			throw new AdapterException("Device must not be null.");
		}
		if (device.getId() == null) {
			throw new AdapterException("Device ID must not be null.");
		}
		getDevices().put(device.getId(), device);
	}

	/**
	 * Removes the device from the configuration. Device should not be removed
	 * by adapter if an user/app has customized the device.
	 * 
	 * Note: AdapterConfiguration will not automatically be serialized.
	 * 
	 * @param device
	 *            The ID of the device.
	 * @throws AdapterException
	 *             Should not be thrown
	 */
	public void remove(String device) throws AdapterException {
		if (device != null) {
			getDevices().remove(device);
		}
	}

	/**
	 * Serializes the AdapterConfiguration
	 * 
	 * @throws AdapterException
	 *             If serialization failed.
	 */
	public synchronized void serialize() throws AdapterException {
		File tmpFile = null;
		try {
			File destination = new File(filename);
			File parent = destination.getParentFile();
			if (parent != null) {
				// create parent directory
				parent.mkdirs();
			}

			// creation of temporary backup file
			tmpFile = File.createTempFile(filename, ".bak", parent);

			// writing configuration to temporary backup file
			mapper.writerWithDefaultPrettyPrinter().writeValue(tmpFile, config);

			// Replacing deprecated configuration by new configuration file
			if (!tmpFile.renameTo(destination)) {
				throw new Exception("Replacing " + destination.getAbsolutePath() + " with '" + tmpFile.getAbsolutePath() + "' failed.");
			}
		} catch (Exception e) {
			// delete temporary file
			if (tmpFile != null && tmpFile.exists()) {
				tmpFile.delete();
			}
			throw new AdapterException("Failed to persist config", e);
		}
	}

	private synchronized void load(File configFile) throws AdapterException {
		try {
			config = mapper.readValue(configFile, Container.class);
			setFilename(filename);
		} catch (IOException e) {
			throw new AdapterException("Failed to load config.", e);
		}
	}

	/**
	 * Trying to deserialize the configuration at the specified location.
	 * 
	 * @param filename
	 *            The location of the configuration.
	 * @return The configuration of the specified location. If file does not
	 *         exist a new instance will be returned
	 * @throws AdapterException
	 *             If deserialization failed.
	 */
	public static synchronized AdapterConfiguration deserialize(String filename) throws AdapterException {
		File configFile = new File(filename);
		if (configFile.exists()) {
			AdapterConfiguration config = new AdapterConfiguration(filename);
			config.load(configFile);
			return config;
		}
		return new AdapterConfiguration(filename);
	}

}
