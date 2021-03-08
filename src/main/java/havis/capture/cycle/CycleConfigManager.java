package havis.capture.cycle;

import havis.capture.AdapterException;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;

public class CycleConfigManager {

	private final static Logger LOG = Logger.getLogger(CycleConfigManager.class.getName());

	private final static ObjectMapper MAPPER = new ObjectMapper();

	private CycleConfiguration instance;

	public CycleConfiguration getConfiguration() {
		return instance;
	}

	private String filename;

	public CycleConfigManager(String filename) {
		this.filename = filename;
		try {
			instance = deserialize();
		} catch (AdapterException e) {
			LOG.log(Level.SEVERE, "Failed to deserialize Cycle config.", e);
			instance = new CycleConfiguration();
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
				if (!parent.mkdirs() && !parent.exists()) {
					LOG.warning("Failed to create parent directory '" + parent.getAbsolutePath() + "'.");
				}
			}

			// creation of temporary backup file
			tmpFile = File.createTempFile(filename, ".bak", parent);
			LOG.fine("Created temporary file '" + tmpFile.getAbsolutePath() + "'.");

			// writing configuration to temporary backup file
			MAPPER.writerWithDefaultPrettyPrinter().writeValue(tmpFile, instance);

			// Replacing deprecated configuration by new configuration file
			if (tmpFile.renameTo(destination)) {
				LOG.fine("Replaced configuration file.");
			} else {
				throw new Exception("Replacing " + destination.getAbsolutePath() + " with '" + tmpFile.getAbsolutePath()
						+ "' failed.");
			}
		} catch (Exception e) {
			LOG.log(Level.SEVERE, "Failed to persist config", e);
			// delete temporary file
			if (tmpFile != null && tmpFile.exists()) {
				tmpFile.delete();
			}
			throw new AdapterException("Failed to persist config", e);
		}
	}

	public synchronized CycleConfiguration deserialize() throws AdapterException {
		File configFile = new File(filename);
		if (configFile.exists()) {
			try {
				CycleConfiguration config = MAPPER.readValue(configFile, CycleConfiguration.class);
				return config;
			} catch (Exception e) {
				throw new AdapterException("Failed to load config.", e);
			}
		} else {
			LOG.fine("Config '" + filename + "' does not exist.");
		}
		return new CycleConfiguration();
	}

}
