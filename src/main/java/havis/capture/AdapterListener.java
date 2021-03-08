package havis.capture;

/**
 * AdapterListener provides the callback interface for the Adapter interface.
 * 
 */
public interface AdapterListener {

    /**
	 * Signals that the usability of a device has changed.
	 * 
	 * @param source    Instance of the Adapter that send the event.
     * @param event     The event containing all needed information.
	 */
	void usabilityChanged(Adapter source, DeviceUsabilityChangedEvent event);

    /**
	 * Signals that the usability of a field within a device has changed.
	 * 
	 * @param source    Instance of the Adapter that send the event.
     * @param event     The event containing all needed information.
	 */
	void usabilityChanged(Adapter source, FieldUsabilityChangedEvent event);

    /**
	 * Signals that the value of a field within a device has changed or was captured.
	 * 
	 * @param source    Instance of the Adapter that send the event.
     * @param event     The event containing all needed information.
	 */
	void valueChanged(Adapter source, FieldValueChangedEvent event);
}