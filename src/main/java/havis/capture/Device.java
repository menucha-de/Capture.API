package havis.capture;

import java.util.Map;

/**
 * Represents a physical device.
 * 
 */
public class Device {

	private String id;
	private String name;
	private String label;
	private boolean usable;
	private boolean customized;
	private Map<String, String> properties;
	private Map<String, Field> fields;

    /**
	 * Creates a new instance of Device.
	 */
	public Device() {
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
	 * Retrieves the usable state of this Instance.
	 *       
	 * @return          The usable state.
	 */
	public boolean isUsable() {
		return usable;
	}

    /**
	 * Sets the usable state of this Instance.
	 *       
	 * @param usable        The usable state.
	 */
	public void setUsable(boolean usable) {
		this.usable = usable;
	}

	public boolean isCustomized() {
		return customized;
	}

	public void setCustomized(boolean customized) {
		this.customized = customized;
	}

    /**
	 * Retrievs the implementation specific device properties of this Instance
     * as list of key-value-pairs where the key is the name of the property.
	 *       
	 * @return                  The property list.
	 */
	public Map<String, String> getProperties() {
		return properties;
	}

    /**
	 * Sets the implementation specific device properties of this Instance.
	 *       
	 * @param properties        The list of properties to set.
	 */
	public void setProperties(Map<String, String> properties) {
		this.properties = properties;
	}

    /**
	 * Retrievs the fieds of this Instance as list of key-value-pairs 
     * where the key is id of the field.
	 *       
	 * @return                  The field list.
	 */
	public Map<String, Field> getFields() {
		return fields;
	}

    /**
	 * Sets the fields of this Instance-
	 *       
	 * @param field             The list of fields to set.
	 */
	public void setFields(Map<String, Field> fields) {
		this.fields = fields;
	}
}