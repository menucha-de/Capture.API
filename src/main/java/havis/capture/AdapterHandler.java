package havis.capture;

import java.util.Map;

public interface AdapterHandler extends AutoCloseable {

	/**
	 * Sets the listener to receive device, field, and value state changes
	 * 
	 * @param listener
	 *            The listener
	 */
	void setListener(AdapterListener listener) throws AdapterException;

	/**
	 * Gets the current device list
	 * 
	 * @return The devices
	 */
	Map<String, Device> getDevices() throws AdapterException;

	/**
	 * Adds a device to adapter
	 * 
	 * @param device
	 *            The device to add
	 */
	String add(Device device) throws AdapterException;

	/**
	 * Remove a device from adapter
	 * 
	 * @param device
	 *            The device to add
	 */
	void remove(String device) throws AdapterException;

	/**
	 * Gets the device label
	 * 
	 * @param device
	 *            The device id
	 * @return The device label
	 */
	String getLabel(String device) throws AdapterException;

	/**
	 * Gets the device field label
	 * 
	 * @param device
	 *            The device id
	 * @param field
	 *            The field id
	 * @return The device field label
	 */
	String getLabel(String device, String field) throws AdapterException;

	/**
	 * Sets the device label
	 * 
	 * @param device
	 *            The device id
	 * @param value
	 *            The device label
	 */
	void setLabel(String device, String value) throws AdapterException;

	/**
	 * Sets the device field label
	 * 
	 * @param device
	 *            The device id
	 * @param field
	 *            The field id
	 * @param value
	 *            The device field label
	 */
	void setLabel(String device, String field, String value) throws AdapterException;

	/**
	 * Sets a device property
	 * 
	 * @param device
	 *            The device id
	 * @param name
	 *            The property name
	 * @param value
	 *            The property value
	 */
	void setProperty(String device, String name, String value) throws AdapterException;

	/**
	 * Sets a field property
	 * 
	 * @param device
	 *            The device id
	 * @param field
	 *            The field id
	 * @param name
	 *            The property name
	 * @param value
	 *            The property value
	 */
	void setProperty(String device, String field, String name, String value) throws AdapterException;

	/**
	 * Gets the device field value
	 * 
	 * @param device
	 *            The device id
	 * @param field
	 *            The field id
	 * @return The device field value
	 */
	Object getValue(String device, String field) throws AdapterException;

	/**
	 * Sets the device field value
	 * 
	 * @param device
	 *            The device id
	 * @param field
	 *            The field id
	 * @param value
	 *            The device field value
	 */
	void setValue(String device, String field, Object value) throws AdapterException;

	/**
	 * Subscribes to a device field
	 * 
	 * @param device
	 *            The device id
	 * @param field
	 *            The field id
	 */
	void subscribe(String device, String field) throws AdapterException;

	/**
	 * Unsubscribes from a device field
	 * 
	 * @param device
	 *            The device id
	 * @param field
	 *            The field id
	 */
	void unsubscribe(String device, String field) throws AdapterException;
}