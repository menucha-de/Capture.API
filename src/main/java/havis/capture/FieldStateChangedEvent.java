package havis.capture;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
@JsonSubTypes({ @JsonSubTypes.Type(value = FieldUsabilityChangedEvent.class), @JsonSubTypes.Type(value = FieldValueChangedEvent.class) })
public class FieldStateChangedEvent extends DeviceStateChangedEvent {

	private String field;

	public FieldStateChangedEvent() {
	}

	public FieldStateChangedEvent(String device, String field) {
		super(device);
		this.field = field;
	}

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}
}