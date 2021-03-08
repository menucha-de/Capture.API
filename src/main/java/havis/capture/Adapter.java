package havis.capture;

import java.util.Map;

/**
 * Adapter provides a generic interface for connecting different sensor/actor devices.
 * 
 */
public interface Adapter {

    /**
	 * Opens a callback channel between the adapter implementation and its listener.
     * This must be the first method to call by the user.
	 * 
	 * @param listener  Instance of AdapterListener providing defined callback methods         
	 */
	void open(AdapterListener listener) throws AdapterException;

    /**
	 * Closes the callback channel between the adapter implementation and its listener
	 */
	void close() throws AdapterException;

    /**
	 * Retrieves the set of implementation specific configuration properties
     * as a list of key-value-pairs where rhe key is the name of the corresponding property.
	 *    
	 * @return          The requested list of properties.
	 */
	Map<String, String> getProperties() throws AdapterException;

    /**
	 * Set the set of implmentation specific configuration properties as a list
     * of key-value-pairs.
	 * 
	 * @param properties  The list of properties to set.         
	 */
	void setProperties(Map<String, String> properties) throws AdapterException;

    /**
	 * Sets the value configuration propery with name name. 
	 * 
	 * @param name      The name of the property.
     * @param value     The value to set.
	 */
	void setProperty(String name, String value) throws AdapterException;

    /**
	 * Retrives the set of devices as a list of key-value-pairs where
     * the key is the id of the corresponding device.
	 * 
	 * @return          The requested list of devices.
	 */
	Map<String, Device> getDevices() throws AdapterException;

    /**
	 * Retrieves the label for the device with id device.
	 * 
	 * @param device    The id of the requested device.         
	 * @return          The label of the device.
	 */
	String getLabel(String device) throws AdapterException;

    /**
	 * Set the label for the device with id device.
	 * 
	 * @param device    The id of the device.         
	 * @param label     The label to set.
	 */
	void setLabel(String device, String label) throws AdapterException;

    /**
	 * Retrieves the label for the field with id field of the device with id device.
	 * 
	 * @param device    The id of the requested device.
     * @param field     The id of the requested field.
	 * @return          The label of the field.
	 */
	String getLabel(String device, String field) throws AdapterException;

    /**
	 * Set the label for the field with id field fo the device with id device.
	 * 
	 * @param device    The id of the device.
     * @param field    The id of the field.     
	 * @param label     The label to set.
	 */
	void setLabel(String device, String field, String label) throws AdapterException;

    /**
	 * Set the implementation specific device property with name name of the device with id device.
	 * 
	 * @param device    The id of the device.
     * @param name      The name of the property.     
	 * @param value     The value to set.
	 */
	void setProperty(String device, String name, String value) throws AdapterException;

    /**
	 * Set the implementation specific field property with name name of the field with id field
     * of the device with id device.
	 * 
	 * @param device    The id of the device.
     * @param field     The field of the device.
     * @param name      The name of the property.     
	 * @param value     The value to set.
	 */
	void setProperty(String device, String field, String name, String value) throws AdapterException;

    /**
	 * Retrieves the current value of the field with id field of the device with id device
     * from the corresponding physical sensor/actor.
	 * 
	 * @param device    The id of the device.
     * @param field     The id of the field.
	 * @return          The requested value.
	 */
	Object getValue(String device, String field) throws AdapterException;

    /**
	 * Sets the current value of the field with id field of the device with id device
     * on the corresponding physical sensor/actor.
	 * 
	 * @param device    The id of the device.
     * @param field     The id of the field.
     * @param value     The value to set.
	 */
	void setValue(String device, String field, Object value) throws AdapterException;

    /**
	 * Mark the field with id field of the device with id device as observed.
     * Consequently the implementation should start to send FieldValueChangedEvents
     * for this field to the callback methods of the registed AdapterLister.
	 * 
	 * @param device    The id of the device.
     * @param field     The id of the field.
	 */
	void subscribe(String device, String field) throws AdapterException;

    /**
	 * Remove the observe mark from field with id field of the device with id device.
     * Consequently the implementation should stop to send FieldValueChangedEvents
     * for this field to the callback methods of the registed AdapterLister.
	 * 
	 * @param device    The id of the device.
     * @param field     The id of the field.
	 */
	void unsubscribe(String device, String field) throws AdapterException;

    /**
	 * Adds a new device.
	 * 
	 * @param device    The device to add.
     * @return          The generated unique id of the added device.
	 */
	String add(Device device) throws AdapterException;

    /**
	 * Remove the device with id device.
	 * 
	 * @param device    The id of the device.
	 */
	void remove(String device) throws AdapterException;

}