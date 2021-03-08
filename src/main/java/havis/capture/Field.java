package havis.capture;

import java.util.Map;

/**
 * Represents a field of a physical device.
 * 
 */
public class Field {

	private String id;
	private String name;
	private String label;
	private Map<String, String> properties;

    /**
	 * Creates a new instance of Field.
	 */
	public Field() {
	}

    /**
	 * Retrieves the unique id of this Instance.
	 *       
	 * @return          The unique id.
	 */
	public String getId() {
		return id;
	}

    /**
	 * Sets the unique id of this Instance.
	 *       
	 * @param id        The unique id to set.
	 */
	public void setId(String id) {
		this.id = id;
	}

    /**
	 * Retrieves the name of this Instance.
	 *       
	 * @return          The name.
	 */
	public String getName() {
		return name;
	}

    /**
	 * Sets the name of this Instance.
	 *       
	 * @param name        The name.
	 */
	public void setName(String name) {
		this.name = name;
	}

    /**
	 * Retrieves the label of this Instance.
	 *       
	 * @return          The label.
	 */
	public String getLabel() {
		return label;
	}

    /**
	 * Sets the label of this Instance.
	 *       
	 * @param name        The label.
	 */
	public void setLabel(String label) {
		this.label = label;
	}

    /**
	 * Retrievs the implementation specific field properties of this Instance
     * as list of key-value-pairs where the key is the name of the property.
	 *       
	 * @return                  The property list.
	 */
	public Map<String, String> getProperties() {
		return properties;
	}

    /**
	 * Sets the implementation specific field properties of this Instance.
	 *       
	 * @param properties        The list of properties to set.
	 */
	public void setProperties(Map<String, String> properties) {
		this.properties = properties;
	}
}