package havis.capture;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * Represents the event that should be raised when a field that is marked
 * as obsered changed its value or its value is captured.
 * It provides information about which field of which device and the value.
 * 
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class", visible = true)
public class FieldValueChangedEvent extends FieldStateChangedEvent {

	private Object value;

	public FieldValueChangedEvent() {
	}

	public FieldValueChangedEvent(String device, String field, Object value) {
		super(device, field);
		this.value = value;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}
}