package havis.capture;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * Represents the event that should be raised when a field becomes usable or unusable.
 * It provides information about which field of which device and the usable state.
 * 
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public class FieldUsabilityChangedEvent extends FieldStateChangedEvent {

	private boolean usable;

	public FieldUsabilityChangedEvent() {
	}

	public FieldUsabilityChangedEvent(String device, String field, boolean usable) {
		super(device, field);
		this.usable = usable;
	}

	public boolean isUsable() {
		return usable;
	}

	public void setUsable(boolean usable) {
		this.usable = usable;
	}
}